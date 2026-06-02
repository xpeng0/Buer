package com.cscyxp.fitness.workout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cscyxp.fitness.R
import com.cscyxp.fitness.ui.composable.FitnessGradientButton
import com.cscyxp.fitness.ui.composable.FitnessSecondaryButton
import com.cscyxp.fitness.ui.theme.FitnessColors
import com.cscyxp.fitness.workout.ui.composable.ActiveExerciseCard
import com.cscyxp.fitness.workout.ui.composable.ActiveWorkoutHeader
import com.cscyxp.fitness.workout.ui.composable.RestTimerDialog
import com.cscyxp.fitness.workout.ui.state.ActiveWorkoutScreenUiState
import com.cscyxp.fitness.workout.ui.state.activeWorkoutPreviewExercises
import com.cscyxp.fitness.workout.vm.ActiveWorkoutViewModel

@Composable
internal fun ActiveWorkoutRoute(
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ActiveWorkoutViewModel = hiltViewModel(),
) {
    val uiState by viewModel.activeWorkoutScreenUiState.collectAsStateWithLifecycle()
    ActiveWorkoutScreen(
        uiState = uiState,
        onCloseClick = onCloseClick,
        onFinishClick = onCloseClick,
        onToggleExercise = viewModel::toggleExercise,
        onDeleteExercise = viewModel::deleteExercise,
        onAddExercise = viewModel::addExercise,
        onAddSet = viewModel::addSet,
        onDeleteSet = viewModel::deleteSet,
        onCompleteSet = viewModel::completeSet,
        onRestDismiss = viewModel::dismissRestTimer,
        onRestSecondsChange = viewModel::updateRestSeconds,
        modifier = modifier,
    )
}

@Composable
internal fun ActiveWorkoutScreen(
    uiState: ActiveWorkoutScreenUiState,
    onCloseClick: () -> Unit,
    onFinishClick: () -> Unit,
    onToggleExercise: (Int) -> Unit,
    onDeleteExercise: (Int) -> Unit,
    onAddExercise: () -> Unit,
    onAddSet: (Int) -> Unit,
    onDeleteSet: (Int, Int) -> Unit,
    onCompleteSet: (Int, Int) -> Unit,
    onRestDismiss: () -> Unit,
    onRestSecondsChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val totalSets = uiState.exercises.sumOf { it.sets.size }
    val completedSets = uiState.exercises.sumOf { exercise -> exercise.sets.count { it.completed } }
    Box(modifier = modifier.fillMaxSize().safeDrawingPadding().background(FitnessColors.Background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            ActiveWorkoutHeader(uiState.workoutName, completedSets, totalSets, onCloseClick)
            LazyColumn(modifier = Modifier.weight(1f), contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp)) {
                items(uiState.exercises, key = { it.id }) { exercise ->
                    ActiveExerciseCard(
                        exercise,
                        { onToggleExercise(exercise.id) },
                        { onDeleteExercise(exercise.id) },
                        { onAddSet(exercise.id) },
                        { onDeleteSet(exercise.id, it) },
                        { onCompleteSet(exercise.id, it) },
                        Modifier.padding(bottom = 14.dp),
                    )
                }
                item { FitnessSecondaryButton(stringResource(R.string.fitness_add_exercise), onAddExercise) }
            }
            FitnessGradientButton(stringResource(R.string.fitness_finish_workout), onFinishClick, Modifier.padding(horizontal = 14.dp, vertical = 12.dp))
        }
    }
    if (uiState.restTimer != null) RestTimerDialog(uiState.restTimer, uiState.restSeconds, onRestDismiss, onRestSecondsChange)
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun ActiveWorkoutScreenPreview() {
    ActiveWorkoutScreen(ActiveWorkoutScreenUiState(stringResource(R.string.fitness_template_push_day), activeWorkoutPreviewExercises()), {}, {}, {}, {}, {}, {}, { _, _ -> }, { _, _ -> }, {}, {})
}
