package com.bestmakina.depotakip.presentation.ui.view.login

sealed class LoginEffect {
    data class ShowToast(val message: String) : LoginEffect()
    data class NavigateTo(val destination: String) : LoginEffect()
}