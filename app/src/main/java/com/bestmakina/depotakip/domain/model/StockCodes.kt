package com.bestmakina.depotakip.domain.model

data class StockCodes(
    val status: String,
    val stockCodes: List<String>? = null,
    val counter: Int
)