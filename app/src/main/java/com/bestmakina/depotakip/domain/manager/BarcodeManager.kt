package com.bestmakina.depotakip.domain.manager

import android.content.Context
import android.util.Log
import com.honeywell.aidc.AidcManager
import com.honeywell.aidc.BarcodeReader
import com.honeywell.aidc.BarcodeFailureEvent
import com.honeywell.aidc.BarcodeReadEvent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

class BarcodeManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var barcodeReader: BarcodeReader? = null
    private val _barcodeData = MutableStateFlow<String?>(null)
    val barcodeData: StateFlow<String?> get() = _barcodeData

    init {
        initBarcodeReader()
    }

    fun changeBarcodeData(barcode: String) {
        _barcodeData.value = barcode
    }

    private fun initBarcodeReader() {
        AidcManager.create(context) { aidcManager ->
            aidcManager?.let {
                barcodeReader = it.createBarcodeReader()
                setupBarcodeReader()
            }
        }
    }

    private fun setupBarcodeReader() {
        barcodeReader?.let { reader ->
            reader.setProperty(
                BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE,
                BarcodeReader.TRIGGER_CONTROL_MODE_AUTO_CONTROL
            )

            reader.addBarcodeListener(object : BarcodeReader.BarcodeListener {
                override fun onBarcodeEvent(barcodeEvent: BarcodeReadEvent) {
                    val barcode = barcodeEvent.barcodeData
                    _barcodeData.value = barcode
                }

                override fun onFailureEvent(failureEvent: BarcodeFailureEvent) {
                    Log.e("BarcodeManager", "Barkod okuma hatası: ${failureEvent}")
                }
            })

            reader.claim()
        }
    }

    fun startScanning() {
        barcodeReader?.aim(true)
        barcodeReader?.light(true)
        barcodeReader?.decode(true)
    }

    fun stopScanning() {
        barcodeReader?.aim(false)
        barcodeReader?.light(false)
        barcodeReader?.decode(false)
    }

    fun releaseScanner() {
        barcodeReader?.release()
    }

    fun clearBarcodeData() {
        _barcodeData.value = null
    }
}
