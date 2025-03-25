package com.bestmakina.depotakip.presentation.ui.view.login

import com.bestmakina.depotakip.data.model.request.personnel.PersonnelData

data class LoginState(
    val isLoading: Boolean = false,
    val personnelList: List<PersonnelData> = emptyList(),
    val nfcData : String = "",
    val waitingNfc : Boolean = false
)