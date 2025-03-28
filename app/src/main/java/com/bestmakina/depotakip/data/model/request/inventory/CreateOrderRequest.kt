package com.bestmakina.depotakip.data.model.request.inventory

data class CreateOrderRequest(
    val DepoKodu: String,
    val IhtiyacMiktari: String,
    val LineNumber: String,
    val MakinaSeri: String,
    val ReceteMiktari: String,
    val StokKodu: String,
    val TerminalUser: String
)