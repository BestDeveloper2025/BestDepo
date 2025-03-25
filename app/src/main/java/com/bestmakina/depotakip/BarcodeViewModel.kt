package com.bestmakina.depotakip

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import com.honeywell.aidc.AidcManager
import com.honeywell.aidc.BarcodeReader
import com.honeywell.aidc.BarcodeFailureEvent
import com.honeywell.aidc.BarcodeReadEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class BarcodeViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private var barcodeReader: BarcodeReader? = null
    private val _barcodeData = MutableStateFlow<String?>(null)
    val barcodeData: StateFlow<String?> = _barcodeData.asStateFlow()

    init {
        initBarcodeReader()
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
                    Log.e("BarcodeViewModel", "Barkod okuma hatası: ${failureEvent}")
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

    override fun onCleared() {
        super.onCleared()
        barcodeReader?.release()
    }

    fun clearBarcodeData() {
        _barcodeData.value = null
    }
}