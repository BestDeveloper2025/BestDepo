package com.bestmakina.depotakip.presentation.ui.component.button

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun CustomButton(onClick: () -> Unit = {}, title: String) {
    ElevatedButton(
        modifier = Modifier.width(220.dp).padding(horizontal = 22.dp).height(70.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary),
            textAlign = TextAlign.Center
        )
    }
}