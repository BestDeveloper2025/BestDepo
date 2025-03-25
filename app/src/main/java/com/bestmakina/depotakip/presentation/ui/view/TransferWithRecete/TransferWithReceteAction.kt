package com.bestmakina.depotakip.presentation.ui.view.TransferWithRecete

import com.bestmakina.depotakip.common.model.DropdownType
import com.bestmakina.depotakip.common.model.TransferItemModel

sealed class TransferWithReceteAction {
    data class ToggleDropdown(val dropdownType: DropdownType) : TransferWithReceteAction()
    data object ButtonClick : TransferWithReceteAction()
    data object ChangePanelVisibility : TransferWithReceteAction()
    data class TransferButtonClick(val quantity: Int, val stockCode: String) : TransferWithReceteAction()
    data class LoadItemData(val transferItemModel : TransferItemModel): TransferWithReceteAction()
    data object ChangeListPanelVisibility : TransferWithReceteAction()
    data object CloseDetailPanel : TransferWithReceteAction()
    data object PreparePage: TransferWithReceteAction()
}