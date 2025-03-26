package com.bestmakina.depotakip.presentation.ui.view.nftregistration.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.bestmakina.depotakip.common.util.extension.toastShort
import com.bestmakina.depotakip.presentation.ui.view.nftregistration.NftRegistrationAction
import com.bestmakina.depotakip.presentation.ui.view.nftregistration.NftRegistrationEffect
import com.bestmakina.depotakip.presentation.ui.view.nftregistration.NftRegistrationState
import com.bestmakina.depotakip.presentation.ui.view.nftregistration.viewmodel.NftRegistrationViewModel

@Composable
fun NftRegistrationView(viewModel: NftRegistrationViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is NftRegistrationEffect.ShowToast -> context.toastShort(effect.message)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        MainContent(
            state = state,
            onInputChange = { viewModel.handleAction(NftRegistrationAction.SetInputData(it)) },
            onScanClick = { viewModel.handleAction(NftRegistrationAction.ScanButtonClick) },
            onCancelClick = { viewModel.handleAction(NftRegistrationAction.StartNfcScan) }
        )

        if (state.alertDialogVisibility) {
            NftConfirmationDialog(
                inputData = state.inputData,
                onConfirm = { viewModel.handleAction(NftRegistrationAction.StartNfcScan) },
                onDismiss = { viewModel.handleAction(NftRegistrationAction.ChangeAlertDialogVisibility) }
            )
        }
    }
}

@Composable
private fun MainContent(
    state: NftRegistrationState,
    onInputChange: (String) -> Unit,
    onScanClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "NFC Kaydı",
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
            color = MaterialTheme.colorScheme.onPrimary
        )

        OutlinedTextField(
            value = state.inputData,
            onValueChange = onInputChange,
            label = { Text("NFC Bilgisi Girin") },
            visualTransformation = VisualTransformation.None,
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color.White, shape = RoundedCornerShape(8.dp)),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            enabled = !state.isWaitingForNfcScan
        )

        Button(
            onClick = onScanClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            ),
            enabled = !state.isWaitingForNfcScan
        ) {
            Text("NFC Tara", fontSize = 16.sp)
        }

        AnimatedVisibility(visible = state.isWaitingForNfcScan) {
            NfcWaitingIndicator(onCancelClick = onCancelClick)
        }
    }
}

@Composable
private fun NfcWaitingIndicator(onCancelClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
        Text(
            "NFC Kartı Bekleniyor...",
            color = MaterialTheme.colorScheme.onPrimary
        )
        Button(
            onClick = onCancelClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red
            )
        ) {
            Text("İptal")
        }
    }
}

@Composable
private fun NftConfirmationDialog(
    inputData: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        containerColor = Color.Black,
        onDismissRequest = onDismiss,
        title = { Text("Onay", fontWeight = FontWeight.Bold) },
        text = { Text("$inputData değerini kartınıza yüklemek istiyor musunuz?") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Evet")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}