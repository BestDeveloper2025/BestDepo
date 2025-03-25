package com.bestmakina.depotakip.presentation.ui.view.stocktaking.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bestmakina.depotakip.domain.manager.BarcodeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockTakingViewModel @Inject constructor(
    private val barcodeManager: BarcodeManager
) : ViewModel() {


}
