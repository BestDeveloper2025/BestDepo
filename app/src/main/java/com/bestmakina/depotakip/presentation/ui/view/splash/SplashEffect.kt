package com.bestmakina.depotakip.presentation.ui.view.splash

sealed class SplashEffect {
    data class ShowToast(val message: String) : SplashEffect()
    data class NavigateTo(val destination: String) : SplashEffect()
}