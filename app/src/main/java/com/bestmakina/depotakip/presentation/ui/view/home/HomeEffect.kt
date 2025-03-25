package com.bestmakina.depotakip.presentation.ui.view.home

sealed class HomeEffect {
    data class ShowToast(val message: String) : HomeEffect()
    data class NavigateTo(val route: String) : HomeEffect()
}