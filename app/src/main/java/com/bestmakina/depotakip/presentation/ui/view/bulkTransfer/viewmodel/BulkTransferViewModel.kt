package com.bestmakina.depotakip.presentation.ui.view.bulkTransfer.viewmodel

import android.nfc.Tag
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.bestmakina.depotakip.common.base.BaseNfcViewModel
import com.bestmakina.depotakip.common.model.TransferItemModel
import com.bestmakina.depotakip.common.network.NetworkResult
import com.bestmakina.depotakip.data.local.SharedPreferencesHelper
import com.bestmakina.depotakip.data.model.request.inventory.BulkTransferWithRecereRequest
import com.bestmakina.depotakip.data.model.request.inventory.CreateOrderRequest
import com.bestmakina.depotakip.data.model.request.inventory.GetInventoryDataRequest
import com.bestmakina.depotakip.data.model.request.inventory.MachineSerialRequest
import com.bestmakina.depotakip.domain.manager.NfcManager
import com.bestmakina.depotakip.domain.model.PreferencesKeys
import com.bestmakina.depotakip.domain.usecase.personnel.GetNameByBarcodeUseCase
import com.bestmakina.depotakip.domain.usecase.cache.GetAllMachineDataUseCase
import com.bestmakina.depotakip.domain.usecase.inventory.BulkTransferWithReceteUseCase
import com.bestmakina.depotakip.domain.usecase.inventory.CreateOrderUseCase
import com.bestmakina.depotakip.domain.usecase.inventory.GetInventoryDataUseCase
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
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BulkTransferViewModel @Inject constructor(
    private val nfcManager: NfcManager,
    private val sharedPreferencesHelper: SharedPreferencesHelper,
    private val getNameToBarcodeUseCase: GetNameByBarcodeUseCase,
    private val getMachineDataUseCase: GetAllMachineDataUseCase,
    private val getMachinePrescriptionUseCase: GetMachinePrescriptionUseCase,
    private val getInventoryDataUseCase: GetInventoryDataUseCase,
    private val bulkTransferWithReceteUseCase: BulkTransferWithReceteUseCase,
    private val createOrderUseCase: CreateOrderUseCase
) : BaseNfcViewModel(nfcManager) {

    private val _state = MutableStateFlow(BulkTransferState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<BulkTransferEffect>(replay = 1)
    val effect = _effect.asSharedFlow()

    private val _tagData = MutableStateFlow<String?>(null)

    private val _depoKodu = sharedPreferencesHelper.getData(PreferencesKeys.WareHouseName, "")
    private val _terminalKullanici = sharedPreferencesHelper.getData(PreferencesKeys.UserId, "")

    fun handleAction(action: BulkTransferAction) {
        when (action) {
            is BulkTransferAction.ChangePanelVisibility -> changeListPanelVisibility()
            is BulkTransferAction.LoadMachineData -> loadMachineData(action.machineData)
            is BulkTransferAction.StartTransferButtonClick -> startTransfer()
            is BulkTransferAction.TransferProduct -> transferSelectedItem(action.quantity, action.stockCode)
            is BulkTransferAction.CloseDetailPanel -> closeDetailPanel()
            is BulkTransferAction.OnCreateOrderButtonClick -> createOrderButtonClick(action.orderAmount, action.stockCode)
        }
    }

    init {
        //silinecek
        getMachineData()
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
                            id = entity.Kod,
                            name = entity.MakinaSeri
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
        _state.value =
            _state.value.copy(selectedMachine = machineData, searchablePanelVisibility = false)
    }

    private fun startTransfer() {
        viewModelScope.launch {
            if (state.value.selectedMachine!!.id.isEmpty()) {
                _effect.emit(BulkTransferEffect.ShowToast("Lütfen makina seçiniz"))
            } else {
                val tempMachineId = state.value.selectedMachine!!.id
                Log.d("BulkTransferViewModel", "startTransfer: $tempMachineId")
                getMachinePrescription(tempMachineId)
            }
        }
    }

    private fun getMachinePrescription(machineID: String) {
        viewModelScope.launch {
            getMachinePrescriptionUseCase.invoke(MachineSerialRequest(machineID))
                .collect { result ->
                    when (result) {
                        is NetworkResult.Loading -> _state.value =
                            _state.value.copy(isLoading = true)

                        is NetworkResult.Success -> {
                            result.data?.let {
                                when (it.status) {
                                    "OK" -> {
                                        _state.value =
                                            it.stockCodes?.let { it1 -> _state.value.copy(stockCodes = it1) }!!
                                        _state.value = _state.value.copy(
                                            isLoading = false,
                                            listState = it.counter
                                        )
                                        getInventoryData(it.stockCodes[state.value.listState])
                                    }

                                    "Hata" -> {
                                        _effect.emit(BulkTransferEffect.ShowToast("Reçete Bağlı Değil"))
                                        _state.value = _state.value.copy(isLoading = false)
                                    }

                                    else -> {
                                        _effect.emit(BulkTransferEffect.ShowToast("Reçete Alınırken Bir Hata Oluştu"))
                                        _state.value = _state.value.copy(isLoading = false)
                                    }
                                }
                            } ?: run {
                                Log.d(
                                    "BulkTransferViewModel",
                                    "getMachinePrescription: Success but data is null"
                                )
                                _effect.emit(BulkTransferEffect.ShowToast("Makina reçetesi alınırken bir hata oluştuuuu"))
                                _state.value = _state.value.copy(isLoading = false)
                            }
                        }

                        is NetworkResult.Error -> {
                            Log.d(
                                "BulkTransferViewModel",
                                "getMachinePrescription: Error, message: ${result.message}"
                            )
                            _effect.emit(BulkTransferEffect.ShowToast("Makina reçetesi alınırken bir hata oluştu"))
                            _state.value = _state.value.copy(isLoading = false)
                        }
                    }
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
                Log.d("TransferWithRecete", "collectLatest: result = $result")
                when (result) {
                    is NetworkResult.Error<*> -> {
                        Log.d("TransferWithRecete", "NetworkResult.Error: ${result.message}")
                        _effect.emit(BulkTransferEffect.ShowToast("Bir Hata Oluştu Lütfen Tekrar Deneyiniz"))
                    }

                    is NetworkResult.Loading<*> -> {
                        Log.d("TransferWithRecete", "NetworkResult.Loading")
                        _state.value = _state.value.copy(isLoading = true)
                    }

                    is NetworkResult.Success<*> -> {
                        val inventoryData = result.data
                        if (inventoryData != null) {
                            if (inventoryData.durum == "OK") {
                                _state.value =
                                    _state.value.copy(
                                        currentProductDetail = result.data,
                                        isLoading = false,
                                        detailPanelVisibility = true
                                    )
                            } else {
                                _state.value = _state.value.copy(isLoading = false)
                                _effect.emit(BulkTransferEffect.ShowToast("Reçete Bulunamadı"))
                            }
                        } else {
                            _state.value = _state.value.copy(isLoading = false)
                            Log.d(
                                "TransferWithRecete",
                                "getInventoryData: Success but data is null"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun transferSelectedItem(quantity: Int, stockCode: String) {
        viewModelScope.launch {
            val request = BulkTransferWithRecereRequest(
                MakinaSeri = state.value.selectedMachine!!.id,
                TransferNedeni = "1",
                TerminalUser = _terminalKullanici,
                TeslimAlan = state.value.selectedPersonnel!!.id,
                DepoKodu = _depoKodu,
                StokKodu = stockCode,
                TransferMiktari = quantity.toString(),
                SayacSira = state.value.listState + 1
            )

            bulkTransferWithReceteUseCase(request)
                .flowOn(Dispatchers.IO)
                .collectLatest { result ->
                    when (result) {
                        is NetworkResult.Loading -> {
                            _state.value = _state.value.copy(isLoading = true)
                        }

                        is NetworkResult.Success -> {
                            result.data?.let {
                                if (it.Durum == "OK") {
                                    _effect.emit(BulkTransferEffect.ShowToast("Transfer Başarılı"))

                                    val currentProductDetail = state.value.currentProductDetail ?: return@collectLatest
                                    val isLastItem = state.value.listState + 1 == state.value.stockCodes.size
                                    val montajaVerilen = currentProductDetail.montajaVerilen ?: return@collectLatest
                                    val isQuantityEqualToRecipe = montajaVerilen + quantity == currentProductDetail.receteMiktari

                                    if (isQuantityEqualToRecipe) {
                                        if (isLastItem) {
                                            _effect.emit(BulkTransferEffect.ShowToast("Reçete Sonuna Ulaşıldı"))
                                            _state.value = _state.value.copy(
                                                isLoading = false,
                                                searchablePanelVisibility = false
                                            )
                                        } else {
                                            val nextListState = state.value.listState + 1
                                            _state.value = _state.value.copy(
                                                isLoading = false,
                                                listState = nextListState
                                            )
                                            getInventoryData(state.value.stockCodes[nextListState])
                                        }
                                    } else {
                                        _effect.emit(BulkTransferEffect.ShowToast("Bir Sonraki Malzemeye Geçmeden Önce Sipariş Oluşturun veya Transferi Tamamlayın"))
                                        getInventoryData(state.value.stockCodes[state.value.listState])
                                    }


                                } else {
                                    _effect.emit(BulkTransferEffect.ShowToast("Transfer Başarısız"))
                                    _state.value = _state.value.copy(isLoading = false)
                                }
                            }
                        }

                        is NetworkResult.Error -> {
                            _effect.emit(BulkTransferEffect.ShowToast("Transfer Başarısız"))
                            _state.value = _state.value.copy(isLoading = false)
                        }
                    }
                }
        }
    }

    private fun createOrderButtonClick(orderAmount: Int, stockCode: String) {
        viewModelScope.launch {
            val request = CreateOrderRequest(
                DepoKodu = _depoKodu,
                MakinaSeri = state.value.selectedMachine!!.id,
                StokKodu = stockCode,
                TerminalUser = _terminalKullanici,
                IhtiyacMiktari = orderAmount.toString(),
                ReceteMiktari = state.value.currentProductDetail?.receteMiktari.toString(),
                LineNumber = (state.value.listState + 1).toString()
            )

            Log.d("CreateOrderRequest", "createOrderButtonClick: $request")
            createOrderUseCase.invoke(request).flowOn(Dispatchers.IO).collectLatest { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _state.value = _state.value.copy(isLoading = true)
                    }
                    is NetworkResult.Success -> {
                        _effect.emit(BulkTransferEffect.ShowToast("Sipariş Başarıyla Oluşturuldu"))
                        val nextListState = state.value.listState + 1
                        _state.value = _state.value.copy(
                            isLoading = false,
                            listState = nextListState
                        )
                        getInventoryData(state.value.stockCodes[nextListState])
                    }
                    is NetworkResult.Error -> {
                        _effect.emit(BulkTransferEffect.ShowToast("Sipariş Oluşturulurken Bir Hata Oluştu"))
                        _state.value = _state.value.copy(isLoading = false)
                    }
                }
            }
        }
    }

    private fun closeDetailPanel() {
        _state.value = _state.value.copy(detailPanelVisibility = false)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            nfcManager.forceCloseConnections()
        }
    }
}