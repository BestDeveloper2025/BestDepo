package com.bestmakina.depotakip.presentation.ui.view.login.view

import LoginButton
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.bestmakina.depotakip.R
import com.bestmakina.depotakip.BarcodeViewModel
import com.bestmakina.depotakip.common.util.extension.toastShort
import com.bestmakina.depotakip.presentation.navigation.Screens
import com.bestmakina.depotakip.presentation.ui.view.login.LoginAction
import com.bestmakina.depotakip.presentation.ui.view.login.LoginEffect
import com.bestmakina.depotakip.presentation.ui.view.login.viewmodel.LoginViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginView(
    navController: NavHostController,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state = loginViewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        loginViewModel.effect.collectLatest { effect ->
            when (effect) {
                is LoginEffect.ShowToast -> {
                    context.toastShort(effect.message)
                }
                is LoginEffect.NavigateTo -> {
                    Toast.makeText(context, "Giriş Başarılı", Toast.LENGTH_SHORT).show()
                    navController.navigate(effect.destination) {
                        popUpTo(Screens.SplashScreen.route) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.bst_logo_small),
                contentDescription = null,
                Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.height(120.dp))
            LoginButton(
                onClick = {
                    loginViewModel.handleAction(LoginAction.ChangeLoadingStatus)
                }
            )
        }

        AnimatedVisibility(visible = state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = Color.White)
                }

            }
        }
        AnimatedVisibility(visible = state.waitingNfc) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = Color.White)
                    Text(
                        "Lütfen Kartınızı Okutunuz..."
                    )
                }

            }
        }
    }
}