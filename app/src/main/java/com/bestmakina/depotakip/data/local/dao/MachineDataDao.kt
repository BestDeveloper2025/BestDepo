package com.bestmakina.depotakip.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bestmakina.depotakip.data.local.entity.MachineDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MachineDataDao {
    @Query("SELECT * FROM machine_data")
    fun getAllMachineData(): Flow<List<MachineDataEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMachineData(machines: List<MachineDataEntity>)

    @Delete
    suspend fun deleteMachineData(machine: MachineDataEntity)

    @Query("DELETE FROM machine_data")
    suspend fun deleteAllMachineData()
}