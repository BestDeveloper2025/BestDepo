package com.bestmakina.depotakip.domain.repository.remote

import com.bestmakina.depotakip.common.network.NetworkResult
import com.bestmakina.depotakip.data.model.request.inventory.GetInventoryDataRequest
import com.bestmakina.depotakip.data.model.request.inventory.TransferWithReceteRequest
import com.bestmakina.depotakip.data.model.response.inventory.TransferWithReceteResponse
import com.bestmakina.depotakip.domain.model.InventoryModel
import kotlinx.coroutines.flow.Flow

interface InventoryRepository {
    suspend fun getInventoryData(request: GetInventoryDataRequest): Flow<NetworkResult<InventoryModel>>
    suspend fun transferWithRecete(request: TransferWithReceteRequest): Flow<NetworkResult<TransferWithReceteResponse>>
}