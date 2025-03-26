package com.bestmakina.depotakip.data.model.request.inventory

import com.google.gson.annotations.SerializedName

data class MachineSerialRequest(
    @SerializedName("MakinaSeri")
    val machineSerial: String
)