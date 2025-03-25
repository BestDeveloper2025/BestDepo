package com.bestmakina.depotakip.domain.repository.local

import com.bestmakina.depotakip.data.local.entity.MachineDataEntity
import kotlinx.coroutines.flow.Flow

interface MachineDataRepository {
    fun getAllMachineData(): Flow<List<MachineDataEntity>>
    suspend fun saveAllMachineData(machines: List<MachineDataEntity>)
    suspend fun clearAllMachineData()
}