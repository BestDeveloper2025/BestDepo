package com.bestmakina.depotakip.domain.usecase.inventory

import com.bestmakina.depotakip.common.network.NetworkResult
import com.bestmakina.depotakip.data.model.request.inventory.TransferWithReceteRequest
import com.bestmakina.depotakip.data.model.response.inventory.TransferWithReceteResponse
import com.bestmakina.depotakip.domain.repository.remote.InventoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TransferWithReceteUseCase @Inject constructor(
    private val inventoryRepository: InventoryRepository
){
    suspend operator fun invoke(request: TransferWithReceteRequest): Flow<NetworkResult<TransferWithReceteResponse>> {
        return inventoryRepository.transferWithRecete(request)
    }
}