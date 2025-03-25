package com.bestmakina.depotakip.presentation.ui.view.TransferWithRecete

import com.bestmakina.depotakip.common.model.TransferItemModel
import com.bestmakina.depotakip.domain.model.InventoryModel

data class TransferWithReceteState(
    val isLoading: Boolean = false,
    val personnelList: List<TransferItemModel> = emptyList(),
    val selectedPersonnel: TransferItemModel? = TransferItemModel(id = "", name = "Teslim Alan Seç"),
    val transferNedeniList: List<TransferItemModel> = emptyList(),
    val selectedTransferNedeni: TransferItemModel? = TransferItemModel(id = "", name = "Transfer Nedeni Seç"),
    val machineList: List<TransferItemModel> = emptyList(),
    val selectedMachine: TransferItemModel? = TransferItemModel(id = "", name = "Makina Seri Seç"),
    val panelVisibility: Boolean = false,
    val panelData: InventoryModel? = null,
    val searchState: Int = 0,
    val barcodeData: String = "",
    val showSearchablePanel: Boolean = false,
    val userName: String = "",
    val warehouseName: String = "",
)