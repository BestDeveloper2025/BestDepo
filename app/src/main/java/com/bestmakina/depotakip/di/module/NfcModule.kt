package com.bestmakina.depotakip.di.module

import android.nfc.NfcAdapter
import com.bestmakina.depotakip.domain.manager.NfcManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NfcModule {

    @Provides
    @Singleton
    fun provideNfcAdapter(@ApplicationContext context: android.content.Context): NfcAdapter? {
        return NfcAdapter.getDefaultAdapter(context)
    }

    @Provides
    @Singleton
    fun provideNfcManager(nfcAdapter: NfcAdapter?): NfcManager {
        return NfcManager(nfcAdapter)
    }
}