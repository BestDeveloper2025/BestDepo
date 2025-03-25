package com.bestmakina.depotakip.presentation.ui.view.bulkTransfer.viewmodel

import android.nfc.Tag
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.bestmakina.depotakip.common.base.BaseNfcViewModel
import com.bestmakina.depotakip.common.model.TransferItemModel
import com.bestmakina.depotakip.common.network.NetworkResult
import com.bestmakina.depotakip.data.local.SharedPreferencesHelper
import com.bestmakina.depotakip.domain.manager.NfcManager
import com.bestmakina.depotakip.domain.model.PreferencesKeys
import com.bestmakina.depotakip.domain.usecase.GetNameByBarcodeUseCase
import com.bestmakina.depotakip.presentation.ui.view.bulkTransfer.BulkTransferEffect
import com.bestmakina.depotakip.presentation.ui.view.bulkTransfer.BulkTransferState
import com.bestmakina.depotakip.presentation.ui.view.login.LoginEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BulkTransferViewModel @Inject constructor(
    private val nfcManager: NfcManager,
    private val sharedPreferencesHelper: SharedPreferencesHelper,
    private val getNameToBarcodeUseCase: GetNameByBarcodeUseCase
) : BaseNfcViewModel(nfcManager) {

    private val _state = MutableStateFlow(BulkTransferState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<BulkTransferEffect>(replay = 1)
    val effect = _effect.asSharedFlow()

    private val _tagData = MutableStateFlow<String?>(null)


    init {
        observeNfcTags()
        preparePage()
    }

    private fun preparePage() {
        getTerminalData()
    }

    private fun getTerminalData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                name = sharedPreferencesHelper.getData(PreferencesKeys.UserName),
                wareHouseName = sharedPreferencesHelper.getData(PreferencesKeys.WareHouseName)
            )
        }
    }

    override fun onTagDetected(tag: Tag) {
        viewModelScope.launch {
            readFromTag(tag)
        }
    }

    private fun readFromTag(tag: Tag) {
        viewModelScope.launch {
            readFromTagAndLog(tag).fold(
                onSuccess = { data ->
                    _tagData.value = data
                    _state.value = _state.value.copy(
                        isNfcEnabled = true
                    )
                    getNameByBarcode(data)
                },
                onFailure = { error ->
                    _effect.emit(BulkTransferEffect.ShowToast("Okuma hatası: ${error.message}"))

                }
            )
        }
    }

    private fun getNameByBarcode(barcode: String) {
        viewModelScope.launch {
            getNameToBarcodeUseCase(barcode).collectLatest { result ->
                when (result) {
                    is NetworkResult.Loading -> Log.d("LoginViewModel", "getNameByBarcode: Loading")
                    is NetworkResult.Success -> {
                        result.data?.let {
                            Log.d(
                                "LoginViewModel",
                                "getNameByBarcode: Success, personnelName: ${it.personnelAdi}"
                            )
                            _state.value = _state.value.copy(
                                isLoading = false,
                                selectedPersonnel = TransferItemModel(
                                    id = barcode,
                                    name = it.personnelAdi
                                ),

                            )
                        }
                    }

                    is NetworkResult.Error -> {
                        _state.value = _state.value.copy(isLoading = false)
                        Log.d(
                            "LoginViewModel",
                            "getNameByBarcode: Error, message: ${result.message}"
                        )
                        _effect.emit(BulkTransferEffect.ShowToast("Barkod okuma hatası: ${result.message}"))
                    }
                }
            }
        }
    }

}