package com.bestmakina.depotakip.presentation.ui.component.panel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.bestmakina.depotakip.common.util.extension.toastShort
import com.bestmakina.depotakip.presentation.ui.component.custom.QuantitySelector
import com.bestmakina.depotakip.presentation.ui.component.custom.TableStockInfo

@Composable
fun CreateOrderPanel(
    stockCode: String,
    barcode: String,
    stockName: String,
    upperDepotAmount: Int,
    lowerDepotAmount: Int,
    minStock: Int,
    onCreateOrderButtonClick:(Int, Int) -> Unit
) {
    val context = LocalContext.current

    val currentStockCode by rememberUpdatedState(stockCode)
    val currentBarcode by rememberUpdatedState(barcode)
    val currentStockName by rememberUpdatedState(stockName)
    val currentUpperDepotAmount by rememberUpdatedState(upperDepotAmount)
    val currentLowerDepotAmount by rememberUpdatedState(lowerDepotAmount)
    val currentMinStock by rememberUpdatedState(minStock)

    var manufacturingQuantity by remember { mutableIntStateOf(0) }
    var purchaseQuantity by remember { mutableIntStateOf(0) }

    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
                .zIndex(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(1.dp)
            ) {
                TableStockInfo(
                    stockCode = currentStockCode,
                    barcode = currentBarcode,
                    stockName = currentStockName,
                    upperDepotAmount = currentUpperDepotAmount,
                    lowerDepotAmount = currentLowerDepotAmount,
                    minStock = currentMinStock
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "İmalata Verilecek Miktar",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                QuantitySelector(
                    quantity = manufacturingQuantity,
                    onDecrease = {
                        if (manufacturingQuantity > 0) manufacturingQuantity--
                    },
                    onIncrease = {
                        manufacturingQuantity++
                    },
                    onQuantityChanged = { newQuantity ->
                        manufacturingQuantity = newQuantity
                    },
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Satın Almaya Verilecek Miktar",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                QuantitySelector(
                    quantity = purchaseQuantity,
                    onDecrease = {
                        if (purchaseQuantity > 0) purchaseQuantity--
                    },
                    onIncrease = {
                        purchaseQuantity++
                    },
                    onQuantityChanged = { newQuantity ->
                        purchaseQuantity = newQuantity
                    },
                )
                Button(
                    onClick = { onCreateOrderButtonClick(manufacturingQuantity, purchaseQuantity) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Sipariş Oluştur",
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun CreateOrderPanelPreview() {
    CreateOrderPanel(
        stockCode = "stockCode",
        barcode = "barcode",
        upperDepotAmount = 1,
        lowerDepotAmount = 1,
        minStock = 1,
        stockName = "ksfdg sd fgs gjsd gjşlsdfgj lşsjdflgl",
        onCreateOrderButtonClick = { _, _ -> }
    )
}