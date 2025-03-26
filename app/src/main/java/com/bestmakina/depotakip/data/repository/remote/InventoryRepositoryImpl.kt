package com.bestmakina.depotakip.data.repository.remote

import com.bestmakina.depotakip.common.network.NetworkResult
import com.bestmakina.depotakip.data.mapper.toDomain
import com.bestmakina.depotakip.data.model.request.inventory.GetInventoryDataRequest
import com.bestmakina.depotakip.data.model.request.inventory.MachineSerialRequest
import com.bestmakina.depotakip.data.model.request.inventory.TransferWithReceteRequest
import com.bestmakina.depotakip.data.model.response.inventory.MachinePrescriptionsDto
import com.bestmakina.depotakip.data.model.response.inventory.TransferWithReceteResponse
import com.bestmakina.depotakip.data.remote.InventoryApiService
import com.bestmakina.depotakip.domain.model.InventoryModel
import com.bestmakina.depotakip.domain.model.StockCodes
import com.bestmakina.depotakip.domain.repository.remote.InventoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class InventoryRepositoryImpl @Inject constructor(
    private val inventoryService: InventoryApiService
) : InventoryRepository {

    override suspend fun getInventoryData(request: GetInventoryDataRequest): Flow<NetworkResult<InventoryModel>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = inventoryService.getInventoryData(request)
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    emit(NetworkResult.Success(responseBody.toDomain()))
                } else {
                    emit(NetworkResult.Error("Response body is null"))
                }
            } else {
                emit(NetworkResult.Error("Response is not successful: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error("Exception occurred: ${e.message}"))
        }
    }

    override suspend fun transferWithRecete(request: TransferWithReceteRequest): Flow<NetworkResult<TransferWithReceteResponse>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = inventoryService.transferWithRecete(request)
            val responseBody = response.body()
            if (responseBody != null) {
                emit(NetworkResult.Success(responseBody))
            } else {
                emit(NetworkResult.Error("Response body is null"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error("Exception occurred: ${e.message}"))
        }
    }

    override suspend fun getMachinePrescription(request: MachineSerialRequest): Flow<NetworkResult<StockCodes>> = flow{
        emit(NetworkResult.Loading())
        try {
            val response = inventoryService.getMachinePrescriptions(request)
            val responseBody = response.body()
            if (responseBody != null) {
                emit(NetworkResult.Success(responseBody.toDomain()))
            } else {
                emit(NetworkResult.Error("Response body is null"))
            }
        }catch (e: Exception){
            emit(NetworkResult.Error("Exception occurred: ${e.message}"))
        }
    }
}