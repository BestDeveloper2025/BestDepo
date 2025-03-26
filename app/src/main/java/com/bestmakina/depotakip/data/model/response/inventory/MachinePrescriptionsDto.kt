package com.bestmakina.depotakip.data.model.response.inventory

import com.google.gson.annotations.SerializedName

data class MachinePrescriptionsDto(
    @SerializedName("Durum")
    val durum: String,
    @SerializedName("StokKoduListe")
    val stockCodeListDto: List<StockCodeListDto>
)