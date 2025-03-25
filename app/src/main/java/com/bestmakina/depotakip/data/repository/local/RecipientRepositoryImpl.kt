package com.bestmakina.depotakip.data.repository.local

import com.bestmakina.depotakip.data.local.dao.RecipientDao
import com.bestmakina.depotakip.data.local.entity.RecipientEntity
import com.bestmakina.depotakip.domain.repository.local.RecipientRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RecipientRepositoryImpl @Inject constructor(
    private val recipientDao: RecipientDao
) : RecipientRepository{
    override fun getAllRecipient(): Flow<List<RecipientEntity>> {
        return recipientDao.getAllDeviceReceivers()
    }

    override suspend fun saveAllRecipient(recipients: List<RecipientEntity>) {
        recipientDao.insertAllDevice(recipients)
    }

    override suspend fun clearAllRecipient() {
        recipientDao.deleteAllDevice()
    }
}