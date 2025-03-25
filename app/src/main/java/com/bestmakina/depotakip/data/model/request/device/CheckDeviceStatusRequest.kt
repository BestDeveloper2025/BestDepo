package com.bestmakina.depotakip.data.model.request.device

import com.google.gson.annotations.SerializedName

data class CheckDeviceStatusRequest(
    @SerializedName("Cozunurluk")
    val cozunurluk: String,
    @SerializedName("IPAdresi")
    val IPAdresi: String,
    @SerializedName("VersionNo")
    val versionNo: String,
    @SerializedName("terminal_adi")
    val terminalAdi: String,
    @SerializedName("terminal_id")
    val terminalId: String
)