package com.bestmakina.depotakip.di.module

import com.bestmakina.depotakip.data.remote.PersonnelApiService
import com.bestmakina.depotakip.data.repository.remote.WarehousePersonnelRepositoryImpl
import com.bestmakina.depotakip.domain.repository.remote.WarehousePersonnelRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WareHousePersonnelRepositoryModule {

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): PersonnelApiService {
        return retrofit.create(PersonnelApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideWareHousePersonnelRepository(apiService: PersonnelApiService): WarehousePersonnelRepository {
        return WarehousePersonnelRepositoryImpl(apiService)
    }
}