package com.cscyxp.fitness.exercise.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cscyxp.fitness.R
import com.cscyxp.fitness.exercise.ui.state.ExerciseBrowserUiState
import com.cscyxp.fitness.exercise.ui.state.ExerciseCategoryUiModel
import com.cscyxp.fitness.exercise.ui.state.ExerciseUiModel
import com.cscyxp.fitness.ui.theme.FitnessColors

@Composable
internal fun ExerciseBrowser(
    uiState: ExerciseBrowserUiState,
    selectedExerciseId: String?,
    onExerciseSelected: (ExerciseUiModel) -> Unit,
    modifier: Modifier = Modifier,
    onAddExerciseClick: (() -> Unit)? = null,
) {
    var query by remember { mutableStateOf("") }
    var selectedCategoryRes by remember { mutableStateOf(uiState.categories.first().nameRes) }
    var selectedSubcategoryRes by remember { mutableStateOf<Int?>(null) }
    var selectedEquipmentRes by remember { mutableStateOf<Int?>(null) }
    val category = uiState.categories.first { it.nameRes == selectedCategoryRes }
    val filteredExercises = uiState.exercises.filter { exercise ->
        exercise.categoryRes == selectedCategoryRes &&
            (selectedSubcategoryRes == null || exercise.subcategoryRes == selectedSubcategoryRes) &&
            (selectedEquipmentRes == null || exercise.equipmentRes == selectedEquipmentRes) &&
            (query.isBlank() || stringResource(exercise.nameRes).contains(query, ignoreCase = true))
    }
    val equipmentResIds = uiState.exercises
        .filter { it.categoryRes == selectedCategoryRes }
        .map { it.equipmentRes }
        .distinct()

    Column(modifier = modifier.fillMaxSize().background(FitnessColors.Background)) {
        ExerciseSearchRow(query, { query = it }, onAddExerciseClick)
        Row(modifier = Modifier.weight(1f)) {
            ExerciseCategorySidebar(
                categories = uiState.categories,
                selectedCategoryRes = selectedCategoryRes,
                selectedSubcategoryRes = selectedSubcategoryRes,
                onCategorySelected = {
                    selectedCategoryRes = it
                    selectedSubcategoryRes = null
                    selectedEquipmentRes = null
                },
                onSubcategorySelected = { selectedSubcategoryRes = it },
            )
            Column(modifier = Modifier.weight(1f).padding(start = 12.dp, end = 14.dp)) {
                EquipmentFilters(equipmentResIds, selectedEquipmentRes) { selectedEquipmentRes = it }
                Text(
                    text = stringResource(selectedSubcategoryRes ?: category.nameRes),
                    color = FitnessColors.TextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 10.dp),
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(filteredExercises) { exercise ->
                        ExerciseCard(exercise, selectedExerciseId == exercise.id) { onExerciseSelected(exercise) }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExerciseSearchRow(
    query: String,
    onQueryChange: (String) -> Unit,
    onAddExerciseClick: (() -> Unit)?,
) {
    Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.weight(1f).clip(RoundedCornerShape(14.dp)).background(FitnessColors.SurfaceMuted).padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = FitnessColors.TextMuted)
            Spacer(modifier = Modifier.width(8.dp))
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = FitnessColors.TextPrimary, fontSize = 16.sp),
                singleLine = true,
                decorationBox = { field ->
                    if (query.isBlank()) Text(stringResource(R.string.fitness_search_exercises), color = FitnessColors.TextMuted, fontSize = 16.sp)
                    field()
                },
            )
        }
        if (onAddExerciseClick != null) {
            Surface(onClick = onAddExerciseClick, modifier = Modifier.size(50.dp), shape = RoundedCornerShape(14.dp), color = FitnessColors.Primary) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.fitness_add_exercise), tint = FitnessColors.White)
                }
            }
        }
    }
}

@Composable
private fun ExerciseCategorySidebar(
    categories: List<ExerciseCategoryUiModel>,
    selectedCategoryRes: Int,
    selectedSubcategoryRes: Int?,
    onCategorySelected: (Int) -> Unit,
    onSubcategorySelected: (Int) -> Unit,
) {
    Column(modifier = Modifier.width(88.dp).fillMaxSize().background(FitnessColors.SurfaceMuted).verticalScroll(rememberScrollState()).padding(vertical = 6.dp)) {
        categories.forEach { category ->
            SidebarItem(
                text = stringResource(category.nameRes),
                selected = category.nameRes == selectedCategoryRes && selectedSubcategoryRes == null,
                bold = true,
                onClick = { onCategorySelected(category.nameRes) },
            )
            if (category.nameRes == selectedCategoryRes) {
                category.subcategoryResIds.forEach { subcategoryRes ->
                    SidebarItem(
                        text = stringResource(subcategoryRes),
                        selected = subcategoryRes == selectedSubcategoryRes,
                        indented = true,
                        onClick = { onSubcategorySelected(subcategoryRes) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SidebarItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    bold: Boolean = false,
    indented: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
            .background(if (selected) FitnessColors.PrimarySoft else FitnessColors.Transparent)
            .padding(start = if (indented) 24.dp else 8.dp, end = 8.dp, top = if (indented) 9.dp else 11.dp, bottom = if (indented) 9.dp else 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (!indented) {
            Box(modifier = Modifier.width(3.dp).height(22.dp).background(if (selected) FitnessColors.Primary else FitnessColors.Transparent))
            Spacer(modifier = Modifier.width(5.dp))
        }
        Text(
            text = text,
            color = if (selected) FitnessColors.PrimaryDark else if (indented) FitnessColors.TextSubcategory else FitnessColors.TextSecondary,
            fontSize = if (indented) 13.sp else 14.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        )
    }
}

@Composable
private fun EquipmentFilters(
    equipmentResIds: List<Int>,
    selectedEquipmentRes: Int?,
    onEquipmentSelected: (Int?) -> Unit,
) {
    LazyRow(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        item { EquipmentFilterChip(R.string.fitness_equipment_all, selectedEquipmentRes == null) { onEquipmentSelected(null) } }
        items(equipmentResIds) { equipmentRes ->
            EquipmentFilterChip(equipmentRes, selectedEquipmentRes == equipmentRes) { onEquipmentSelected(equipmentRes) }
        }
    }
}

@Composable
private fun EquipmentFilterChip(labelRes: Int, selected: Boolean, onClick: () -> Unit) {
    Surface(onClick = onClick, color = if (selected) FitnessColors.Primary else FitnessColors.White, shape = RoundedCornerShape(8.dp)) {
        Text(
            text = stringResource(labelRes),
            color = if (selected) FitnessColors.White else FitnessColors.TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
        )
    }
}

@Composable
private fun ExerciseCard(exercise: ExerciseUiModel, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
            .background(if (selected) FitnessColors.PrimarySoft else FitnessColors.White)
            .border(1.dp, if (selected) FitnessColors.Primary else FitnessColors.Border, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick).padding(horizontal = 10.dp, vertical = 9.dp),
    ) {
        Text(stringResource(exercise.nameRes), color = FitnessColors.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(3.dp))
        Text(stringResource(exercise.equipmentRes), color = FitnessColors.TextMuted, fontSize = 12.sp)
    }
}
