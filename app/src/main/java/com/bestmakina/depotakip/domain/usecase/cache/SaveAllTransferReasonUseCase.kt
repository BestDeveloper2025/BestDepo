package com.bestmakina.depotakip.domain.usecase.cache

import com.bestmakina.depotakip.data.local.entity.TransferReasonEntity
import com.bestmakina.depotakip.domain.repository.local.TransferReasonRepository
import javax.inject.Inject

class SaveAllTransferReasonUseCase @Inject constructor(
    private val repository: TransferReasonRepository
){

    suspend operator fun invoke(transferReason: List<TransferReasonEntity>) {
        repository.saveAllTransferReasons(transferReason)
    }

}