package com.cscyxp.fitness.workout.vm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.cscyxp.fitness.R
import com.cscyxp.fitness.navigation.FitnessActiveWorkout
import com.cscyxp.fitness.workout.ui.state.ActiveWorkoutExerciseUiModel
import com.cscyxp.fitness.workout.ui.state.ActiveWorkoutScreenUiState
import com.cscyxp.fitness.workout.ui.state.ActiveWorkoutSetUiModel
import com.cscyxp.fitness.workout.ui.state.RestTimerUiModel
import com.cscyxp.fitness.workout.ui.state.activeWorkoutPreviewExercises
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
internal class ActiveWorkoutViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val route = savedStateHandle.toRoute<FitnessActiveWorkout>()
    private val _activeWorkoutScreenUiState = MutableStateFlow(
        ActiveWorkoutScreenUiState(route.templateName, activeWorkoutPreviewExercises()),
    )
    val activeWorkoutScreenUiState: StateFlow<ActiveWorkoutScreenUiState> = _activeWorkoutScreenUiState.asStateFlow()

    fun toggleExercise(id: Int) = updateExercise(id) { it.copy(expanded = !it.expanded) }

    fun deleteExercise(id: Int) {
        updateState { it.copy(exercises = it.exercises.filterNot { exercise -> exercise.id == id }) }
    }

    fun addExercise() {
        updateState { state ->
            val id = (state.exercises.maxOfOrNull { it.id } ?: 0) + 1
            state.copy(exercises = state.exercises + ActiveWorkoutExerciseUiModel(id, R.string.fitness_exercise_cable_flyes, true, listOf(ActiveWorkoutSetUiModel(1, 12, 15))))
        }
    }

    fun addSet(id: Int) = updateExercise(id) { exercise ->
        val last = exercise.sets.lastOrNull()
        exercise.copy(sets = exercise.sets + ActiveWorkoutSetUiModel((exercise.sets.maxOfOrNull { it.id } ?: 0) + 1, last?.reps ?: 10, last?.weightKg ?: 0))
    }

    fun deleteSet(exerciseId: Int, setId: Int) = updateExercise(exerciseId) {
        it.copy(sets = it.sets.filterNot { set -> set.id == setId })
    }

    fun completeSet(exerciseId: Int, setId: Int) = updateState { state ->
        var timer = state.restTimer
        var seconds = state.restSeconds
        val exercises = state.exercises.map { exercise ->
            if (exercise.id != exerciseId) return@map exercise
            val updated = exercise.copy(sets = exercise.sets.map { if (it.id == setId) it.copy(completed = !it.completed) else it })
            if (updated.sets.first { it.id == setId }.completed) {
                seconds = 60
                timer = RestTimerUiModel(updated.nameRes, updated.sets.count { it.completed }, updated.sets.size)
            }
            updated
        }
        state.copy(exercises = exercises, restTimer = timer, restSeconds = seconds)
    }

    fun dismissRestTimer() {
        updateState { it.copy(restTimer = null) }
    }

    fun updateRestSeconds(seconds: Int) {
        updateState { it.copy(restSeconds = seconds.coerceAtLeast(0)) }
    }

    private fun updateExercise(id: Int, transform: (ActiveWorkoutExerciseUiModel) -> ActiveWorkoutExerciseUiModel) {
        updateState { state -> state.copy(exercises = state.exercises.map { if (it.id == id) transform(it) else it }) }
    }

    private fun updateState(transform: (ActiveWorkoutScreenUiState) -> ActiveWorkoutScreenUiState) {
        _activeWorkoutScreenUiState.value = transform(_activeWorkoutScreenUiState.value)
    }
}
