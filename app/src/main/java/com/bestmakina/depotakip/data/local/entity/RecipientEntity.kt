package com.bestmakina.depotakip.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("recipient")
data class RecipientEntity(
    @PrimaryKey
    val Kod: String,
    val TeslimAlan: String
)
