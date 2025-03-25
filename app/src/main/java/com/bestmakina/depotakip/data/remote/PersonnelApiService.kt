package com.bestmakina.depotakip.data.remote

import com.bestmakina.depotakip.data.model.request.personnel.BarcodeToNameData
import com.bestmakina.depotakip.data.model.request.personnel.WareHousePersonnel
import com.bestmakina.depotakip.data.model.response.device.TeslimAlanResponse
import com.bestmakina.depotakip.data.model.response.transfer.DeviceListResponse
import com.bestmakina.depotakip.data.model.response.transfer.TransferNedeniResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface PersonnelApiService {
    @GET("BestUretim/hs/Best/Uretim/SorumluPersonel/")
    suspend fun getWarehousePersonnel(): WareHousePersonnel

    @GET("BestUretim/hs/Best/Uretim/{barcode}")
    suspend fun getBarcodeToName(@Path("barcode") barcode: String): BarcodeToNameData

    @GET("BestUretim/hs/Best/TeslimAlanList/")
    suspend fun getTeslimAlanList(): TeslimAlanResponse

    @GET("BestUretim/hs/Best/TransferNedeniList")
    suspend fun getTransferNedeniList(): TransferNedeniResponse

    @GET("BestUretim/hs/Best/MakinaSeriList")
    suspend fun getMakinaSeriList(): DeviceListResponse

}