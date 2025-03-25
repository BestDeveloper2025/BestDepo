package com.bestmakina.depotakip.data.remote

import com.bestmakina.depotakip.data.model.request.device.CheckDeviceStatusRequest
import com.bestmakina.depotakip.data.model.response.device.DeviceStatusResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface DeviceApiService {
    @POST("BestUretim/hs/Best/Kimsin2025")
    suspend fun checkDeviceStatus(@Body deviceData: CheckDeviceStatusRequest): DeviceStatusResponse
}