package com.bestmakina.depotakip.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("transfer_reason")
data class TransferReasonEntity(
    @PrimaryKey
    val Kod: String,
    val TransferNedeni: String
)
