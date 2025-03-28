package com.bestmakina.depotakip.domain.usecase.inventory

import com.bestmakina.depotakip.common.network.NetworkResult
import com.bestmakina.depotakip.data.model.request.inventory.CreateOrderRequest
import com.bestmakina.depotakip.data.model.response.inventory.CreateOrderDto
import com.bestmakina.depotakip.domain.repository.remote.InventoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateOrderUseCase @Inject constructor(
    private val repository: InventoryRepository
){
    suspend operator fun invoke(request: CreateOrderRequest): Flow<NetworkResult<CreateOrderDto>>{
        return repository.createOrder(request)
    }
}