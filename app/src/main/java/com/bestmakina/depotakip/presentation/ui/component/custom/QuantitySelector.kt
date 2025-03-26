package com.bestmakina.depotakip.presentation.ui.component.custom

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bestmakina.depotakip.R
import com.bestmakina.depotakip.common.util.extension.toastShort

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