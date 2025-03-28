package com.bestmakina.depotakip.presentation.ui.view.nftregistration

import com.bestmakina.depotakip.common.model.TransferItemModel

data class NftRegistrationState(
    val isLoading: Boolean = false,
    val inputData: String = "",
    val alertDialogVisibility: Boolean = false,
    val isWaitingForNfcScan: Boolean = false,
    val personnelList: List<TransferItemModel> = emptyList(),
    val selectedPersonnel: TransferItemModel? = TransferItemModel(id = "", name = "Personel Seç"),
    val panelVisibility: Boolean = false,
)