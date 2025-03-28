package com.bestmakina.depotakip.presentation.ui.view.nftregistration.viewmodel

import android.app.Activity
import android.content.Context
import kotlinx.coroutines.flow.asSharedFlow


import android.nfc.Tag
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.bestmakina.depotakip.common.base.BaseNfcViewModel
import com.bestmakina.depotakip.common.model.TransferItemModel
import com.bestmakina.depotakip.domain.manager.NfcManager
import com.bestmakina.depotakip.domain.usecase.cache.GetAllRecipientUseCase
import com.bestmakina.depotakip.presentation.ui.view.nftregistration.NftRegistrationAction
import com.bestmakina.depotakip.presentation.ui.view.nftregistration.NftRegistrationEffect
import com.bestmakina.depotakip.presentation.ui.view.nftregistration.NftRegistrationState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NftRegistrationViewModel @Inject constructor(
    private val getTeslimAlanUseCase: GetAllRecipientUseCase,
    private val nfcManager: NfcManager,
    @ApplicationContext private val context: Context,
) : BaseNfcViewModel(nfcManager) {

    private val _state = MutableStateFlow(NftRegistrationState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<NftRegistrationEffect>(extraBufferCapacity = 1)
    val effect = _effect.asSharedFlow()

    private val _tagData = MutableStateFlow<String?>(null)

    init {
        observeNfcTags()
        preparePage()
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

            is NftRegistrationAction.ChangePanelVisibility -> changePanelVisibility()
            is NftRegistrationAction.LoadPersonnelData -> loadPersonnelData(action.selectedPersonnel)
        }
    }

    private fun preparePage(){
        viewModelScope.launch {
            try {
                val personnelFlow = getTeslimAlanUseCase().map { entityList ->
                    entityList.map { entity ->
                        TransferItemModel(
                            id = entity.Kod ?: "",
                            name = entity.TeslimAlan ?: ""
                        )
                    }
                }
                val personnelList = personnelFlow.first()
                _state.value = _state.value.copy(personnelList = personnelList)
            }catch (e: Exception){
                Log.e("NftRegistration", "Error fetching personnel list", e)
                _effect.emit(NftRegistrationEffect.ShowToast("Personel listesi alınamadı"))
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

    private fun changePanelVisibility() {
        _state.value = _state.value.copy(panelVisibility = !_state.value.panelVisibility)
    }

    private fun loadPersonnelData(selectedPersonnel: TransferItemModel) {
        _state.value = _state.value.copy(
            selectedPersonnel = selectedPersonnel,
            panelVisibility = false,
            inputData = selectedPersonnel.id
        )
    }

    public override fun onCleared() {
        super.onCleared()
        viewModelScope.coroutineContext.cancelChildren()
        Log.d("NftRegistrationViewModel", "onCleared: ViewModel cleared")
        if (context is Activity) {
            val activity = context as Activity
            nfcManager.disableForegroundDispatch(activity = activity)
        }
    }
}