package com.bestmakina.depotakip.presentation.ui.view.home

sealed class HomeAction {
    data object LogOut : HomeAction()
    data class CustomNavigate(val route: String) : HomeAction()
    data object SetNfcState : HomeAction()
}