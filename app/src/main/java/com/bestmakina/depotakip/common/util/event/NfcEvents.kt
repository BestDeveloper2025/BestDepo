package com.bestmakina.depotakip.common.util.event

import android.nfc.Tag
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object NfcEvents {

    private val _tagDetected = MutableSharedFlow<Tag>(extraBufferCapacity = 1)
    val tagDetected = _tagDetected.asSharedFlow()

    suspend fun publishDetectedTag(tag: Tag) {
        _tagDetected.emit(tag)
    }
}