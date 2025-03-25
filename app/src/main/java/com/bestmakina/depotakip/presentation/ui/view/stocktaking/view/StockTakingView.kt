package com.bestmakina.depotakip.presentation.ui.view.stocktaking.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun StockTakingView(
    navHostController: NavHostController,
    //stockTakingViewModel: StockTakingViewModel = hiltViewModel()
) {


    /* val barcode by stockTakingViewModel.barcodeData.collectAsState()

     LaunchedEffect(barcode) {
         Log.d("StockTakingView", "Barcode: $barcode")
     }

     DisposableEffect(Unit) {
         onDispose {
             stockTakingViewModel.stopScanning()
         }
     }

     */

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary)
            .padding(12.dp)

    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {


        }
    }
}



