package com.bestmakina.depotakip.presentation.ui.view.TransferWithRecete.viewmodel

import android.content.Context
import android.nfc.Tag
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.bestmakina.depotakip.common.base.BaseNfcViewModel
import com.bestmakina.depotakip.common.model.DropdownType
import com.bestmakina.depotakip.common.model.TransferItemModel
import com.bestmakina.depotakip.common.network.NetworkResult
import com.bestmakina.depotakip.data.local.SharedPreferencesHelper
import com.bestmakina.depotakip.data.model.request.inventory.GetInventoryDataRequest
import com.bestmakina.depotakip.data.model.request.inventory.TransferWithReceteRequest
import com.bestmakina.depotakip.domain.manager.BarcodeManager
import com.bestmakina.depotakip.domain.manager.NfcManager
import com.bestmakina.depotakip.domain.model.PreferencesKeys
import com.bestmakina.depotakip.domain.usecase.inventory.GetInventoryDataUseCase
import com.bestmakina.depotakip.domain.usecase.inventory.TransferWithReceteUseCase
import com.bestmakina.depotakip.domain.usecase.cache.GetAllMachineDataUseCase
import com.bestmakina.depotakip.domain.usecase.cache.GetAllRecipientUseCase
import com.bestmakina.depotakip.domain.usecase.cache.GetAllTransferReasonUseCase
import com.bestmakina.depotakip.presentation.ui.view.TransferWithRecete.TransferWithReceteAction
import com.bestmakina.depotakip.presentation.ui.view.TransferWithRecete.TransferWithReceteEffect
import com.bestmakina.depotakip.presentation.ui.view.TransferWithRecete.TransferWithReceteState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransferWithReceteViewModel @Inject constructor(
    private val getTeslimAlanUseCase: GetAllRecipientUseCase,
    private val getTransferNedeniUseCase: GetAllTransferReasonUseCase,
    private val getMachineListUseCase: GetAllMachineDataUseCase,
    private val getInventoryDataUseCase: GetInventoryDataUseCase,
    private val sharedPreferencesHelper: SharedPreferencesHelper,
    private val transferWithReceteUseCase: TransferWithReceteUseCase,
    private val nfcManager: NfcManager,
    @ApplicationContext private val context: Context
) : BaseNfcViewModel(nfcManager) {

    private val barcodeManager = BarcodeManager(context)

    private var barcodeJob: Job? = null

    private val _state = MutableStateFlow(TransferWithReceteState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<TransferWithReceteEffect>()
    val effect = _effect.asSharedFlow()

    private val _tagData = MutableStateFlow<String?>(null)

    private val _depoKodu = sharedPreferencesHelper.getData(PreferencesKeys.WareHouseName, "")
    private val _terminalKullanici = sharedPreferencesHelper.getData(PreferencesKeys.UserId, "")

    init {
        handleBarcodeData()
        getTerminalInfo()
        observeNfcTags()
    }


    fun handleAction(action: TransferWithReceteAction) {
        when (action) {
            is TransferWithReceteAction.ToggleDropdown -> viewModelScope.launch {
                toggleDropdown(action.dropdownType)
            }

            is TransferWithReceteAction.ButtonClick -> transferButtonClick()
            is TransferWithReceteAction.ChangePanelVisibility -> openDetailPanel()
            is TransferWithReceteAction.TransferButtonClick -> panelTransferButtonClick(
                action.quantity,
                action.stockCode
            )

            is TransferWithReceteAction.LoadItemData -> loadItemData(action.transferItemModel)
            is TransferWithReceteAction.ChangeListPanelVisibility -> changeListPanelVisibility()
            is TransferWithReceteAction.CloseDetailPanel -> closeDetailPanel()
            is TransferWithReceteAction.PreparePage -> setViewData()
        }
    }

    private fun setViewData() {
        preparePage()
    }

    private fun getTerminalInfo() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                userName = sharedPreferencesHelper.getData(PreferencesKeys.UserName),
                warehouseName = sharedPreferencesHelper.getData(PreferencesKeys.WareHouseName)
            )
        }
    }

    private fun handleBarcodeData() {
        barcodeJob?.cancel()
        barcodeJob = viewModelScope.launch {
            barcodeManager.barcodeData
                .filterNotNull()
                .filterNot { it.isEmpty() }
                .collect { barcode ->
                    if (barcode.isNotEmpty()) {
                        Log.d("BarcodeData", barcode)
                        barcodeProcess(barcode)
                        barcodeManager.clearBarcodeData()
                    }
                }
        }
    }

    private suspend fun toggleDropdown(dropdownType: DropdownType) {
        _state.value = when (dropdownType) {
            DropdownType.PERSONNEL -> _state.value.copy(searchState = 1)
            DropdownType.TRANSFER_NEDENI -> _state.value.copy(searchState = 2)
            DropdownType.MACHINE -> _state.value.copy(searchState = 3)
        }
        handleSelectedPanel()
    }

    private suspend fun handleSelectedPanel() {
        when (_state.value.searchState) {
            1 -> {
                _effect.emit(TransferWithReceteEffect.ShowSearchablePanel(_state.value.personnelList))
                changeListPanelVisibility()
            }

            2 -> {
                _effect.emit(TransferWithReceteEffect.ShowSearchablePanel(_state.value.transferNedeniList))
                changeListPanelVisibility()
            }

            3 -> {
                _effect.emit(TransferWithReceteEffect.ShowSearchablePanel(_state.value.machineList))
                changeListPanelVisibility()
            }
        }
    }

    private fun preparePage() {
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
                val transferNedeniFlow = getTransferNedeniUseCase().map { entityList ->
                    entityList.map { entity ->
                        TransferItemModel(
                            id = entity.Kod ?: "",
                            name = entity.TransferNedeni ?: ""
                        )
                    }
                }
                val machineFlow = getMachineListUseCase().map { entityList ->
                    entityList.map { entity ->
                        TransferItemModel(
                            id = entity.Kod ?: "",
                            name = entity.MakinaSeri ?: ""
                        )
                    }
                }
                val personnelList = personnelFlow.first()
                val transferNedeniList = transferNedeniFlow.first()
                val machineList = machineFlow.first()
                Log.d("TransferWithRecete", "preparePage:$transferNedeniList")

                _state.value = _state.value.copy(
                    personnelList = personnelList,
                    transferNedeniList = transferNedeniList,
                    machineList = machineList,
                )
            } catch (e: Exception) {
                _effect.emit(TransferWithReceteEffect.ShowToast("Veriler yüklenirken bir hata oluştu: ${e.message}"))
            }
        }
    }

    private fun getInventoryData(barcodeData: String) {
        val selectedMachineId = state.value.selectedMachine?.id ?: return
        Log.d("TransferWithRecete", "getInventoryData burda: $selectedMachineId")
        viewModelScope.launch {
            getInventoryDataUseCase(
                GetInventoryDataRequest(
                    makinaSeri = selectedMachineId,
                    stokKodu = barcodeData
                )
            ).collectLatest { result ->
                when (result) {
                    is NetworkResult.Error<*> -> {
                        _effect.emit(TransferWithReceteEffect.ShowToast("Bir Hata Oluştu Lütfen Tekrar Deneyiniz"))
                    }

                    is NetworkResult.Loading<*> -> {
                        _state.value = _state.value.copy(isLoading = true)
                    }

                    is NetworkResult.Success<*> -> {
                        val inventoryData = result.data
                        if (inventoryData != null) {
                            if (inventoryData.durum == "OK" && inventoryData.receteMiktari != 0) {
                                _state.value =
                                    _state.value.copy(
                                        panelData = result.data,
                                        isLoading = false,
                                        barcodeData = ""
                                    )
                                barcodeManager.clearBarcodeData()
                                openDetailPanel()
                            } else if (inventoryData.durum == "OK" && inventoryData.receteMiktari == 0){
                                _effect.emit(TransferWithReceteEffect.ShowToast("Ürün Seçmiş Olduğunuz Makina Reçetesinde Bulunmuyor"))
                                _state.value = _state.value.copy(panelVisibility = false, isLoading = false)
                            }
                            else {
                                _state.value = _state.value.copy(isLoading = false)
                                _effect.emit(TransferWithReceteEffect.ShowToast("Reçete Bulunamadı"))
                            }
                        } else {
                            _state.value = _state.value.copy(isLoading = false)
                        }
                    }
                }
            }
        }
    }

    private fun checkButtonClickable(): Boolean {
        return _state.value.selectedPersonnel?.name != "Teslim Alan Seç" &&
                _state.value.selectedTransferNedeni?.name != "Transfer Nedeni Seç" &&
                _state.value.selectedMachine?.name != "Makina Seri Seç"
    }

    private fun transferButtonClick() {
        if (checkButtonClickable()) {
            barcodeManager.startScanning()
        } else {
            viewModelScope.launch {
                _effect.emit(TransferWithReceteEffect.ShowToast("Lütfen seçimleri yapınız"))
            }
        }
    }

    private fun openDetailPanel() {
        if (state.value.panelVisibility) {
            barcodeManager.clearBarcodeData()
        }
        _state.value = _state.value.copy(panelVisibility = true)
    }

    private fun closeDetailPanel() {
        _state.value = _state.value.copy(panelVisibility = false)
    }

    private fun panelTransferButtonClick(quantity: Int, stockCode: String) {
        viewModelScope.launch {
            val request = TransferWithReceteRequest(
                MakinaSeri = state.value.selectedMachine!!.id,
                TransferNedeni = state.value.selectedTransferNedeni!!.id,
                TerminalUser = _terminalKullanici,
                TeslimAlan = state.value.selectedPersonnel!!.id,
                DepoKodu = _depoKodu,
                StokKodu = stockCode,
                TransferMiktari = quantity.toString(),
            )

            Log.d("TransferWithReceteRequest", request.toString())

            transferWithReceteUseCase(request)
                .flowOn(Dispatchers.IO)
                .collectLatest { result ->
                    when (result) {
                        is NetworkResult.Error -> {
                            _state.value = _state.value.copy(isLoading = false)
                            _effect.emit(TransferWithReceteEffect.ShowToast("Bir Hata Oluştu Lütfen Tekrar Deneyiniz"))
                        }

                        is NetworkResult.Loading -> {
                            _state.value = _state.value.copy(isLoading = true)
                        }

                        is NetworkResult.Success -> {
                            barcodeProcess(stockCode)
                            _effect.emit(TransferWithReceteEffect.ShowToast("Transfer Başarılı"))
                            _state.value = _state.value.copy(isLoading = false)
                        }
                    }
                }
            barcodeManager.clearBarcodeData()
            Log.d(
                "TransferWithRecete",
                "${_state.value.selectedMachine}, ${_state.value.selectedTransferNedeni}, ${_state.value.selectedPersonnel}, ${_state.value.barcodeData}, "
            )
        }
    }

    private fun loadItemData(transferItemModel: TransferItemModel) {
        when (_state.value.searchState) {
            1 -> {
                _state.value = _state.value.copy(selectedPersonnel = transferItemModel)
                changeListPanelVisibility()
                _state.value = _state.value.copy(searchState = 0)
            }

            2 -> {
                _state.value = _state.value.copy(selectedTransferNedeni = transferItemModel)
                changeListPanelVisibility()
                _state.value = _state.value.copy(searchState = 0)
            }

            3 -> {
                _state.value = _state.value.copy(selectedMachine = transferItemModel)
                changeListPanelVisibility()
                _state.value = _state.value.copy(searchState = 0)
            }
        }
    }

    private fun changeListPanelVisibility() {
        _state.value = _state.value.copy(
            showSearchablePanel = !_state.value.showSearchablePanel, barcodeData = ""
        )
        barcodeManager.clearBarcodeData()
    }

    private fun barcodeProcess(barcodeData: String) {
        if (_state.value.searchState == 0) {
            getInventoryData(barcodeData)
        } else {
            _state.value = _state.value.copy(barcodeData = barcodeData.removePrefix("0"))
        }
    }

    override fun onTagDetected(tag: Tag) {
        viewModelScope.launch {
            readFromTag(tag)
        }
    }

    private fun readFromTag(tag: Tag) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            readFromTagAndLog(tag).fold(
                onSuccess = { data ->
                    _tagData.value = data
                    _effect.emit(TransferWithReceteEffect.ShowToast("Kart Okundu"))
                    _state.value = _state.value.copy(
                        isLoading = false,
                        barcodeData = data.removePrefix("0")
                    )
                    handleNfcData()
                },
                onFailure = { error ->
                    _effect.emit(TransferWithReceteEffect.ShowToast("Okuma hatası: ${error.message}"))
                    _state.value = _state.value.copy(isLoading = false)
                }
            )
        }
    }

    private fun handleNfcData() {
        val barcodeData = state.value.barcodeData

        val foundItem = state.value.personnelList.find { item ->
            item.id.removePrefix("0") == barcodeData || item.id == barcodeData
        }
        if (foundItem != null) {
            Log.d("NftRegistration", "Etikettten okunan veri: ${foundItem.id} ${foundItem.name}")
            _state.value = _state.value.copy(selectedPersonnel = foundItem)
        } else {
            viewModelScope.launch {
                _effect.emit(TransferWithReceteEffect.ShowToast("Okutulan Personel Bulunamadı"))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            nfcManager.forceCloseConnections()
        }
    }

}