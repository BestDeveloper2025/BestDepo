package com.bestmakina.depotakip.common.model

sealed class TagDetectionState {
    data object Idle: TagDetectionState()
    data object Scanning: TagDetectionState()
    data class Detected(val tagId: String): TagDetectionState()
    data class Error(val message: String): TagDetectionState()

}