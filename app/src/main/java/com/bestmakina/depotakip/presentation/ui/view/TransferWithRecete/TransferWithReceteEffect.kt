package com.bestmakina.depotakip.presentation.ui.view.TransferWithRecete

import com.bestmakina.depotakip.common.model.TransferItemModel


sealed class TransferWithReceteEffect {
    data class ShowToast(val message: String) : TransferWithReceteEffect()
    data class ShowSearchablePanel(val transferItemModelList: List<TransferItemModel>) : TransferWithReceteEffect()
}