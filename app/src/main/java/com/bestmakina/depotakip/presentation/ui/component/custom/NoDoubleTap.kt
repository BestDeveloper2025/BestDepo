package com.bestmakina.depotakip.presentation.ui.component.custom

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun NoDoubleTapZoom(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { /* Çift tıklamayı yut, hiçbir şey yapma */ }
                )
            }
    ) {
        content()
    }
}
