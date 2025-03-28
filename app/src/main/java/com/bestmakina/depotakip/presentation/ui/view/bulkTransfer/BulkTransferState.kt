package com.bestmakina.depotakip.presentation.ui.view.bulkTransfer

import com.bestmakina.depotakip.common.model.TransferItemModel
import com.bestmakina.depotakip.domain.model.InventoryModel

data class BulkTransferState(
    val isLoading: Boolean = true,
    val name: String = "",
    val wareHouseName: String = "",
    val isNfcEnabled: Boolean = false,
    val selectedPersonnel: TransferItemModel? = TransferItemModel(id = "000717083", name = "Personel Seç"),
    val machineList: List<TransferItemModel> = emptyList(),
    val selectedMachine: TransferItemModel? = TransferItemModel(id = "", name = "Makina Seri Seç"),
    val searchablePanelVisibility: Boolean = false,
    val stockCodes: List<String> = emptyList(),
    val currentProductDetail: InventoryModel? = null,
    val nextProductDetail: InventoryModel? = null,
    val detailPanelVisibility: Boolean = false,
    val listState: Int = 0
    )