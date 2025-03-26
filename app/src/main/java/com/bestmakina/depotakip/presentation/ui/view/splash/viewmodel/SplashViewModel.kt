package com.bestmakina.depotakip.presentation.ui.view.splash.viewmodel

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestmakina.depotakip.common.network.NetworkResult
import com.bestmakina.depotakip.data.local.SharedPreferencesHelper
import com.bestmakina.depotakip.data.local.entity.MachineDataEntity
import com.bestmakina.depotakip.data.local.entity.RecipientEntity
import com.bestmakina.depotakip.data.local.entity.TransferReasonEntity
import com.bestmakina.depotakip.data.model.request.device.CheckDeviceStatusRequest
import com.bestmakina.depotakip.domain.model.PreferencesKeys
import com.bestmakina.depotakip.domain.usecase.device.CheckDeviceStatusUseCase
import com.bestmakina.depotakip.domain.usecase.inventory.GetMachineListUseCase
import com.bestmakina.depotakip.domain.usecase.personnel.GetTeslimAlanUseCase
import com.bestmakina.depotakip.domain.usecase.GetTransferNedeniUseCase
import com.bestmakina.depotakip.domain.usecase.cache.SaveAllMachineDataUseCase
import com.bestmakina.depotakip.domain.usecase.cache.SaveAllRecipientUseCase
import com.bestmakina.depotakip.domain.usecase.cache.SaveAllTransferReasonUseCase
import com.bestmakina.depotakip.presentation.ui.view.splash.SplashEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.net.NetworkInterface
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.system.exitProcess

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val checkDeviceStatusUseCase: CheckDeviceStatusUseCase,
    private val sharedPreferences: SharedPreferencesHelper,
    private val getTeslimAlanUseCase: GetTeslimAlanUseCase,
    private val getTransferNedeniUseCase: GetTransferNedeniUseCase,
    private val getMachineListUseCase: GetMachineListUseCase,
    private val saveAllTransferReasonUseCase: SaveAllTransferReasonUseCase,
    private val saveAllMachineDataUseCase: SaveAllMachineDataUseCase,
    private val saveAllRecipientUseCase: SaveAllRecipientUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _effect = MutableSharedFlow<SplashEffect>()
    val effect = _effect.asSharedFlow()

    init {
        Log.d("SplashViewModel", createDeviceStatus().toString())
        checkDeviceStatus() // Sadece device status kontrolü
    }

    private fun createDeviceStatus(): CheckDeviceStatusRequest {
        val displayMetrics = getDisplayMetrics()
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels

        return CheckDeviceStatusRequest(
            cozunurluk = "H:$height W:$width",
            IPAdresi = getIPAddress(),
            versionNo = "2.0.9",
            terminalAdi = getDeviceName(),
            terminalId = getDeviceUniqueId()
        )
    }

    private fun getDisplayMetrics(): DisplayMetrics {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics
    }

    private fun getIPAddress(): String {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                val addresses = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    if (!address.isLoopbackAddress && address.hostAddress.indexOf(':') < 0) {
                        return address.hostAddress
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "0.0.0.0"
    }

    private fun getAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName.toString()
        } catch (e: Exception) {
            "1.0.0"
        }
    }

    private fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.lowercase().startsWith(manufacturer.lowercase())) {
            model.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        } else {
            "${manufacturer.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }} $model"
        }
    }

    private fun getDeviceUniqueId(): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            ?: UUID.randomUUID().toString()
    }

    private fun checkDeviceStatus() {
        viewModelScope.launch {
            val request = createDeviceStatus()
            Log.d("SplashViewModel", "checkDeviceStatus: $request")
            checkDeviceStatusUseCase(request).collect { result ->
                when (result) {
                    is NetworkResult.Error<*> -> {
                        Log.d("SplashViewModel", "Error ${result.data}")
                        Log.d("SplashViewModel", "Error ${result.message}")
                        _effect.emit(SplashEffect.ShowToast("Bir Hata Oldu Lütfen Tekrar Deneyiniz"))
                    }
                    is NetworkResult.Loading<*> -> {
                        Log.d("SplashViewModel", "Loading")
                    }
                    is NetworkResult.Success<*> -> {
                        val data = result.data
                        if (result.data?.Durum == null) {
                            Log.d("SplashViewModel", "Success ${result}")
                            _effect.emit(SplashEffect.ShowToast("Bir Hata Oluştu Lütfen Tekrar deneyiniz!!! ${data}"))
                            delay(2000)
                            exitProcess(0)
                        } else {
                            if (data?.Durum == "Update") {
                                //TODO: Update işlemi yapılacak
                            } else if (data?.Durum == "OK") {
                                sharedPreferences.saveData(PreferencesKeys.WareHouseName, result.data.DepoAdı.toString())
                                // Device status başarılı ise fetchDataAndSaveLocally çağrılır
                                fetchDataAndSaveLocally()
                            } else if (data?.Aktif == false) {
                                _effect.emit(SplashEffect.ShowToast("Depo Onayı Bekleniyor!!!"))
                                delay(2000)
                                exitProcess(0)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun fetchDataAndSaveLocally() {
        viewModelScope.launch {
            try {
                val recipients = mutableListOf<RecipientEntity>()
                val transferReasons = mutableListOf<TransferReasonEntity>()
                val machines = mutableListOf<MachineDataEntity>()

                coroutineScope {
                    launch {
                        getTeslimAlanUseCase().collect { result ->
                            if (result is NetworkResult.Success) {
                                result.data?.Liste?.forEach { item ->
                                    recipients.add(RecipientEntity(
                                        Kod = item.Kod ?: "",
                                        TeslimAlan = item.TeslimAlan ?: ""
                                    ))
                                }
                            }
                        }
                    }

                    launch {
                        getTransferNedeniUseCase().collect { result ->
                            if (result is NetworkResult.Success) {
                                result.data?.Liste?.forEach { item ->
                                    transferReasons.add(TransferReasonEntity(
                                        Kod = item.Kod ?: "",
                                        TransferNedeni = item.TransferNedeni ?: ""
                                    ))
                                }
                            }
                        }
                    }

                    launch {
                        getMachineListUseCase().collect { result ->
                            if (result is NetworkResult.Success) {
                                result.data?.Liste?.forEach { item ->
                                    machines.add(MachineDataEntity(
                                        Kod = item.Kod ?: "",
                                        MakinaSeri = item.MakinaSeri ?: "",
                                        Tarih = item.Tarih ?: ""
                                    ))
                                }
                            }
                        }
                    }
                }

                saveAllRecipientUseCase(recipients)
                saveAllTransferReasonUseCase(transferReasons)
                saveAllMachineDataUseCase(machines)

                _effect.emit(SplashEffect.NavigateTo("login"))

            } catch (e: CancellationException) {
                Log.d("SplashViewModel", "İş iptal edildi: ${e.message}")
                throw e
            } catch (e: Exception) {
                _effect.emit(SplashEffect.ShowToast("Veri çekme veya kaydetme işlemi sırasında hata oluştu"))
                Log.d("SplashViewModel", "Veri çekme veya kaydetme işlemi sırasında hata oluştu ${e.message}")
            }
        }
    }
}