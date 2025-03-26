package com.bestmakina.depotakip.domain.usecase.inventory

import com.bestmakina.depotakip.common.network.NetworkResult
import com.bestmakina.depotakip.data.model.request.inventory.GetInventoryDataRequest
import com.bestmakina.depotakip.domain.model.InventoryModel
import com.bestmakina.depotakip.domain.repository.remote.InventoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetInventoryDataUseCase @Inject constructor(
    private val repository: InventoryRepository
) {
    suspend operator fun invoke(request: GetInventoryDataRequest): Flow<NetworkResult<InventoryModel>> {
        return repository.getInventoryData(request)
    }
}