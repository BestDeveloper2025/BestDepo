package com.bestmakina.depotakip.di.module

import android.content.Context
import androidx.room.Room
import com.bestmakina.depotakip.data.local.AppDatabase
import com.bestmakina.depotakip.data.local.dao.MachineDataDao
import com.bestmakina.depotakip.data.local.dao.RecipientDao
import com.bestmakina.depotakip.data.local.dao.TransferReasonDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "depo_takip_database"
        ).build()
    }

    @Provides
    fun provideMachineDataDao(database: AppDatabase): MachineDataDao {
        return database.machineDataDao()
    }

    @Provides
    fun provideTransferReasonDao(database: AppDatabase): TransferReasonDao {
        return database.transferReasonDao()
    }

    @Provides
    fun provideDeviceReceiverDao(database: AppDatabase): RecipientDao {
        return database.deviceReceiverDao()
    }
}