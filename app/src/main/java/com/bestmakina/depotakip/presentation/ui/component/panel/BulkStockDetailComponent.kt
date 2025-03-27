package com.bestmakina.depotakip.presentation.ui.component.panel


import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.bestmakina.depotakip.R
import com.bestmakina.depotakip.common.util.extension.toastShort
import com.bestmakina.depotakip.presentation.ui.component.custom.QuantitySelector
import com.bestmakina.depotakip.presentation.ui.component.custom.TableStockInfo

@Composable
fun BulkStockDetailPanel(
    stockCode: String,
    barcode: String,
    shelfCode: String,
    stockName: String,
    recipeAmount: Int,
    upperDepotAmount: Int,
    lowerDepotAmount: Int,
    virtualSafe: Int,
    imageData: String,
    montajaVerilen: Int,
    minStock: Int = 0,
    onclick: (Int, String) -> Unit,
    onBackButtonClick: () -> Unit,
    onCreateOrderButtonClick: () -> Unit
) {

    val buttonClickable = remember { mutableStateOf(true) }
    var quantity by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showCreateOrderDialog by remember { mutableStateOf(false) }

    val currentStockCode by rememberUpdatedState(stockCode)
    val currentBarcode by rememberUpdatedState(barcode)
    val currentShelfCode by rememberUpdatedState(shelfCode)
    val currentStockName by rememberUpdatedState(stockName)
    val currentRecipeAmount by rememberUpdatedState(recipeAmount)
    val currentUpperDepotAmount by rememberUpdatedState(upperDepotAmount)
    val currentLowerDepotAmount by rememberUpdatedState(lowerDepotAmount)
    val currentVirtualSafe by rememberUpdatedState(virtualSafe)
    val currentImageData by rememberUpdatedState(imageData)
    val montajaVerilen by rememberUpdatedState(montajaVerilen)
    val currentMinStock by rememberUpdatedState(minStock)

    LaunchedEffect(
        currentStockCode, currentBarcode, currentShelfCode,
        currentStockName, currentRecipeAmount, currentUpperDepotAmount,
        currentLowerDepotAmount, currentVirtualSafe
    ) {
        buttonClickable.value = true
        quantity = 0
    }

    val imageBitmap = remember(currentImageData) {
        try {
            val bytes = android.util.Base64.decode(currentImageData, android.util.Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
        } catch (e: Exception) {
            context.toastShort("Resim Yüklenirken Hata Oluştu")
            null
        }
    }



    Dialog(
        onDismissRequest = onBackButtonClick,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
                .zIndex(10f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 1.dp),
            ) {
                Spacer(modifier = Modifier.height(2.dp))
                TableStockInfo(
                    currentStockCode,
                    currentBarcode,
                    currentShelfCode,
                    currentStockName,
                    currentRecipeAmount,
                    currentUpperDepotAmount,
                    currentLowerDepotAmount,
                    currentVirtualSafe,
                    montajaVerilen,
                    currentMinStock
                )
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            showCreateOrderDialog = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 1.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Sipariş Oluştur", color = Color.Black, fontSize = 16.sp)
                    }
                    Button(
                        onClick = {
                            showConfirmationDialog = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 1.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Transfer Et", color = Color.Black, fontSize = 16.sp)
                    }
                }
                QuantitySelector(
                    quantity = quantity,
                    onDecrease = {
                        if (quantity > 0) quantity--
                        else {
                            context.toastShort("Miktar 0'dan küçük olamaz")
                        }
                    },
                    onIncrease = {
                        when {
                            quantity >= currentRecipeAmount -> {
                                context.toastShort("Miktar reçete miktarından fazla olamaz")
                            }

                            quantity >= upperDepotAmount -> {
                                context.toastShort("Miktar stok adetinden fazla olamaz")
                            }

                            quantity >= (currentRecipeAmount - montajaVerilen) -> {
                                context.toastShort("Daha fazla transfer yapamazsınız")
                            }

                            else -> {
                                quantity++
                            }
                        }
                    },
                    recipeAmount = currentRecipeAmount,
                    upperDepotAmount = currentUpperDepotAmount,
                    montajaVerilen = montajaVerilen,
                    onQuantityChanged = { newQuantity ->
                        quantity = newQuantity
                    }
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                        .border(width = 1.dp, color = Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    imageBitmap?.let {
                        Image(
                            bitmap = it,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    } ?: Text("Resim yüklenemedi", color = Color.Red)
                }
            }
        }
    }

    AnimatedVisibility(visible = showConfirmationDialog) {
        AlertDialog(
            containerColor = Color.Black,
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text("Onay", fontWeight = FontWeight.Bold) },
            text = { Text("$quantity adet $currentStockName transferini onaylıyor musunuz?") },
            confirmButton = {
                Button(
                    onClick = {
                        if (quantity == 0) {
                            context.toastShort("Lütfen miktar giriniz")
                            showConfirmationDialog = false
                        } else {
                            buttonClickable.value = false
                            showConfirmationDialog = false
                            onclick(quantity, currentStockCode)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Evet")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showConfirmationDialog = false }
                ) {
                    Text("İptal")
                }
            }
        )
    }

    AnimatedVisibility(visible = showCreateOrderDialog) {
        AlertDialog(
            containerColor = Color.Black,
            onDismissRequest = { showCreateOrderDialog = false },
            title = { Text("Onay", fontWeight = FontWeight.Bold) },
            text = { Text("$quantity adet $currentStockName Sipariş Oluşturmak İstiyor Musunuz?") },
            confirmButton = {
                Button(
                    onClick = { onCreateOrderButtonClick() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Evet")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showCreateOrderDialog = false }
                ) {
                    Text("İptal")
                }
            }
        )
    }
}