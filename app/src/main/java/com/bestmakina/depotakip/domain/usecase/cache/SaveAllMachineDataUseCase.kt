package com.bestmakina.depotakip.domain.usecase.cache

import com.bestmakina.depotakip.data.local.entity.MachineDataEntity
import com.bestmakina.depotakip.domain.repository.local.MachineDataRepository
import javax.inject.Inject

class SaveAllMachineDataUseCase @Inject constructor(
    private val repository: MachineDataRepository
){

    suspend operator fun invoke(machines: List<MachineDataEntity>) {
        repository.saveAllMachineData(machines)
    }

}