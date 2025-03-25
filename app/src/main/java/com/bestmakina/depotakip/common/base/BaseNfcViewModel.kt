package com.bestmakina.depotakip.common.base

import android.nfc.Tag
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestmakina.depotakip.common.model.TagDetectionState
import com.bestmakina.depotakip.common.util.event.NfcEvents
import com.bestmakina.depotakip.domain.manager.NfcManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseNfcViewModel(
    private val nfcManager: NfcManager
) : ViewModel() {

    private val _tagDetectionState = MutableStateFlow<TagDetectionState>(TagDetectionState.Idle)
    val tagDetectionState: StateFlow<TagDetectionState> = _tagDetectionState

    protected fun observeNfcTags() {
        viewModelScope.launch {
            NfcEvents.tagDetected.collect { tag ->
                onTagDetected(tag)
            }
        }
    }

    protected abstract fun onTagDetected(tag: Tag)

    protected suspend fun readFromTagAndLog(tag: Tag): Result<String> {
        return nfcManager.readFromTag(tag).also { result ->
            result.onSuccess { data ->
                Log.d("NfcTag", "Okunan veri: $data")
            }
        }
    }

    protected suspend fun writeToTag(tag: Tag, data: String): Result<Unit> {
        return nfcManager.writeToTag(tag, data)
    }
}