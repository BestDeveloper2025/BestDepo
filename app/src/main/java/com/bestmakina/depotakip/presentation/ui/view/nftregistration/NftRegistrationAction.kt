package com.bestmakina.depotakip.presentation.ui.view.nftregistration

import com.bestmakina.depotakip.common.model.TransferItemModel

sealed class NftRegistrationAction {
    data class SetInputData(val data: String) : NftRegistrationAction()
    data object ChangeAlertDialogVisibility : NftRegistrationAction()
    data object ScanButtonClick : NftRegistrationAction()
    data object StartNfcScan : NftRegistrationAction()
    data object ChangePanelVisibility: NftRegistrationAction()
    data class LoadPersonnelData(val selectedPersonnel: TransferItemModel) : NftRegistrationAction()
}