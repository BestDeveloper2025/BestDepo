package com.bestmakina.depotakip.domain.model

data class InventoryModel(
    val durum: String?,
    val message: String?,
    val stokKodu: String?,
    val barkodNo: String?,
    val rafKodu: String?,
    val receteMiktari: Int?,
    val ustDepoAdet: String?,
    val altDepoAdet: String?,
    val sanalKasa: Int?,
    val urunAdi: String?,
    val resimData: String?,
    val montajaVerilen: Int?
)
