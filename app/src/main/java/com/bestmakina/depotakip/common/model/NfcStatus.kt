package com.bestmakina.depotakip.common.model

sealed class NfcStatus {
    data object NotInitialized : NfcStatus()
    data object NotSupported : NfcStatus()
    data object Disabled : NfcStatus()
    data object Available : NfcStatus()
}