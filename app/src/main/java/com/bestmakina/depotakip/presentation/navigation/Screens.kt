package com.bestmakina.depotakip.presentation.navigation

sealed class Screens (val route: String){
    data object SplashScreen : Screens("splash")
    data object LoginScreen : Screens("login")
    data object HomeScreen : Screens("home")
    data object StockTakingScreen : Screens("stockTaking")
    data object TransferWithReceteScreen : Screens("transferWithRecete")
    data object NfcRegistrationScreen : Screens("nfcRegistration")
    data object BulkTransferScreen : Screens("bulkTransfer")
}