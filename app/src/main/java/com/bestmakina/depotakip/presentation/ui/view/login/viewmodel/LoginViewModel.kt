package com.bestmakina.depotakip.presentation.ui.view.login.viewmodel

import android.content.Context
import android.nfc.Tag
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.bestmakina.depotakip.common.base.BaseNfcViewModel
import com.bestmakina.depotakip.common.network.NetworkResult
import com.bestmakina.depotakip.data.local.SharedPreferencesHelper
import com.bestmakina.depotakip.domain.manager.BarcodeManager
import com.bestmakina.depotakip.domain.manager.NfcManager
import com.bestmakina.depotakip.domain.model.PreferencesKeys
import com.bestmakina.depotakip.domain.usecase.personnel.GetNameByBarcodeUseCase
import com.bestmakina.depotakip.domain.usecase.personnel.GetPersonnelUseCase
import com.bestmakina.depotakip.presentation.ui.view.login.LoginAction
import com.bestmakina.depotakip.presentation.ui.view.login.LoginEffect
import com.bestmakina.depotakip.presentation.ui.view.login.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val getPersonnelUseCase: GetPersonnelUseCase,
    private val getNameToBarcodeUseCase: GetNameByBarcodeUseCase,
    private val sharedPreferencesHelper: SharedPreferencesHelper,
    private val nfcManager: NfcManager,
    @ApplicationContext private val context: Context
) : BaseNfcViewModel(nfcManager) {

    private val barcodeManager = BarcodeManager(context)

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<LoginEffect>()
    val effect = _effect.asSharedFlow()

    private val _tagData = MutableStateFlow<String?>(null)
    private var barcodeJob: Job? = null


    fun handleAction(action: LoginAction) {
        when (action) {
            is LoginAction.ChangeLoadingStatus -> changeLoadingStatus()
        }
    }

    init {
        handleBarcodeData()
        preparePage()
        observeNfcTags()
    }

    private fun handleBarcodeData() {
        barcodeJob?.cancel()
        barcodeJob = viewModelScope.launch {
            barcodeManager.barcodeData
                .filterNotNull()
                .filterNot { it.isEmpty() }
                .collect { barcode ->
                    if (barcode.isNotEmpty()) {
                        getNameByBarcode(barcode)
                    }
                    barcodeManager.clearBarcodeData()
                }
        }
    }

    private fun preparePage() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            fetchUsers()
            delay(500)
            getExistingUser()
        }
    }

    private suspend fun fetchUsers() {
        getPersonnelUseCase().collectLatest { result ->
            when (result) {
                is NetworkResult.Loading -> Log.d("LoginViewModel", "fetchUsers: Loading")
                is NetworkResult.Success -> {
                    val personnelList = result.data?.sorumluPersonel ?: emptyList()
                    _state.value = _state.value.copy(personnelList = personnelList)
                    Log.d("LoginViewModel", "fetchUsers: Success, personnelList size: ${personnelList.size}")
                }
                is NetworkResult.Error -> {
                    _state.value = _state.value.copy(isLoading = false)
                    Log.d("LoginViewModel", "fetchUsers: Error, message: ${result.message}")
                }
            }
        }
    }

    private fun getExistingUser() {
        viewModelScope.launch {
            fetchUsers()
            val userId = sharedPreferencesHelper.getData(PreferencesKeys.UserId)
            if (userId.isNotEmpty()) {
                getNameByBarcode(userId)
            } else {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    private fun getNameByBarcode(barcode: String) {
        Log.d("LoginViewModel", "getNameByBarcode: $barcode")
        viewModelScope.launch {
            getNameToBarcodeUseCase(barcode).collectLatest { result ->
                when (result) {
                    is NetworkResult.Loading -> _state.value = _state.value.copy(isLoading = true)
                    is NetworkResult.Success -> {
                        result.data?.let {
                            Log.d("LoginViewModel", "getNameByBarcode: Success, personnelName: ${it.personnelAdi}")
                            checkPersonnel(it.personnelAdi, barcode)
                            _state.value = _state.value.copy(isLoading = false)
                        }
                    }
                    is NetworkResult.Error -> {
                        _state.value = _state.value.copy(isLoading = false)
                        Log.d("LoginViewModel", "getNameByBarcode: Error, message: ${result.message}")
                        _effect.emit(LoginEffect.ShowToast("Barkod okuma hatası: ${result.message}"))
                    }
                }
            }
        }
    }

    private fun checkPersonnel(personnelName: String, barcodeData: String) {
        val exist = _state.value.personnelList.any { it.Adi == personnelName }
        Log.d("LoginViewModel", "checkPersonnel: $exist, $personnelName, $barcodeData")
        viewModelScope.launch {
            if (exist) {
                sharedPreferencesHelper.saveData(PreferencesKeys.UserName, personnelName)
                sharedPreferencesHelper.saveData(PreferencesKeys.UserId, barcodeData)
                _effect.emit(LoginEffect.NavigateTo("home"))
            } else {
                _state.value = _state.value.copy(isLoading = false)
                _effect.emit(LoginEffect.ShowToast("Personel bulunamadı"))
            }
        }
    }

    override fun onTagDetected(tag: Tag) {
        viewModelScope.launch {
            readFromTag(tag)
        }
    }

    private fun readFromTag(tag: Tag) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            readFromTagAndLog(tag).fold(
                onSuccess = { data ->
                    _tagData.value = data
                    _effect.emit(LoginEffect.ShowToast("Kart Okundu"))
                    _state.value = _state.value.copy(
                        isLoading = false,
                        nfcData = data.removePrefix("0")
                    )
                    getNameByBarcode(data)
                },
                onFailure = { error ->
                    _effect.emit(LoginEffect.ShowToast("Okuma hatası: ${error.message}"))
                    _state.value = _state.value.copy(isLoading = false)
                }
            )
        }
    }

    private fun changeLoadingStatus(){
        _state.value = _state.value.copy(waitingNfc = !_state.value.isLoading)
    }

    override fun onCleared() {
        super.onCleared()
        barcodeJob?.cancel()
        barcodeManager.clearBarcodeData()
    }

}