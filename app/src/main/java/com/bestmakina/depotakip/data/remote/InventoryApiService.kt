package com.bestmakina.depotakip.data.remote

import com.bestmakina.depotakip.data.model.request.inventory.GetInventoryDataRequest
import com.bestmakina.depotakip.data.model.request.inventory.MachineSerialRequest
import com.bestmakina.depotakip.data.model.request.inventory.TransferWithReceteRequest
import com.bestmakina.depotakip.data.model.response.inventory.InventoryDataDto
import com.bestmakina.depotakip.data.model.response.inventory.MachinePrescriptionsDto
import com.bestmakina.depotakip.data.model.response.inventory.TransferWithReceteResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface InventoryApiService {
    @POST("BestUretim/hs/Best/EnvanterRecete")
    suspend fun getInventoryData(@Body request: GetInventoryDataRequest): Response<InventoryDataDto>

    @POST("BestUretim/hs/Best/DepoTransferiRecete")
    suspend fun transferWithRecete(@Body request: TransferWithReceteRequest): Response<TransferWithReceteResponse>

    @POST("BestUretim/hs/Best/MakinaSeriRecete")
    suspend fun getMachinePrescriptions(@Body request: MachineSerialRequest): Response<MachinePrescriptionsDto>
}