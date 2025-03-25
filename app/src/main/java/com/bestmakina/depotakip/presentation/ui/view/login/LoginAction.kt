package com.bestmakina.depotakip.presentation.ui.view.login

sealed class LoginAction {
    data object ChangeLoadingStatus: LoginAction()
}