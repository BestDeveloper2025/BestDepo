package com.bestmakina.depotakip.domain.usecase

import com.bestmakina.depotakip.common.network.NetworkResult
import com.bestmakina.depotakip.data.model.response.transfer.TransferNedeniResponse
import com.bestmakina.depotakip.domain.repository.remote.WarehousePersonnelRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTransferNedeniUseCase @Inject constructor(
    private val repository: WarehousePersonnelRepository
){
    suspend operator fun invoke(): Flow<NetworkResult<TransferNedeniResponse>> {
        return repository.getTransferNedeniList()
    }
}