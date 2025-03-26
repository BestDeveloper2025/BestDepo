package com.bestmakina.depotakip.presentation.ui.view.bulkTransfer

import com.bestmakina.depotakip.common.model.TransferItemModel

data class BulkTransferState(
    val isLoading: Boolean = true,
    val name: String = "",
    val wareHouseName: String = "",
    val isNfcEnabled: Boolean = false,
    val selectedPersonnel: TransferItemModel? = TransferItemModel(id = "", name = "Nfc Okuma Bekleniyor"),
    val machineList: List<TransferItemModel> = emptyList(),
    val selectedMachine: TransferItemModel? = TransferItemModel(id = "", name = "Makina Seri Seç"),
    val searchablePanelVisibility: Boolean = false,
    val stockCodes: List<String> = emptyList(),
    )