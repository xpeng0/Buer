package com.cscyxp.fitness.exercise.ui.state

import androidx.annotation.StringRes
import com.cscyxp.fitness.R

internal data class ExerciseBrowserUiState(
    val categories: List<ExerciseCategoryUiModel>,
    val exercises: List<ExerciseUiModel>,
)

internal data class AddExerciseScreenUiState(
    val browserUiState: ExerciseBrowserUiState,
    val selectedExerciseId: String? = null,
)

internal data class ExerciseCategoryUiModel(
    @StringRes val nameRes: Int,
    @StringRes val subcategoryResIds: List<Int>,
)

internal data class ExerciseUiModel(
    val id: String,
    @StringRes val nameRes: Int,
    @StringRes val categoryRes: Int,
    @StringRes val subcategoryRes: Int,
    @StringRes val equipmentRes: Int,
)

internal val ExerciseBrowserPreviewState = ExerciseBrowserUiState(
    categories = listOf(
        ExerciseCategoryUiModel(R.string.fitness_category_chest, listOf(R.string.fitness_subcategory_upper_chest, R.string.fitness_subcategory_middle_chest, R.string.fitness_subcategory_lower_chest)),
        ExerciseCategoryUiModel(R.string.fitness_category_back, listOf(R.string.fitness_subcategory_lats, R.string.fitness_subcategory_upper_back)),
        ExerciseCategoryUiModel(R.string.fitness_category_legs, listOf(R.string.fitness_subcategory_quads, R.string.fitness_subcategory_hamstrings, R.string.fitness_subcategory_calves)),
        ExerciseCategoryUiModel(R.string.fitness_category_glutes, listOf(R.string.fitness_subcategory_glute_max, R.string.fitness_subcategory_glute_med)),
        ExerciseCategoryUiModel(R.string.fitness_category_shoulders, listOf(R.string.fitness_subcategory_front_delts, R.string.fitness_subcategory_side_delts, R.string.fitness_subcategory_rear_delts)),
        ExerciseCategoryUiModel(R.string.fitness_category_biceps, emptyList()),
        ExerciseCategoryUiModel(R.string.fitness_category_triceps, emptyList()),
        ExerciseCategoryUiModel(R.string.fitness_category_abs, emptyList()),
    ),
    exercises = listOf(
        ExerciseUiModel("barbell_incline_press", R.string.fitness_exercise_barbell_incline_press, R.string.fitness_category_chest, R.string.fitness_subcategory_upper_chest, R.string.fitness_equipment_barbell),
        ExerciseUiModel("dumbbell_incline_press", R.string.fitness_exercise_dumbbell_incline_press, R.string.fitness_category_chest, R.string.fitness_subcategory_upper_chest, R.string.fitness_equipment_dumbbell),
        ExerciseUiModel("machine_incline_press", R.string.fitness_exercise_machine_incline_press, R.string.fitness_category_chest, R.string.fitness_subcategory_upper_chest, R.string.fitness_equipment_machine),
        ExerciseUiModel("cable_fly", R.string.fitness_exercise_cable_fly, R.string.fitness_category_chest, R.string.fitness_subcategory_middle_chest, R.string.fitness_equipment_cable),
        ExerciseUiModel("flat_bench_press", R.string.fitness_exercise_flat_bench_press, R.string.fitness_category_chest, R.string.fitness_subcategory_middle_chest, R.string.fitness_equipment_barbell),
        ExerciseUiModel("dips", R.string.fitness_exercise_dips, R.string.fitness_category_chest, R.string.fitness_subcategory_lower_chest, R.string.fitness_equipment_bodyweight),
        ExerciseUiModel("lat_pulldown", R.string.fitness_exercise_lat_pulldown, R.string.fitness_category_back, R.string.fitness_subcategory_lats, R.string.fitness_equipment_machine),
        ExerciseUiModel("seated_row", R.string.fitness_exercise_seated_row, R.string.fitness_category_back, R.string.fitness_subcategory_upper_back, R.string.fitness_equipment_machine),
        ExerciseUiModel("squat", R.string.fitness_exercise_squat, R.string.fitness_category_legs, R.string.fitness_subcategory_quads, R.string.fitness_equipment_barbell),
        ExerciseUiModel("romanian_deadlift", R.string.fitness_exercise_romanian_deadlift, R.string.fitness_category_legs, R.string.fitness_subcategory_hamstrings, R.string.fitness_equipment_barbell),
        ExerciseUiModel("hip_thrust", R.string.fitness_exercise_hip_thrust, R.string.fitness_category_glutes, R.string.fitness_subcategory_glute_max, R.string.fitness_equipment_barbell),
        ExerciseUiModel("lateral_raise", R.string.fitness_exercise_lateral_raise, R.string.fitness_category_shoulders, R.string.fitness_subcategory_side_delts, R.string.fitness_equipment_dumbbell),
    ),
)

internal val AddExercisePreviewState = AddExerciseScreenUiState(ExerciseBrowserPreviewState)
