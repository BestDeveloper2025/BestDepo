package com.bestmakina.depotakip.presentation.ui.view.bulkTransfer

import com.bestmakina.depotakip.common.model.TransferItemModel

data class BulkTransferState(
    val isLoading: Boolean = true,
    val name: String = "",
    val wareHouseName: String = "",
    val isNfcEnabled: Boolean = false,
    val selectedPersonnel: TransferItemModel? = TransferItemModel(id = "", name = "Nfc Okuma Bekleniyor"),
)