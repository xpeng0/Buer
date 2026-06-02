package com.cscyxp.fitness.home.vm

import androidx.lifecycle.ViewModel
import com.cscyxp.fitness.home.ui.state.FitnessPreviewState
import com.cscyxp.fitness.home.ui.state.FitnessScreenUiState
import com.cscyxp.fitness.home.ui.state.FitnessTab
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
internal class FitnessViewModel @Inject constructor() : ViewModel() {
    private val _fitnessScreenUiState = MutableStateFlow(FitnessPreviewState)
    val fitnessScreenUiState: StateFlow<FitnessScreenUiState> = _fitnessScreenUiState.asStateFlow()

    fun selectTab(tab: FitnessTab) {
        _fitnessScreenUiState.value = _fitnessScreenUiState.value.copy(selectedTab = tab)
    }
}
