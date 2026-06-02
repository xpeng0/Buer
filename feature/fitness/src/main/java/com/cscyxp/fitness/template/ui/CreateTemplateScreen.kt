package com.cscyxp.fitness.template.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cscyxp.fitness.R
import com.cscyxp.fitness.template.ui.composable.TemplateExerciseEditor
import com.cscyxp.fitness.template.ui.composable.TemplateNameField
import com.cscyxp.fitness.template.ui.state.CreateTemplateScreenUiState
import com.cscyxp.fitness.template.vm.CreateTemplateViewModel
import com.cscyxp.fitness.ui.composable.FitnessGradientButton
import com.cscyxp.fitness.ui.composable.FitnessPageHeader
import com.cscyxp.fitness.ui.composable.FitnessSecondaryButton
import com.cscyxp.fitness.ui.theme.FitnessColors

@Composable
internal fun CreateTemplateRoute(
    onBackClick: () -> Unit,
    onAddExerciseClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateTemplateViewModel = hiltViewModel(),
) {
    val uiState by viewModel.createTemplateScreenUiState.collectAsStateWithLifecycle()
    CreateTemplateScreen(uiState, viewModel::updateTemplateName, onBackClick, onAddExerciseClick, modifier)
}

@Composable
internal fun CreateTemplateScreen(
    uiState: CreateTemplateScreenUiState,
    onTemplateNameChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onAddExerciseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().safeDrawingPadding().background(FitnessColors.Background)) {
        FitnessPageHeader(stringResource(R.string.fitness_template_create_title), onBackClick)
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 22.dp, vertical = 20.dp)) {
            TemplateNameField(uiState.templateName, onTemplateNameChange)
            Spacer(modifier = Modifier.height(26.dp))
            TemplateExerciseEditor()
            Spacer(modifier = Modifier.height(16.dp))
            FitnessSecondaryButton(stringResource(R.string.fitness_add_exercise), onAddExerciseClick)
        }
        FitnessGradientButton(stringResource(R.string.fitness_save_template), {}, enabled = uiState.templateName.isNotBlank())
    }
}

@Preview(showBackground = true, heightDp = 760)
@Composable
private fun CreateTemplateScreenPreview() {
    CreateTemplateScreen(CreateTemplateScreenUiState(stringResource(R.string.fitness_template_push_day)), {}, {}, {})
}
