package com.bestmakina.depotakip.domain.usecase.cache

import com.bestmakina.depotakip.data.local.entity.MachineDataEntity
import com.bestmakina.depotakip.domain.repository.local.MachineDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllMachineDataUseCase @Inject constructor(
    private val repository: MachineDataRepository
){

    operator fun invoke(): Flow<List<MachineDataEntity>> {
        return repository.getAllMachineData()
    }

}