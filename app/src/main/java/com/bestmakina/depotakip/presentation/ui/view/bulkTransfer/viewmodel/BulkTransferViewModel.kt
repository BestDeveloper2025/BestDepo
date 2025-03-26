package com.bestmakina.depotakip.presentation.ui.view.bulkTransfer.viewmodel

import android.nfc.Tag
import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bestmakina.depotakip.common.base.BaseNfcViewModel
import com.bestmakina.depotakip.common.model.TransferItemModel
import com.bestmakina.depotakip.common.network.NetworkResult
import com.bestmakina.depotakip.data.local.SharedPreferencesHelper
import com.bestmakina.depotakip.data.model.request.inventory.MachineSerialRequest
import com.bestmakina.depotakip.domain.manager.NfcManager
import com.bestmakina.depotakip.domain.model.PreferencesKeys
import com.bestmakina.depotakip.domain.usecase.personnel.GetNameByBarcodeUseCase
import com.bestmakina.depotakip.domain.usecase.cache.GetAllMachineDataUseCase
import com.bestmakina.depotakip.domain.usecase.inventory.GetMachinePrescriptionUseCase
import com.bestmakina.depotakip.presentation.ui.view.bulkTransfer.BulkTransferAction
import com.bestmakina.depotakip.presentation.ui.view.bulkTransfer.BulkTransferEffect
import com.bestmakina.depotakip.presentation.ui.view.bulkTransfer.BulkTransferState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BulkTransferViewModel @Inject constructor(
    private val nfcManager: NfcManager,
    private val sharedPreferencesHelper: SharedPreferencesHelper,
    private val getNameToBarcodeUseCase: GetNameByBarcodeUseCase,
    private val getMachineDataUseCase: GetAllMachineDataUseCase,
    private val getMachinePrescriptionUseCase: GetMachinePrescriptionUseCase
) : BaseNfcViewModel(nfcManager) {

    private val _state = MutableStateFlow(BulkTransferState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<BulkTransferEffect>(replay = 1)
    val effect = _effect.asSharedFlow()

    private val _tagData = MutableStateFlow<String?>(null)

    fun handleAction(action: BulkTransferAction) {
        when (action) {
            is BulkTransferAction.ChangePanelVisibility -> changeListPanelVisibility()
            is BulkTransferAction.LoadMachineData -> loadMachineData(action.machineData)
            is BulkTransferAction.StartTransferButtonClick -> startTransfer()
        }
    }

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

    private fun getMachineData() {
        viewModelScope.launch {
            try {
                val machineFlow = getMachineDataUseCase().map { entityList ->
                    entityList.map { entity ->
                        TransferItemModel(
                            id = entity.Kod ?: "",
                            name = entity.MakinaSeri ?: ""
                        )
                    }
                }
                val machineList = machineFlow.first()
                _state.value = _state.value.copy(machineList = machineList)
            } catch (e: Exception) {
                _effect.emit(BulkTransferEffect.ShowToast("Veriler yüklenirken bir hata oluştu lütfen tekrar deneyiniz"))
            }
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
                            _state.value = _state.value.copy(
                                isLoading = false,
                                selectedPersonnel = TransferItemModel(
                                    id = barcode,
                                    name = it.personnelAdi
                                ),
                            )
                            getMachineData()
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

    private fun changeListPanelVisibility() {
        _state.value = _state.value.copy(
            searchablePanelVisibility = !_state.value.searchablePanelVisibility
        )
    }

    private fun loadMachineData(machineData: TransferItemModel) {
        _state.value = _state.value.copy(selectedMachine = machineData, searchablePanelVisibility = false)
    }

    private fun startTransfer(){
        viewModelScope.launch(Dispatchers.IO) {
            if(state.value.selectedMachine!!.id.isEmpty()){
                _effect.emit(BulkTransferEffect.ShowToast("Lütfen makina seçiniz"))
            }else {
                Log.d("BulkTransferViewModel", "startTransfer: ${state.value.selectedMachine!!.name}")
                Log.d("BulkTransferViewModel", "startTransfer: ${state.value.selectedMachine!!.id}")
                getMachinePrescription(state.value.selectedMachine!!.name)
            }
        }
    }

    private fun getMachinePrescription(machineID: String) {
        viewModelScope.launch {
            getMachinePrescriptionUseCase.invoke(MachineSerialRequest(machineID)).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> Log.d("BulkTransferViewModel", "getMachinePrescription: Loading")
                    is NetworkResult.Success -> {
                        result.data?.let {
                            _state.value = _state.value.copy(stockCodes = it.stockCodes ?: emptyList())
                        } ?: run {
                            Log.d("BulkTransferViewModel", "getMachinePrescription: Success but data is null")
                            _effect.emit(BulkTransferEffect.ShowToast("Makina reçetesi alınırken bir hata oluştuuuu"))
                        }
                    }
                    is NetworkResult.Error -> {
                        Log.d("BulkTransferViewModel", "getMachinePrescription: Error, message: ${result.message}")
                        _effect.emit(BulkTransferEffect.ShowToast("Makina reçetesi alınırken bir hata oluştu"))
                    }
                }
            }
        }
    }

}