package com.bestmakina.depotakip.domain.usecase.inventory

import com.bestmakina.depotakip.common.network.NetworkResult
import com.bestmakina.depotakip.data.model.response.transfer.DeviceListResponse
import com.bestmakina.depotakip.domain.repository.remote.WarehousePersonnelRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMachineListUseCase @Inject constructor(
    private val repository: WarehousePersonnelRepository
){
    suspend operator fun invoke(): Flow<NetworkResult<DeviceListResponse>> {
        return repository.getMachineList()
    }
}