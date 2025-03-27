package com.bestmakina.depotakip.presentation.ui.component.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bestmakina.depotakip.common.util.extension.removeLeadingZeros

@Composable
fun TableStockInfo(
    stockCode: String? = null,
    barcode: String? = null,
    shelfCode: String? = null,
    stockName: String? = null,
    recipeAmount: Int? = null,
    upperDepotAmount: Int? = null,
    lowerDepotAmount: Int? = null,
    virtualSafe: Int? = null,
    montajaVerilen: Int? = null,
    minStock: Int? = null
) {
    val stockInfo = listOfNotNull(
        stockCode?.let { "StokKodu" to it },
        barcode?.let { "BarkodNo" to it },
        shelfCode?.let { "RafKodu" to it },
        virtualSafe?.toString()?.let { "SanalKasa" to it },
        upperDepotAmount?.toString()?.let { "ÜstDepo" to it },
        lowerDepotAmount?.toString()?.let { "AltDepo" to it },
        recipeAmount?.toString()?.let { "Reçete" to it },
        montajaVerilen?.toString()?.let { "Montaja Verilen" to it },
        minStock?.toString()?.let { "Minimum Stok" to it }
    )

    if (stockInfo.isEmpty() && stockName.isNullOrBlank()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        for (i in stockInfo.indices step 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                                    .background(Color(0xB39A9A9A))
                                    .padding(vertical = 1.dp, horizontal = 2.dp)
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
                                    .padding(vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }

        stockName?.let {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 1.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        it,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White
                    )
                }
            }
        }
    }
}