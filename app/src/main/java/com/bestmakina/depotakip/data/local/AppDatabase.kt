package com.bestmakina.depotakip.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bestmakina.depotakip.data.local.dao.MachineDataDao
import com.bestmakina.depotakip.data.local.dao.RecipientDao
import com.bestmakina.depotakip.data.local.dao.TransferReasonDao
import com.bestmakina.depotakip.data.local.entity.MachineDataEntity
import com.bestmakina.depotakip.data.local.entity.RecipientEntity
import com.bestmakina.depotakip.data.local.entity.TransferReasonEntity

@Database(
    entities = [
        MachineDataEntity::class,
        TransferReasonEntity::class,
        RecipientEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun machineDataDao(): MachineDataDao
    abstract fun transferReasonDao(): TransferReasonDao
    abstract fun deviceReceiverDao(): RecipientDao
}