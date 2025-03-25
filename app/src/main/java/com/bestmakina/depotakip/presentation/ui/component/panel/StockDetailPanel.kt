package com.bestmakina.depotakip.presentation.ui.component.panel

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.bestmakina.depotakip.R
import com.bestmakina.depotakip.common.util.extension.removeLeadingZeros
import com.bestmakina.depotakip.common.util.extension.toastShort
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.OutlinedTextField

@Composable
fun StockDetailPanel(
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
    onclick: (Int, String) -> Unit,
    onBackButtonClick: () -> Unit
) {

    val buttonClickable = remember { mutableStateOf(true) }
    var quantity by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

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

    var showConfirmationDialog by remember { mutableStateOf(false) }

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
                    montajaVerilen
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = {
                        if (buttonClickable.value) {
                            showConfirmationDialog = true
                        } else {
                            context.toastShort("Lütfen Barkod Okutunuz")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Transfer Et", color = Color.Black, fontSize = 16.sp)
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
                    // Yeni eklenen parametreler
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
                        .height(180.dp)
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
}

@Composable
fun TableStockInfo(
    stockCode: String,
    barcode: String,
    shelfCode: String,
    stockName: String,
    recipeAmount: Int,
    upperDepotAmount: Int,
    lowerDepotAmount: Int,
    virtualSafe: Int,
    montajaVerilen: Int
) {
    val stockInfo = listOf(
        "StokKodu" to stockCode,
        "BarkodNo" to barcode,
        "RafKodu" to shelfCode,
        "SanalKasa" to virtualSafe.toString(),
        "ÜstDepo" to upperDepotAmount.toString(),
        "AltDepo" to lowerDepotAmount.toString(),
        "Reçete" to recipeAmount.toString(),
        "Montaja Verilen" to montajaVerilen.toString(),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        for (i in stockInfo.indices step 2) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (j in 0 until 2) {
                    if (i + j < stockInfo.size) {
                        val (label, value) = stockInfo[i + j]
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(1.dp)
                                .border(width = 1.dp, color = Color.Black)
                        ) {
                            Text(
                                textAlign = TextAlign.Center,
                                text = label,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.Black,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Color(0xB39A9A9A),
                                    )
                                    .padding(vertical = 6.dp, horizontal = 6.dp)
                            )
                            Text(
                                textAlign = TextAlign.Center,
                                text = value.removeLeadingZeros().ifEmpty { "0" },
                                style = MaterialTheme.typography.titleMedium,
                                color = if (label == "Reçete" || label == "ÜstDepo") Color.Red else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.tertiary)
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stockName,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun QuantitySelector(
    quantity: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    recipeAmount: Int,
    upperDepotAmount: Int,
    montajaVerilen: Int,
    onQuantityChanged: (Int) -> Unit
) {
    val context = LocalContext.current
    var isEditing by remember { mutableStateOf(false) }
    var editText by remember { mutableStateOf(quantity.toString()) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(55.dp)
                .padding(4.dp)
                .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
                .clickable { onDecrease() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.bottom_arrow),
                contentDescription = "Decrease",
                tint = Color.Black,
            )
        }
        Spacer(modifier = Modifier.width(16.dp))

        if (isEditing) {
            OutlinedTextField(
                value = editText,
                onValueChange = { newValue ->
                    // Sadece rakamları kabul et
                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                        editText = newValue
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .width(100.dp)
                    .height(60.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Red,
                    unfocusedTextColor = Color.Red,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = Color.Gray
                ),
                textStyle = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
            )
        } else {
            Text(
                text = quantity.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red,
                modifier = Modifier
                    .clickable { isEditing = true }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Box(
            modifier = Modifier
                .size(55.dp)
                .padding(4.dp)
                .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
                .clickable { onIncrease() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.up_arrow),
                contentDescription = "Increase",
                tint = Color.Black,
            )
        }
    }

    if (isEditing) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { isEditing = false },
                modifier = Modifier.padding(end = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("İptal")
            }

            Button(
                onClick = {
                    val newValue = editText.toIntOrNull() ?: 0
                    val validatedValue = validateQuantity(
                        newValue = newValue,
                        recipeAmount = recipeAmount,
                        upperDepotAmount = upperDepotAmount,
                        montajaVerilen = montajaVerilen,
                        context = context
                    )

                    if (validatedValue != -1) {
                        editText = validatedValue.toString()
                        isEditing = false
                        onQuantityChanged(validatedValue)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Tamam")
            }
        }
    }
}

private fun validateQuantity(
    newValue: Int,
    recipeAmount: Int,
    upperDepotAmount: Int,
    montajaVerilen: Int,
    context: Context
): Int {
    return when {
        newValue < 0 -> {
            context.toastShort("Miktar 0'dan küçük olamaz")
            0
        }

        newValue > recipeAmount -> {
            context.toastShort("Miktar reçete miktarından fazla olamaz")
            -1
        }

        newValue > upperDepotAmount -> {
            context.toastShort("Miktar stok adetinden fazla olamaz")
            -1
        }

        newValue > (recipeAmount - montajaVerilen) -> {
            context.toastShort("Daha fazla transfer yapamazsınız")
            -1
        }

        else -> {
            newValue
        }
    }
}