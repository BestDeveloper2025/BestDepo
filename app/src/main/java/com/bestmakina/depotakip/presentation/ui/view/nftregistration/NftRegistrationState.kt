package com.bestmakina.depotakip.presentation.ui.view.nftregistration

data class NftRegistrationState (
    val isLoading: Boolean = false,
    val inputData: String = "",
    val alertDialogVisibility: Boolean = false,
    val isWaitingForNfcScan: Boolean = false
)