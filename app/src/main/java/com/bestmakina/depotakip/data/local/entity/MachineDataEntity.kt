package com.bestmakina.depotakip.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("machine_data")
data class MachineDataEntity(
        @PrimaryKey
        val Kod: String,
        val MakinaSeri: String,
        val Tarih: String
)
