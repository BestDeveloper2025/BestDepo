package com.bestmakina.depotakip.presentation.ui.view.nftregistration

sealed class NftRegistrationEffect {
    data class ShowToast(val message: String) : NftRegistrationEffect()
}