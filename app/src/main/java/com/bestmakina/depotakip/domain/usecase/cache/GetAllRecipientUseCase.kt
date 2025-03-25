package com.bestmakina.depotakip.domain.usecase.cache

import com.bestmakina.depotakip.data.local.entity.RecipientEntity
import com.bestmakina.depotakip.domain.repository.local.RecipientRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllRecipientUseCase @Inject constructor(
    private val repository: RecipientRepository
){

    operator fun invoke(): Flow<List<RecipientEntity>> {
        return repository.getAllRecipient()
    }

}