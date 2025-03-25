package com.bestmakina.depotakip.di.module

import com.bestmakina.depotakip.data.remote.InventoryApiService
import com.bestmakina.depotakip.data.repository.remote.InventoryRepositoryImpl
import com.bestmakina.depotakip.domain.repository.remote.InventoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InventoryModule {

    @Provides
    @Singleton
    fun provideInventoryApiService(retrofit: Retrofit): InventoryApiService {
        return retrofit.create(InventoryApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideInventoryRepository(inventoryApiService: InventoryApiService): InventoryRepository {
        return InventoryRepositoryImpl(inventoryApiService)
    }
}