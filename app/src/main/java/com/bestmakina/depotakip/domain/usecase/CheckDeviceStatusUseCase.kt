package com.bestmakina.depotakip.domain.usecase

import com.bestmakina.depotakip.data.model.request.device.CheckDeviceStatusRequest
import com.bestmakina.depotakip.domain.repository.remote.DeviceRepository
import javax.inject.Inject

class CheckDeviceStatusUseCase @Inject constructor(
    private val repository: DeviceRepository
) {
    suspend operator fun invoke(deviceData: CheckDeviceStatusRequest) =
        repository.checkDeviceStatus(deviceData)
}