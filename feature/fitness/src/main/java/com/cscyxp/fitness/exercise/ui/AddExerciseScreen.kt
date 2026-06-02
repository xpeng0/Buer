package com.cscyxp.fitness.exercise.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cscyxp.fitness.R
import com.cscyxp.fitness.exercise.ui.composable.ExerciseBrowser
import com.cscyxp.fitness.exercise.ui.state.AddExercisePreviewState
import com.cscyxp.fitness.exercise.ui.state.AddExerciseScreenUiState
import com.cscyxp.fitness.exercise.ui.state.ExerciseUiModel
import com.cscyxp.fitness.exercise.vm.AddExerciseViewModel
import com.cscyxp.fitness.ui.composable.FitnessGradientButton
import com.cscyxp.fitness.ui.composable.FitnessPageHeader
import com.cscyxp.fitness.ui.theme.FitnessColors

@Composable
internal fun AddExerciseRoute(onBackClick: () -> Unit, modifier: Modifier = Modifier, viewModel: AddExerciseViewModel = hiltViewModel()) {
    val uiState by viewModel.addExerciseScreenUiState.collectAsStateWithLifecycle()
    AddExerciseScreen(
        uiState = uiState,
        onExerciseSelected = { viewModel.selectExercise(it.id) },
        onBackClick = onBackClick,
        onConfirmClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
internal fun AddExerciseScreen(
    uiState: AddExerciseScreenUiState,
    onExerciseSelected: (ExerciseUiModel) -> Unit,
    onBackClick: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().safeDrawingPadding().background(FitnessColors.Background)) {
        FitnessPageHeader(stringResource(R.string.fitness_add_exercise), onBackClick)
        ExerciseBrowser(uiState.browserUiState, uiState.selectedExerciseId, onExerciseSelected, Modifier.weight(1f))
        FitnessGradientButton(
            text = stringResource(R.string.fitness_add_selected_exercise),
            onClick = onConfirmClick,
            enabled = uiState.selectedExerciseId != null,
            modifier = Modifier,
        )
    }
}

@Preview(showBackground = true, heightDp = 760)
@Composable
private fun AddExerciseScreenPreview() {
    AddExerciseScreen(AddExercisePreviewState.copy(selectedExerciseId = "barbell_incline_press"), {}, {}, {})
}
