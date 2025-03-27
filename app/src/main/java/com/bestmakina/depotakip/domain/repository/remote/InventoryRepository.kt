package com.bestmakina.depotakip.domain.repository.remote

import com.bestmakina.depotakip.common.network.NetworkResult
import com.bestmakina.depotakip.data.model.request.inventory.BulkTransferWithRecereRequest
import com.bestmakina.depotakip.data.model.request.inventory.GetInventoryDataRequest
import com.bestmakina.depotakip.data.model.request.inventory.MachineSerialRequest
import com.bestmakina.depotakip.data.model.request.inventory.TransferWithReceteRequest
import com.bestmakina.depotakip.data.model.response.inventory.MachinePrescriptionsDto
import com.bestmakina.depotakip.data.model.response.inventory.TransferWithReceteResponse
import com.bestmakina.depotakip.domain.model.InventoryModel
import com.bestmakina.depotakip.domain.model.StockCodes
import kotlinx.coroutines.flow.Flow

interface InventoryRepository {
    suspend fun getInventoryData(request: GetInventoryDataRequest): Flow<NetworkResult<InventoryModel>>
    suspend fun transferWithRecete(request: TransferWithReceteRequest): Flow<NetworkResult<TransferWithReceteResponse>>
    suspend fun getMachinePrescription(request: MachineSerialRequest): Flow<NetworkResult<StockCodes>>
    suspend fun bulkTransferWithRecete(request: BulkTransferWithRecereRequest): Flow<NetworkResult<TransferWithReceteResponse>>

}