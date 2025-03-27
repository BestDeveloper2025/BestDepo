package com.bestmakina.depotakip.presentation.ui.view.bulkTransfer

import com.bestmakina.depotakip.common.model.TransferItemModel

sealed class BulkTransferAction {
    data object ChangePanelVisibility : BulkTransferAction()
    data class LoadMachineData(val machineData: TransferItemModel) : BulkTransferAction()
    data object StartTransferButtonClick : BulkTransferAction()
    data class TransferProduct(val quantity: Int, val stockCode: String) : BulkTransferAction()
    data object CloseDetailPanel : BulkTransferAction()
    data object OnCreateOrderButtonClick : BulkTransferAction()
}