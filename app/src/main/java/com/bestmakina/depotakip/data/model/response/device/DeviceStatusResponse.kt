package com.bestmakina.depotakip.data.model.response.device

data class DeviceStatusResponse(
    val Aktif: Boolean?,
    val DepoAdı: String?,
    val Durum: String,
    val Mesaj: String?
)