package com.bestmakina.depotakip.domain.repository.remote

import com.bestmakina.depotakip.common.network.NetworkResult
import com.bestmakina.depotakip.data.model.request.device.CheckDeviceStatusRequest
import com.bestmakina.depotakip.data.model.response.device.DeviceStatusResponse
import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    suspend fun checkDeviceStatus(deviceData: CheckDeviceStatusRequest): Flow<NetworkResult<DeviceStatusResponse>>
}