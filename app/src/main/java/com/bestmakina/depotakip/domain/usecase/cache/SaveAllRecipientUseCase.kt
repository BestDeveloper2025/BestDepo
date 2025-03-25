package com.bestmakina.depotakip.domain.usecase.cache

import com.bestmakina.depotakip.data.local.entity.RecipientEntity
import com.bestmakina.depotakip.domain.repository.local.RecipientRepository
import javax.inject.Inject

class SaveAllRecipientUseCase @Inject constructor(
    private val repository: RecipientRepository
){

    suspend operator fun invoke(recipient: List<RecipientEntity>) {
        repository.saveAllRecipient(recipient)
    }

}