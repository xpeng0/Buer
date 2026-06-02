package com.cscyxp.fitness.template.vm

import androidx.lifecycle.ViewModel
import com.cscyxp.fitness.template.ui.state.CreateTemplateScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
internal class CreateTemplateViewModel @Inject constructor() : ViewModel() {
    private val _createTemplateScreenUiState = MutableStateFlow(CreateTemplateScreenUiState())
    val createTemplateScreenUiState: StateFlow<CreateTemplateScreenUiState> = _createTemplateScreenUiState.asStateFlow()

    fun updateTemplateName(name: String) {
        _createTemplateScreenUiState.value = _createTemplateScreenUiState.value.copy(templateName = name)
    }
}
