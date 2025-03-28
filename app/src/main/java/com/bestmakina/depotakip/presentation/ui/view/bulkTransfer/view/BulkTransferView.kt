package com.bestmakina.depotakip.presentation.ui.view.bulkTransfer.view

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.bestmakina.depotakip.common.model.DropdownType
import com.bestmakina.depotakip.common.util.extension.toastShort
import com.bestmakina.depotakip.presentation.ui.component.button.CustomButton
import com.bestmakina.depotakip.presentation.ui.component.card.ExpandableCard
import com.bestmakina.depotakip.presentation.ui.component.panel.BulkStockDetailPanel
import com.bestmakina.depotakip.presentation.ui.component.panel.SearchablePanel
import com.bestmakina.depotakip.presentation.ui.component.panel.StockDetailPanel
import com.bestmakina.depotakip.presentation.ui.view.TransferWithRecete.TransferWithReceteAction
import com.bestmakina.depotakip.presentation.ui.view.bulkTransfer.BulkTransferAction
import com.bestmakina.depotakip.presentation.ui.view.bulkTransfer.BulkTransferEffect
import com.bestmakina.depotakip.presentation.ui.view.bulkTransfer.viewmodel.BulkTransferViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun BulkTransferView(
    navController: NavHostController,
    viewModel: BulkTransferViewModel = hiltViewModel()
) {

    val state = viewModel.state.collectAsStateWithLifecycle().value
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is BulkTransferEffect.ShowToast -> context.toastShort(effect.message)
            }
        }
    }

    AnimatedVisibility(visible = state.searchablePanelVisibility) {
        SearchablePanel(
            items = state.machineList,
            selectedId = null,
            onItemSelected = {
                viewModel.handleAction(BulkTransferAction.LoadMachineData(it))
            },
            onDismiss = {
                viewModel.handleAction(BulkTransferAction.ChangePanelVisibility)
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 36.dp, start = 12.dp, end = 12.dp, bottom = 24.dp)
        ) {
            Text(
                "Reçeteden Transfer",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    state.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.W500)
                )
                Text(
                    state.wareHouseName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.W500)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            ExpandableCard(
                selectedItem = state.selectedPersonnel?.name ?: "",
                onclick = {
                    Log.d("BulkTransferView", "BulkTransferView: $state")
                }
            )
            if (state.isNfcEnabled) {
                ExpandableCard(
                    selectedItem = state.selectedMachine?.name ?: "",
                    onclick = {
                        viewModel.handleAction(BulkTransferAction.ChangePanelVisibility)
                    }
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 74.dp)
        ) {
            CustomButton(
                title = "Transferi Başlat",
                onClick = { viewModel.handleAction(BulkTransferAction.StartTransferButtonClick) }
            )
        }
        AnimatedVisibility(visible = state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .clickable(enabled = false) { },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,

                    ) {
                    CircularProgressIndicator(color = Color.White)
                    if (!state.isNfcEnabled) {
                        Text(
                            "İşlemlere Başlamadan Önce Lütfen Kartınızı Okutunuz...",
                            textAlign = TextAlign.Center
                        )
                    } else if (state.selectedPersonnel?.id!!.isEmpty()) {
                        Text(
                            "Kartınız Okundu Lütfen Bekleyiniz...",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        AnimatedVisibility(visible = state.detailPanelVisibility) {
            BulkStockDetailPanel(
                stockCode = state.currentProductDetail?.stokKodu ?: "",
                barcode = state.currentProductDetail?.barkodNo ?: "",
                shelfCode = state.currentProductDetail?.rafKodu ?: "",
                stockName = state.currentProductDetail?.urunAdi ?: "",
                recipeAmount = state.currentProductDetail?.receteMiktari?.toInt() ?: 0,
                upperDepotAmount = state.currentProductDetail?.ustDepoAdet?.toInt() ?: 0,
                lowerDepotAmount = state.currentProductDetail?.altDepoAdet?.toInt() ?: 0,
                virtualSafe = state.currentProductDetail?.sanalKasa?.toInt() ?: 0,
                imageData = state.currentProductDetail?.resimData ?: "",
                montajaVerilen = state.currentProductDetail?.montajaVerilen ?: 0,
                onclick = { quantity, stockCode ->
                    viewModel.handleAction(BulkTransferAction.TransferProduct(quantity, stockCode))
                },
                onBackButtonClick = {
                    viewModel.handleAction(BulkTransferAction.CloseDetailPanel)
                },
                onCreateOrderButtonClick = { orderAmount , stockCode->
                    viewModel.handleAction(BulkTransferAction.OnCreateOrderButtonClick(orderAmount, stockCode))
                }
            )
        }
    }
}