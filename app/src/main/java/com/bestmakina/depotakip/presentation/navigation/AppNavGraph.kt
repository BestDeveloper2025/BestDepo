package com.bestmakina.depotakip.presentation.navigation

import TransferWithReceteView
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bestmakina.depotakip.presentation.ui.view.bulkTransfer.view.BulkTransferView
import com.bestmakina.depotakip.presentation.ui.view.home.view.HomeView
import com.bestmakina.depotakip.presentation.ui.view.login.view.LoginView
import com.bestmakina.depotakip.presentation.ui.view.nftregistration.view.NftRegistrationView
import com.bestmakina.depotakip.presentation.ui.view.splash.view.SplashView
import com.bestmakina.depotakip.presentation.ui.view.stocktaking.view.StockTakingView

@Composable
fun AppNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Screens.SplashScreen.route
    ) {
        composable(Screens.SplashScreen.route) {
            SplashView(navController)
        }
        composable(Screens.LoginScreen.route) {
            LoginView(navController)
        }
        composable(Screens.HomeScreen.route) {
            HomeView(navController)
        }
        composable(Screens.StockTakingScreen.route) {
            StockTakingView(navController)
            BackHandler {
                navController.navigate("home") {
                    popUpTo("stockTaking") { inclusive = true }
                }
            }
        }
        composable(Screens.TransferWithReceteScreen.route) {
            TransferWithReceteView(navController)
            BackHandler {
                navController.navigate("home") {
                    popUpTo("transferWithRecete") { inclusive = true }
                }
            }
        }
        composable(Screens.NfcRegistrationScreen.route) {
            NftRegistrationView(navController)
            BackHandler {
                navController.navigate("home") {
                    popUpTo("nfcRegistration") { inclusive = true }
                }
            }
        }
        composable(Screens.BulkTransferScreen.route) {
            BulkTransferView(navController)
            BackHandler {
                navController.navigate("home") {
                    popUpTo("bulkTransfer") { inclusive = true }
                }
            }
        }
    }
}