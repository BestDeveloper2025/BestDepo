package com.bestmakina.depotakip.domain.manager

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.os.Bundle
import android.util.Log
import com.bestmakina.depotakip.common.model.NfcStatus
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * NFC/RFID işlemlerini yöneten manager sınıfı
 */
@Singleton
class NfcManager @Inject constructor(
    private val nfcAdapter: NfcAdapter?
) {
    private val _nfcStatus = MutableStateFlow<NfcStatus>(NfcStatus.NotInitialized)
    val nfcStatus: StateFlow<NfcStatus> = _nfcStatus

    // NFC bağlantı durumunu takip etmek için
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    // Eşzamanlı erişimi kontrol etmek için mutex
    private val nfcMutex = Mutex()

    // Log tag
    private val TAG = "NfcManager"

    init {
        checkNfcAvailability()
    }

    /**
     * NFC adaptörünün durumunu kontrol eder
     */
    private fun checkNfcAvailability() {
        _nfcStatus.value = when {
            nfcAdapter == null -> NfcStatus.NotSupported
            !nfcAdapter.isEnabled -> NfcStatus.Disabled
            else -> NfcStatus.Available
        }
    }

    /**
     * Activity'nin foreground dispatch sistemini başlatır
     */
    fun enableForegroundDispatch(activity: Activity, intent: PendingIntent) {
        nfcAdapter?.enableForegroundDispatch(
            activity,
            intent,
            null,
            null
        )
    }

    /**
     * Activity'nin foreground dispatch sistemini durdurur
     */
    fun disableForegroundDispatch(activity: Activity) {
        nfcAdapter?.disableForegroundDispatch(activity)
    }

    /**
     * RFID çipinden veri okur
     * Mutex ile senkronize edilmiş ve bağlantı yönetimi iyileştirilmiş
     */
    suspend fun readFromTag(tag: Tag): Result<String> = withContext(Dispatchers.IO) {
        // Mutex ile senkronizasyon sağlıyoruz
        nfcMutex.withLock {
            Log.d(TAG, "Attempting to read from tag")

            val ndef = Ndef.get(tag) ?: return@withLock Result.failure(
                IOException("Tag is not NDEF compatible")
            )

            try {
                // Varolan bir bağlantı kontrolü ve temizleme
                if (ndef.isConnected) {
                    Log.d(TAG, "Found existing connection, closing first")
                    try {
                        ndef.close()
                        delay(100) // Bağlantının kapanması için kısa bir bekleme
                    } catch (e: Exception) {
                        Log.e(TAG, "Error closing existing connection: ${e.message}")
                    }
                }

                // Bağlantı durumunu güncelle
                _isConnected.value = true
                Log.d(TAG, "Connecting to tag")

                ndef.connect()

                if (!ndef.isConnected) {
                    _isConnected.value = false
                    return@withLock Result.failure(IOException("Could not connect to tag"))
                }

                val ndefMessage = ndef.cachedNdefMessage ?: return@withLock Result.failure(
                    IOException("No NDEF messages found")
                )

                val records = ndefMessage.records
                if (records.isEmpty()) {
                    return@withLock Result.failure(IOException("No NDEF records found"))
                }

                val payload = records[0].payload
                val data = String(payload, 3, payload.size - 3, Charsets.UTF_8)

                Log.d(TAG, "Successfully read data: $data")
                Result.success(data)
            } catch (e: Exception) {
                Log.e(TAG, "Error reading from tag: ${e.message}")
                Result.failure(e)
            } finally {
                try {
                    if (ndef.isConnected) {
                        Log.d(TAG, "Closing NFC connection")
                        ndef.close()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error closing NFC connection: ${e.message}")
                }
                _isConnected.value = false
                Log.d(TAG, "NFC connection state reset")
            }
        }
    }

    /**
     * RFID çipine veri yazar
     * Mutex ile senkronize edilmiş ve bağlantı yönetimi iyileştirilmiş
     */
    suspend fun writeToTag(tag: Tag, data: String): Result<Unit> = withContext(Dispatchers.IO) {
        nfcMutex.withLock {
            Log.d(TAG, "Attempting to write to tag")

            try {
                val ndef = Ndef.get(tag)

                if (ndef != null) {
                    // NDEF formatlandıysa
                    return@withLock writeToNdef(ndef, data)
                } else {
                    // NDEF formatlanmadıysa
                    val ndefFormatable = NdefFormatable.get(tag) ?: return@withLock Result.failure(
                        IOException("Tag doesn't support NDEF formatting")
                    )
                    return@withLock formatAndWriteToTag(ndefFormatable, data)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in writeToTag: ${e.message}")
                Result.failure(e)
            }
        }
    }

    /**
     * Zaten NDEF formatında olan bir tag'e yazar
     * Bağlantı yönetimi iyileştirilmiş
     */
    private suspend fun writeToNdef(ndef: Ndef, data: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Varolan bir bağlantı kontrolü
            if (ndef.isConnected) {
                Log.d(TAG, "Found existing connection in writeToNdef, closing first")
                try {
                    ndef.close()
                    delay(100) // Bağlantının kapanması için kısa bir bekleme
                } catch (e: Exception) {
                    Log.e(TAG, "Error closing existing connection: ${e.message}")
                }
            }

            _isConnected.value = true
            Log.d(TAG, "Connecting to NDEF tag for writing")
            ndef.connect()

            if (!ndef.isWritable) {
                return@withContext Result.failure(IOException("Tag is read-only"))
            }

            val maxSize = ndef.maxSize
            val message = createNdefMessage(data)

            if (message.byteArrayLength > maxSize) {
                return@withContext Result.failure(
                    IOException("Message size (${message.byteArrayLength} bytes) exceeds maximum tag size ($maxSize bytes)")
                )
            }

            Log.d(TAG, "Writing NDEF message to tag")
            ndef.writeNdefMessage(message)
            Log.d(TAG, "Successfully wrote data to tag")

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error in writeToNdef: ${e.message}")
            Result.failure(e)
        } finally {
            try {
                if (ndef.isConnected) {
                    Log.d(TAG, "Closing NDEF connection after write")
                    ndef.close()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error closing NDEF connection: ${e.message}")
            }
            _isConnected.value = false
            Log.d(TAG, "NFC connection state reset after write")
        }
    }

    /**
     * Formatlanmamış bir tag'i formatlar ve veri yazar
     * Bağlantı yönetimi iyileştirilmiş
     */
    private suspend fun formatAndWriteToTag(formatable: NdefFormatable, data: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            _isConnected.value = true
            Log.d(TAG, "Connecting to formatable tag")
            formatable.connect()

            val message = createNdefMessage(data)
            Log.d(TAG, "Formatting and writing NDEF message to tag")
            formatable.format(message)
            Log.d(TAG, "Successfully formatted and wrote data to tag")

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error in formatAndWriteToTag: ${e.message}")
            Result.failure(e)
        } finally {
            try {
                Log.d(TAG, "Closing formatable connection")
                formatable.close()
            } catch (e: Exception) {
                Log.e(TAG, "Error closing formatable connection: ${e.message}")
            }
            _isConnected.value = false
            Log.d(TAG, "NFC connection state reset after format and write")
        }
    }

    /**
     * Veriden NDEF mesajı oluşturur
     */
    private fun createNdefMessage(data: String): NdefMessage {
        val languageCode = "en"
        val textBytes = data.toByteArray(Charsets.UTF_8)
        val languageCodeBytes = languageCode.toByteArray(Charsets.US_ASCII)
        val languageCodeLength = languageCodeBytes.size

        val payload = ByteArray(1 + languageCodeLength + textBytes.size)
        payload[0] = languageCodeLength.toByte()

        System.arraycopy(languageCodeBytes, 0, payload, 1, languageCodeLength)
        System.arraycopy(textBytes, 0, payload, 1 + languageCodeLength, textBytes.size)

        val record = NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, ByteArray(0), payload)
        return NdefMessage(arrayOf(record))
    }

    /**
     * NFC bağlantı durumunu döndürür
     */
    fun isNfcConnected(): Boolean {
        return _isConnected.value
    }

    /**
     * Herhangi bir açık bağlantı varsa kapatmaya zorlar
     */
    fun forceCloseConnections() {
        if (_isConnected.value) {
            Log.d(TAG, "Force closing NFC connections")
            _isConnected.value = false
        }
    }
}