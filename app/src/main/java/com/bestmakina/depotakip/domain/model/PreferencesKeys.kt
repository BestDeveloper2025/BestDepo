package com.bestmakina.depotakip.domain.model

sealed class PreferencesKeys(val key: String) {
    data object UserId: PreferencesKeys("user_id")
    data object Token: PreferencesKeys("token")
    data object UserName: PreferencesKeys("user_name")
    data object WareHouseName: PreferencesKeys("warehouse_name")
}