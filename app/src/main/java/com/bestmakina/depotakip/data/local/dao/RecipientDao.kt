package com.bestmakina.depotakip.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bestmakina.depotakip.data.local.entity.RecipientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipientDao {
    @Query("SELECT * FROM recipient")
    fun getAllDeviceReceivers(): Flow<List<RecipientEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDevice(receivers: List<RecipientEntity>)

    @Delete
    suspend fun deleteDevice(receiver: RecipientEntity)

    @Query("DELETE FROM recipient")
    suspend fun deleteAllDevice()
}