package com.bestmakina.depotakip.presentation.ui.view.bulkTransfer
import com.bestmakina.depotakip.common.model.TransferItemModel

sealed class BulkTransferAction {
    data object ChangePanelVisibility : BulkTransferAction()
    data class LoadMachineData(val machineData: TransferItemModel) : BulkTransferAction()
    data object StartTransferButtonClick : BulkTransferAction()
    data object OnNextButtonClick : BulkTransferAction()
    data object OnBackButtonClick : BulkTransferAction()
    data object TransferProduct : BulkTransferAction()
    data object CloseDetailPanel : BulkTransferAction()
}