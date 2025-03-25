package com.bestmakina.depotakip.data.model.response.inventory

import com.google.gson.annotations.SerializedName

data class InventoryDataDto(
    @SerializedName("Durum")
    val durum: String?,
    @SerializedName("Mesaj")
    val message: String?,
    @SerializedName("StokKodu")
    val stokKodu: String?,
    @SerializedName("BARKOD")
    val barkod: String?,
    @SerializedName("BarkodN")
    val barkodN: String?,
    @SerializedName("RafKodu")
    val rafKodu: String?,
    @SerializedName("AltDepoRaf")
    val altDepoRaf: String?,
    @SerializedName("StokAdi")
    val stokAdi: String?,
    @SerializedName("ReçeteMiktarı")
    val receteMiktari: Int?,
    @SerializedName("MontajaVerilen")
    val montajaVerilen: Int?,
    @SerializedName("UstDepo")
    val ustDepo: String?,
    @SerializedName("AltDepo")
    val altDepo: String?,
    @SerializedName("SanalKasa")
    val sanalKasa: Int?,
    @SerializedName("Resim")
    val resim: String?,
    @SerializedName("Sure")
    val sure: Int?
)