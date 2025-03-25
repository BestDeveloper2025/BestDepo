package com.bestmakina.depotakip.data.repository.local

import com.bestmakina.depotakip.data.local.dao.MachineDataDao
import com.bestmakina.depotakip.data.local.entity.MachineDataEntity
import com.bestmakina.depotakip.domain.repository.local.MachineDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

 class MachineDataRepositoryImpl  @Inject constructor(
    private val machineDao: MachineDataDao
) : MachineDataRepository{
     override fun getAllMachineData(): Flow<List<MachineDataEntity>> {
         return machineDao.getAllMachineData()
     }

     override suspend fun saveAllMachineData(machines: List<MachineDataEntity>) {
         machineDao.insertAllMachineData(machines)
     }

     override suspend fun clearAllMachineData() {
         machineDao.deleteAllMachineData()
     }
 }