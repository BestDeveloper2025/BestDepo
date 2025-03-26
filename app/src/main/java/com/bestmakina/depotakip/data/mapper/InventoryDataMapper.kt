package com.bestmakina.depotakip.data.mapper

import com.bestmakina.depotakip.data.model.response.inventory.InventoryDataDto
import com.bestmakina.depotakip.domain.model.InventoryModel

fun InventoryDataDto.toDomain(): InventoryModel {
    return InventoryModel(
        durum = durum,
        message = message,
        stokKodu = stokKodu,
        barkodNo = barkod,
        rafKodu = rafKodu,
        receteMiktari = receteMiktari,
        ustDepoAdet = ustDepo,
        altDepoAdet = altDepo,
        sanalKasa = sanalKasa,
        urunAdi = stokAdi,
        resimData = resim,
        montajaVerilen = montajaVerilen,
        minStock = minStock
    )
}