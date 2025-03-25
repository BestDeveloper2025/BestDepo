package com.bestmakina.depotakip.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bestmakina.depotakip.data.local.entity.TransferReasonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransferReasonDao {
    @Query("SELECT * FROM transfer_reason")
    fun getAllTransferReasons(): Flow<List<TransferReasonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTransferReason(reasons: List<TransferReasonEntity>)

    @Delete
    suspend fun deleteTransferReason(reason: TransferReasonEntity)

    @Query("DELETE FROM transfer_reason")
    suspend fun deleteAllTransferReason()
}