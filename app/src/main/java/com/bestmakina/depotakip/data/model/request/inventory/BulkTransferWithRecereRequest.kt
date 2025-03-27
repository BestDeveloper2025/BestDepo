package com.bestmakina.depotakip.data.model.request.inventory

data class BulkTransferWithRecereRequest(
    val DepoKodu: String,
    val MakinaSeri: String,
    val StokKodu: String,
    val TerminalUser: String,
    val TeslimAlan: String,
    val TransferMiktari: String,
    val TransferNedeni: String,
    val SayacSira: Int?
)
