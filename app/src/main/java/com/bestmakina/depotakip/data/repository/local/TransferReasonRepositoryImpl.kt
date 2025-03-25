package com.bestmakina.depotakip.data.repository.local

import com.bestmakina.depotakip.data.local.dao.TransferReasonDao
import com.bestmakina.depotakip.data.local.entity.TransferReasonEntity
import com.bestmakina.depotakip.domain.repository.local.TransferReasonRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TransferReasonRepositoryImpl @Inject constructor(
    private val transferReasonDao: TransferReasonDao
) : TransferReasonRepository{
    override fun getAllTransferReasons(): Flow<List<TransferReasonEntity>> {
        return transferReasonDao.getAllTransferReasons()
    }

    override suspend fun saveAllTransferReasons(reasons: List<TransferReasonEntity>) {
        transferReasonDao.insertAllTransferReason(reasons)
    }

    override suspend fun clearAllTransferReasons() {
        transferReasonDao.deleteAllTransferReason()
    }
}