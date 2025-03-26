package com.bestmakina.depotakip.data.mapper

import com.bestmakina.depotakip.data.model.response.inventory.MachinePrescriptionsDto
import com.bestmakina.depotakip.domain.model.StockCodes

fun MachinePrescriptionsDto.toDomain(): StockCodes {
    return StockCodes(
        stockCodes = stockCodeListDto.map { it.stockCode }
    )
}