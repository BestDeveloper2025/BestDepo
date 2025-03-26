package com.bestmakina.depotakip.domain.usecase.personnel

import com.bestmakina.depotakip.common.network.NetworkResult
import com.bestmakina.depotakip.data.model.response.device.TeslimAlanResponse
import com.bestmakina.depotakip.domain.repository.remote.WarehousePersonnelRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTeslimAlanUseCase @Inject constructor(
    private val repository: WarehousePersonnelRepository
){
    suspend operator fun invoke(): Flow<NetworkResult<TeslimAlanResponse>> {
        return repository.getTeslimAlanList()
    }
}