package com.bestmakina.depotakip.domain.usecase

import com.bestmakina.depotakip.domain.repository.remote.WarehousePersonnelRepository
import javax.inject.Inject

class GetNameByBarcodeUseCase @Inject constructor(
    private val repository: WarehousePersonnelRepository
) {
    suspend operator fun invoke(barcode: String) = repository.getBarcodeToName(barcode)
}