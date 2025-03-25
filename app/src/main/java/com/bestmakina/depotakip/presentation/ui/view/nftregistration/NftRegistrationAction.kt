package com.bestmakina.depotakip.presentation.ui.view.nftregistration

sealed class NftRegistrationAction {
    data class SetInputData(val data: String) : NftRegistrationAction()
    data object ChangeAlertDialogVisibility : NftRegistrationAction()
    data object ScanButtonClick : NftRegistrationAction()
    data object StartNfcScan : NftRegistrationAction()
}