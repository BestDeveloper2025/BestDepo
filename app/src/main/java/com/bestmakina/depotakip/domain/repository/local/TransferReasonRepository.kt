package com.bestmakina.depotakip.domain.repository.local

import com.bestmakina.depotakip.data.local.entity.TransferReasonEntity
import kotlinx.coroutines.flow.Flow

interface TransferReasonRepository {
    fun getAllTransferReasons(): Flow<List<TransferReasonEntity>>
    suspend fun saveAllTransferReasons(reasons: List<TransferReasonEntity>)
    suspend fun clearAllTransferReasons()
}