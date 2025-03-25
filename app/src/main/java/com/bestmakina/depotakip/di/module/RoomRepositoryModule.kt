package com.bestmakina.depotakip.di.module

import com.bestmakina.depotakip.data.local.dao.MachineDataDao
import com.bestmakina.depotakip.data.local.dao.RecipientDao
import com.bestmakina.depotakip.data.local.dao.TransferReasonDao
import com.bestmakina.depotakip.data.local.entity.RecipientEntity
import com.bestmakina.depotakip.data.repository.local.MachineDataRepositoryImpl
import com.bestmakina.depotakip.data.repository.local.RecipientRepositoryImpl
import com.bestmakina.depotakip.data.repository.local.TransferReasonRepositoryImpl
import com.bestmakina.depotakip.domain.repository.local.MachineDataRepository
import com.bestmakina.depotakip.domain.repository.local.RecipientRepository
import com.bestmakina.depotakip.domain.repository.local.TransferReasonRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomRepositoryModule {

    @Provides
    @Singleton
    fun provideMachineDataRepository(machineDataDao: MachineDataDao): MachineDataRepository {
        return MachineDataRepositoryImpl(machineDataDao)
    }

    @Provides
    @Singleton
    fun provideTransferReasonRepository(transferReasonDao: TransferReasonDao): TransferReasonRepository {
        return TransferReasonRepositoryImpl(transferReasonDao)
    }

    @Provides
    @Singleton
    fun provideDeviceReceiverRepository(recipientDao: RecipientDao): RecipientRepository {
        return RecipientRepositoryImpl(recipientDao)
    }

}