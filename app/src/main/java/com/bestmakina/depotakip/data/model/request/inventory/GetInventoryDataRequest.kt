package com.bestmakina.depotakip.data.model.request.inventory

import com.google.gson.annotations.SerializedName

data class GetInventoryDataRequest(

    @SerializedName("MakinaSeri")
    val makinaSeri: String,

    @SerializedName("StokKodu")
    val stokKodu: String

)