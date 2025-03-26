package com.bestmakina.depotakip.domain.usecase.inventory

import com.bestmakina.depotakip.common.network.NetworkResult
import com.bestmakina.depotakip.data.model.request.inventory.MachineSerialRequest
import com.bestmakina.depotakip.domain.model.StockCodes
import com.bestmakina.depotakip.domain.repository.remote.InventoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMachinePrescriptionUseCase @Inject constructor(
    private val repository: InventoryRepository
){
    suspend operator fun invoke(request: MachineSerialRequest): Flow<NetworkResult<StockCodes>> {
        return repository.getMachinePrescription(request)
    }
}