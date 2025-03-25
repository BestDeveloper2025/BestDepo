package com.bestmakina.depotakip.data.repository.remote

import com.bestmakina.depotakip.common.network.NetworkResult
import com.bestmakina.depotakip.data.model.request.device.CheckDeviceStatusRequest
import com.bestmakina.depotakip.data.model.response.device.DeviceStatusResponse
import com.bestmakina.depotakip.data.remote.DeviceApiService
import com.bestmakina.depotakip.domain.repository.remote.DeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeviceRepositoryImpl @Inject constructor(
    private val apiService: DeviceApiService
) : DeviceRepository {
    override suspend fun checkDeviceStatus(deviceData: CheckDeviceStatusRequest): Flow<NetworkResult<DeviceStatusResponse>> = flow{
        emit(NetworkResult.Loading())
        try {
            val response = apiService.checkDeviceStatus(deviceData)
            emit(NetworkResult.Success(response))
        }catch (e: Exception){
            emit(NetworkResult.Error(e.localizedMessage))
        }
    }
}