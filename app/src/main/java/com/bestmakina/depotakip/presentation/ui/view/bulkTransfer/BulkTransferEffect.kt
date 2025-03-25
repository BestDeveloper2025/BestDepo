package com.bestmakina.depotakip.presentation.ui.view.bulkTransfer

sealed class BulkTransferEffect {
    data class ShowToast(val message: String) : BulkTransferEffect()
    //TODO: Add effects
}