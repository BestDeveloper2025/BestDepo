package com.bestmakina.depotakip.domain.usecase.cache

import com.bestmakina.depotakip.data.local.entity.TransferReasonEntity
import com.bestmakina.depotakip.domain.repository.local.TransferReasonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllTransferReasonUseCase @Inject constructor(
    private val repository: TransferReasonRepository
){

    operator fun invoke(): Flow<List<TransferReasonEntity>> {
        return repository.getAllTransferReasons()
    }

}