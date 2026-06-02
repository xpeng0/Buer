package com.cscyxp.fitness.exercise.vm

import androidx.lifecycle.ViewModel
import com.cscyxp.fitness.exercise.ui.state.ExerciseBrowserPreviewState
import com.cscyxp.fitness.exercise.ui.state.AddExercisePreviewState
import com.cscyxp.fitness.exercise.ui.state.AddExerciseScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
internal class AddExerciseViewModel @Inject constructor() : ViewModel() {
    private val _addExerciseScreenUiState = MutableStateFlow(AddExercisePreviewState)
    val addExerciseScreenUiState: StateFlow<AddExerciseScreenUiState> = _addExerciseScreenUiState.asStateFlow()

    fun selectExercise(id: String) {
        _addExerciseScreenUiState.value = _addExerciseScreenUiState.value.copy(selectedExerciseId = id)
    }
}
