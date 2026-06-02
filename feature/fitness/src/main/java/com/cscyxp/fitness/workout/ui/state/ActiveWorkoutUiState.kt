package com.cscyxp.fitness.workout.ui.state

import androidx.annotation.StringRes
import com.cscyxp.fitness.R

internal data class ActiveWorkoutExerciseUiModel(
    val id: Int,
    @StringRes val nameRes: Int,
    val expanded: Boolean,
    val sets: List<ActiveWorkoutSetUiModel>,
)

internal data class ActiveWorkoutSetUiModel(
    val id: Int,
    val reps: Int,
    val weightKg: Int,
    val completed: Boolean = false,
)

internal data class RestTimerUiModel(
    @StringRes val exerciseNameRes: Int,
    val completedSets: Int,
    val totalSets: Int,
)

internal data class ActiveWorkoutScreenUiState(
    val workoutName: String,
    val exercises: List<ActiveWorkoutExerciseUiModel>,
    val restTimer: RestTimerUiModel? = null,
    val restSeconds: Int = 60,
)

internal fun activeWorkoutPreviewExercises() = listOf(
    ActiveWorkoutExerciseUiModel(1, R.string.fitness_exercise_bench_press, true, List(4) { ActiveWorkoutSetUiModel(it + 1, 8, 80) }),
    ActiveWorkoutExerciseUiModel(2, R.string.fitness_exercise_incline_dumbbell_press, true, List(3) { ActiveWorkoutSetUiModel(it + 1, 10, 30) }),
    ActiveWorkoutExerciseUiModel(3, R.string.fitness_exercise_cable_flyes, false, List(3) { ActiveWorkoutSetUiModel(it + 1, 12, 15) }),
)
