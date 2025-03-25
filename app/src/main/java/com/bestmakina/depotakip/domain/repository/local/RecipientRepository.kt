package com.bestmakina.depotakip.domain.repository.local

import com.bestmakina.depotakip.data.local.entity.RecipientEntity
import kotlinx.coroutines.flow.Flow

interface RecipientRepository {
    fun getAllRecipient(): Flow<List<RecipientEntity>>
    suspend fun saveAllRecipient(recipients: List<RecipientEntity>)
    suspend fun clearAllRecipient()
}