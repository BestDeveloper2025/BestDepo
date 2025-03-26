package com.bestmakina.depotakip.data.model.response.inventory

import com.google.gson.annotations.SerializedName

data class StockCodeListDto(
    @SerializedName("StokKodu")
    val stockCode: String
)