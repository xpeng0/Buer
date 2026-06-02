package com.cscyxp.fitness.home.ui.state

import androidx.annotation.StringRes
import com.cscyxp.fitness.R

internal data class FitnessScreenUiState(
    val selectedTab: FitnessTab,
    val templates: List<WorkoutTemplateUiModel>,
    val monthlySummary: MonthlySummaryUiModel,
)

internal data class WorkoutTemplateUiModel(
    @StringRes val nameRes: Int,
    @StringRes val lastWorkoutRes: Int,
    val exercises: List<TemplateExerciseUiModel>,
    val remainingExerciseCount: Int,
)

internal data class TemplateExerciseUiModel(
    @StringRes val nameRes: Int,
    val sets: Int,
    val reps: Int,
    val weightKg: Int? = null,
)

internal data class MonthlySummaryUiModel(
    val workouts: Int,
    val totalMinutes: Int,
    val totalSets: Int,
)

internal enum class FitnessTab(@StringRes val labelRes: Int) {
    Training(R.string.fitness_tab_training),
    Exercises(R.string.fitness_tab_exercises),
    Calendar(R.string.fitness_tab_calendar),
}

internal val FitnessPreviewState = FitnessScreenUiState(
    selectedTab = FitnessTab.Training,
    templates = listOf(
        WorkoutTemplateUiModel(
            R.string.fitness_template_push_day,
            R.string.fitness_last_may_28,
            listOf(
                TemplateExerciseUiModel(R.string.fitness_exercise_bench_press, 4, 8, 80),
                TemplateExerciseUiModel(R.string.fitness_exercise_incline_dumbbell_press, 3, 10, 30),
                TemplateExerciseUiModel(R.string.fitness_exercise_cable_flyes, 3, 12, 15),
            ),
            2,
        ),
        WorkoutTemplateUiModel(
            R.string.fitness_template_pull_day,
            R.string.fitness_last_may_26,
            listOf(
                TemplateExerciseUiModel(R.string.fitness_exercise_deadlift, 4, 6, 120),
                TemplateExerciseUiModel(R.string.fitness_exercise_pull_ups, 4, 10),
                TemplateExerciseUiModel(R.string.fitness_exercise_barbell_rows, 4, 8, 70),
            ),
            1,
        ),
        WorkoutTemplateUiModel(
            R.string.fitness_template_leg_day,
            R.string.fitness_last_may_24,
            listOf(
                TemplateExerciseUiModel(R.string.fitness_exercise_back_squat, 4, 8, 100),
                TemplateExerciseUiModel(R.string.fitness_exercise_leg_press, 3, 10, 140),
                TemplateExerciseUiModel(R.string.fitness_exercise_calf_raises, 3, 15, 60),
            ),
            1,
        ),
    ),
    monthlySummary = MonthlySummaryUiModel(0, 0, 0),
)
