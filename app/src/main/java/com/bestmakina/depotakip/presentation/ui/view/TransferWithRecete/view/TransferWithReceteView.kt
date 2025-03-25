import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.bestmakina.depotakip.common.model.DropdownType
import com.bestmakina.depotakip.presentation.ui.component.button.CustomButton
import com.bestmakina.depotakip.presentation.ui.component.card.ExpandableCard
import com.bestmakina.depotakip.presentation.ui.component.panel.SearchablePanel
import com.bestmakina.depotakip.presentation.ui.component.panel.StockDetailPanel
import com.bestmakina.depotakip.presentation.ui.view.TransferWithRecete.TransferWithReceteAction
import com.bestmakina.depotakip.presentation.ui.view.TransferWithRecete.TransferWithReceteEffect
import com.bestmakina.depotakip.presentation.ui.view.TransferWithRecete.viewmodel.TransferWithReceteViewModel
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import com.bestmakina.depotakip.common.model.TransferItemModel
import com.bestmakina.depotakip.common.util.extension.toastShort
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TransferWithReceteView(
    navController: NavHostController,
    transferWithReceteViewModel: TransferWithReceteViewModel = hiltViewModel()
) {
    val state = transferWithReceteViewModel.state.collectAsStateWithLifecycle().value
    val context = LocalContext.current
    var searchablePanelItems by remember { mutableStateOf(emptyList<TransferItemModel>()) }

    LaunchedEffect(Unit) {
        transferWithReceteViewModel.effect.collectLatest { effect ->
            when (effect) {
                is TransferWithReceteEffect.ShowToast -> {
                    context.toastShort(effect.message)
                }
                is TransferWithReceteEffect.ShowSearchablePanel -> {
                    searchablePanelItems = effect.transferItemModelList
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        transferWithReceteViewModel.handleAction(TransferWithReceteAction.PreparePage)
    }

    AnimatedVisibility(visible = state.showSearchablePanel) {
        SearchablePanel(
            items = searchablePanelItems,
            selectedId = state.barcodeData,
            onItemSelected = {
                transferWithReceteViewModel.handleAction(TransferWithReceteAction.LoadItemData(it))
            },
            onDismiss = {
                transferWithReceteViewModel.handleAction(TransferWithReceteAction.ChangeListPanelVisibility)
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
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
                    state.userName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.W500)
                )
                Text(
                    state.warehouseName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.W500)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            ExpandableCard(
                selectedItem = state.selectedPersonnel?.name ?: "",
                onclick = {
                    transferWithReceteViewModel.handleAction(TransferWithReceteAction.ToggleDropdown(DropdownType.PERSONNEL))
                }
            )
            ExpandableCard(
                selectedItem = state.selectedTransferNedeni?.name ?: "",
                onclick = {
                    transferWithReceteViewModel.handleAction(TransferWithReceteAction.ToggleDropdown(DropdownType.TRANSFER_NEDENI))
                }
            )
            ExpandableCard(
                selectedItem = state.selectedMachine?.name ?: "",
                onclick = {
                    transferWithReceteViewModel.handleAction(TransferWithReceteAction.ToggleDropdown(DropdownType.MACHINE))
                }
            )
        }

        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier.fillMaxSize().padding(bottom = 50.dp)
        ) {
            CustomButton(
                title = "Transfer Yap",
                onClick = {
                    transferWithReceteViewModel.handleAction(TransferWithReceteAction.ButtonClick)
                }
            )
        }

        AnimatedVisibility(visible = state.panelVisibility) {
            StockDetailPanel(
                stockCode = state.panelData?.stokKodu ?: "",
                barcode = state.panelData?.barkodNo ?: "",
                shelfCode = state.panelData?.rafKodu ?: "",
                stockName = state.panelData?.urunAdi ?: "",
                recipeAmount = state.panelData?.receteMiktari?.toInt() ?: 0,
                upperDepotAmount = state.panelData?.ustDepoAdet?.toInt() ?: 0,
                lowerDepotAmount = state.panelData?.altDepoAdet?.toInt() ?: 0,
                virtualSafe = state.panelData?.sanalKasa?.toInt() ?: 0,
                imageData = state.panelData?.resimData ?: "",
                montajaVerilen = state.panelData?.montajaVerilen ?: 0,
                onclick = { quantity, stockCode ->
                    transferWithReceteViewModel.handleAction(
                        TransferWithReceteAction.TransferButtonClick(quantity, stockCode)
                    )
                },
                onBackButtonClick = {
                    transferWithReceteViewModel.handleAction(
                        TransferWithReceteAction.CloseDetailPanel
                    )
                }
            )
        }
        AnimatedVisibility(visible = state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}