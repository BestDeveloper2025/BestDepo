package com.bestmakina.depotakip.data.model.request.personnel

import com.google.gson.annotations.SerializedName

data class WareHousePersonnel(
    @SerializedName("ListeAdÄ±") val listeAdi: String,
    @SerializedName("SorumluPersonel") val sorumluPersonel: List<PersonnelData>
)