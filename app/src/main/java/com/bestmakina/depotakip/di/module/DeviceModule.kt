package com.bestmakina.depotakip.di.module

import com.bestmakina.depotakip.data.remote.DeviceApiService
import com.bestmakina.depotakip.data.repository.remote.DeviceRepositoryImpl
import com.bestmakina.depotakip.domain.repository.remote.DeviceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DeviceModule {

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): DeviceApiService {
        return retrofit.create(DeviceApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDeviceRepository(apiService: DeviceApiService): DeviceRepository {
        return DeviceRepositoryImpl(apiService)
    }

}