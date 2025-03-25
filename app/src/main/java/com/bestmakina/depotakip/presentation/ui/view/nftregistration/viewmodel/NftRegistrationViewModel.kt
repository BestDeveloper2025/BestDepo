package com.bestmakina.depotakip.presentation.ui.view.nftregistration.viewmodel

import kotlinx.coroutines.flow.asSharedFlow


import android.nfc.Tag
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.bestmakina.depotakip.common.base.BaseNfcViewModel
import com.bestmakina.depotakip.domain.manager.NfcManager
import com.bestmakina.depotakip.presentation.ui.view.nftregistration.NftRegistrationAction
import com.bestmakina.depotakip.presentation.ui.view.nftregistration.NftRegistrationEffect
import com.bestmakina.depotakip.presentation.ui.view.nftregistration.NftRegistrationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NftRegistrationViewModel @Inject constructor(
    private val nfcManager: NfcManager
) : BaseNfcViewModel(nfcManager) {

    private val _state = MutableStateFlow(NftRegistrationState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<NftRegistrationEffect>(extraBufferCapacity = 1)
    val effect = _effect.asSharedFlow()

    private val _tagData = MutableStateFlow<String?>(null)
    val tagData = _tagData.asStateFlow()

    init {
        observeNfcTags()
    }

    fun handleAction(action: NftRegistrationAction) {
        when (action) {
            is NftRegistrationAction.SetInputData -> changeInputData(action.data)
            NftRegistrationAction.ChangeAlertDialogVisibility -> _state.value = _state.value.copy(alertDialogVisibility = !_state.value.alertDialogVisibility)
            NftRegistrationAction.ScanButtonClick -> checkAndScanButton()
            NftRegistrationAction.StartNfcScan -> {
                _state.value = _state.value.copy(
                    isWaitingForNfcScan = true,
                    alertDialogVisibility = false
                )
                viewModelScope.launch {
                    _effect.emit(NftRegistrationEffect.ShowToast("Lütfen NFC kartınızı cihazınıza yaklaştırın"))
                }
            }
        }
    }

    private fun changeInputData(data: String) {
        _state.value = _state.value.copy(
            inputData = data
        )
    }

    private fun checkAndScanButton() {
        if (_state.value.inputData.length < 3) {
            viewModelScope.launch {
                _effect.emit(NftRegistrationEffect.ShowToast("NFT bilgisi en az 3 karakter olmalıdır"))
            }
        } else {
            _state.value = _state.value.copy(alertDialogVisibility = !_state.value.alertDialogVisibility)
        }
    }

    override fun onTagDetected(tag: Tag) {
        if (_state.value.isWaitingForNfcScan) {
            viewModelScope.launch {
                handleWriteData(tag)
            }
        } else {
            readFromTag(tag)
        }
    }

    private fun handleWriteData(tag: Tag) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            writeToTag(tag, _state.value.inputData).fold(
                onSuccess = {
                    _effect.emit(NftRegistrationEffect.ShowToast("Veri başarıyla kaydedildi ${state.value.inputData}"))
                    _state.value = _state.value.copy(isWaitingForNfcScan = false)
                },
                onFailure = { error ->
                    _effect.emit(NftRegistrationEffect.ShowToast("Kaydetme hatası: ${error.message}"))
                    _state.value = _state.value.copy(isWaitingForNfcScan = false)
                }
            )
        }
    }

    private fun readFromTag(tag: Tag) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            readFromTagAndLog(tag).fold(
                onSuccess = { data ->
                    Log.d("NftRegistration", "Etikettten okunan veri: $data")
                    _tagData.value = data
                    _effect.emit(NftRegistrationEffect.ShowToast("Veri başarıyla okundu: $data"))
                    _state.value = _state.value.copy(
                        isLoading = false,
                    )
                },
                onFailure = { error ->
                    Log.e("NftRegistration", "Etiketten okuma hatası", error)
                    _effect.emit(NftRegistrationEffect.ShowToast("Okuma hatası: ${error.message}"))
                    _state.value = _state.value.copy(isLoading = false)
                }
            )
        }
    }
}