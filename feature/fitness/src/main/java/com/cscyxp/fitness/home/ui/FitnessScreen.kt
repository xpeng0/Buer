package com.cscyxp.fitness.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cscyxp.fitness.exercise.ui.composable.ExerciseBrowser
import com.cscyxp.fitness.exercise.ui.state.ExerciseBrowserPreviewState
import com.cscyxp.fitness.home.ui.composable.FitnessHeader
import com.cscyxp.fitness.home.ui.composable.FitnessTabs
import com.cscyxp.fitness.home.ui.composable.TrainingTab
import com.cscyxp.fitness.home.ui.composable.WorkoutCalendarTab
import com.cscyxp.fitness.home.ui.state.FitnessPreviewState
import com.cscyxp.fitness.home.ui.state.FitnessScreenUiState
import com.cscyxp.fitness.home.ui.state.FitnessTab
import com.cscyxp.fitness.home.vm.FitnessViewModel
import com.cscyxp.fitness.ui.theme.FitnessColors

@Composable
internal fun FitnessRoute(
    onBackClick: () -> Unit,
    onCreateTemplateClick: () -> Unit,
    onAddExerciseClick: () -> Unit,
    onStartWorkoutClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FitnessViewModel = hiltViewModel(),
) {
    val uiState by viewModel.fitnessScreenUiState.collectAsStateWithLifecycle()
    FitnessScreen(
        uiState = uiState,
        onTabSelected = viewModel::selectTab,
        onBackClick = onBackClick,
        onCreateTemplateClick = onCreateTemplateClick,
        onAddExerciseClick = onAddExerciseClick,
        onStartWorkoutClick = onStartWorkoutClick,
        modifier = modifier,
    )
}

@Composable
internal fun FitnessScreen(
    uiState: FitnessScreenUiState,
    onTabSelected: (FitnessTab) -> Unit,
    onBackClick: () -> Unit,
    onCreateTemplateClick: () -> Unit,
    onAddExerciseClick: () -> Unit,
    onStartWorkoutClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().safeDrawingPadding().background(FitnessColors.Background)) {
        FitnessHeader(onBackClick)
        FitnessTabs(uiState.selectedTab, onTabSelected)
        Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(FitnessColors.Border))
        when (uiState.selectedTab) {
            FitnessTab.Training -> TrainingTab(uiState, onCreateTemplateClick, onStartWorkoutClick)
            FitnessTab.Exercises -> ExerciseBrowser(
                uiState = ExerciseBrowserPreviewState,
                selectedExerciseId = null,
                onExerciseSelected = {},
                onAddExerciseClick = onAddExerciseClick,
            )
            FitnessTab.Calendar -> WorkoutCalendarTab(uiState.monthlySummary)
        }
    }
}

@Preview(showBackground = true, heightDp = 760)
@Composable
private fun FitnessScreenPreview() {
    FitnessScreen(FitnessPreviewState, {}, {}, {}, {}, {})
}
