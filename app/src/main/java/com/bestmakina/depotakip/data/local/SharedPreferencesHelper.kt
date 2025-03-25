package com.bestmakina.depotakip.data.local

import android.content.Context
import android.content.SharedPreferences
import com.bestmakina.depotakip.domain.model.PreferencesKeys
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class SharedPreferencesHelper @Inject constructor(
    context: Context
){

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

    fun saveData(key: PreferencesKeys, value: String) {
        sharedPreferences.edit { putString(key.key, value) }
    }

    fun getData(key: PreferencesKeys, value: String = ""): String {
        return sharedPreferences.getString(key.key, value) ?: value
    }

    fun removeData(key: PreferencesKeys) {
        sharedPreferences.edit { remove(key.key) }
    }

    fun clearAllData() {
        sharedPreferences.edit { clear() }
    }
}