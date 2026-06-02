package com.cscyxp.fitness.home.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cscyxp.fitness.R
import com.cscyxp.fitness.home.ui.state.FitnessScreenUiState
import com.cscyxp.fitness.home.ui.state.FitnessTab
import com.cscyxp.fitness.home.ui.state.MonthlySummaryUiModel
import com.cscyxp.fitness.home.ui.state.TemplateExerciseUiModel
import com.cscyxp.fitness.home.ui.state.WorkoutTemplateUiModel
import com.cscyxp.fitness.ui.theme.FitnessColors

@Composable
internal fun FitnessHeader(onBackClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(start = 24.dp, top = 18.dp, end = 24.dp, bottom = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.fitness_back), tint = FitnessColors.TextPrimary)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(stringResource(R.string.fitness_title), color = FitnessColors.TextPrimary, fontSize = 34.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun FitnessTabs(selectedTab: FitnessTab, onTabSelected: (FitnessTab) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FitnessTab.entries.forEach { tab ->
            val icon = when (tab) {
                FitnessTab.Training -> Icons.Default.FitnessCenter
                FitnessTab.Exercises -> Icons.AutoMirrored.Filled.MenuBook
                FitnessTab.Calendar -> Icons.Default.CalendarMonth
            }
            TabButton(tab, icon, tab == selectedTab, { onTabSelected(tab) }, Modifier.weight(1f))
        }
    }
}

@Composable
private fun TabButton(tab: FitnessTab, icon: ImageVector, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(onClick = onClick, modifier = modifier.height(46.dp), color = if (selected) FitnessColors.Primary else FitnessColors.SurfaceMuted, shape = RoundedCornerShape(14.dp)) {
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = if (selected) FitnessColors.White else FitnessColors.TextSecondary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(tab.labelRes), color = if (selected) FitnessColors.White else FitnessColors.TextSecondary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
internal fun TrainingTab(uiState: FitnessScreenUiState, onCreateClick: () -> Unit, onStartClick: (String) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { PrimaryButton(stringResource(R.string.fitness_create_template), onCreateClick) }
        item { Text(stringResource(R.string.fitness_your_templates), color = FitnessColors.TextSecondary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 12.dp, bottom = 2.dp)) }
        items(uiState.templates) { template -> TemplateCard(template, onStartClick) }
    }
}

@Composable
private fun PrimaryButton(text: String, onClick: () -> Unit) {
    Surface(onClick = onClick, modifier = Modifier.fillMaxWidth().height(58.dp), shape = RoundedCornerShape(16.dp), color = FitnessColors.Transparent, shadowElevation = 8.dp) {
        Row(modifier = Modifier.background(Brush.horizontalGradient(listOf(FitnessColors.Primary, FitnessColors.PrimaryDark))), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Add, contentDescription = null, tint = FitnessColors.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, color = FitnessColors.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun TemplateCard(template: WorkoutTemplateUiModel, onStartClick: (String) -> Unit) {
    val name = stringResource(template.nameRes)
    Card(modifier = Modifier.fillMaxWidth().border(1.dp, FitnessColors.Border, RoundedCornerShape(16.dp)), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = FitnessColors.White)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(name, color = FitnessColors.TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(stringResource(R.string.fitness_exercise_count_last, template.exercises.size + template.remainingExerciseCount, stringResource(template.lastWorkoutRes)), color = FitnessColors.TextSecondary, fontSize = 15.sp)
                }
                Surface(onClick = { onStartClick(name) }, modifier = Modifier.size(46.dp), shape = CircleShape, color = FitnessColors.Primary, shadowElevation = 4.dp) {
                    Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.PlayArrow, contentDescription = stringResource(R.string.fitness_start_workout), tint = FitnessColors.White) }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            template.exercises.forEach { exercise ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(stringResource(exercise.nameRes), color = FitnessColors.TextPrimary, fontSize = 15.sp)
                    Text(exercise.summary(), color = FitnessColors.TextSecondary, fontSize = 15.sp)
                }
            }
            if (template.remainingExerciseCount > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(stringResource(R.string.fitness_more_exercises, template.remainingExerciseCount), color = FitnessColors.Primary, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun TemplateExerciseUiModel.summary(): String = weightKg?.let {
    stringResource(R.string.fitness_template_set_summary, sets, reps, it)
} ?: stringResource(R.string.fitness_template_set_summary_no_weight, sets, reps)

@Composable
internal fun WorkoutCalendarTab(summary: MonthlySummaryUiModel) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        item {
            Column(modifier = Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(FitnessColors.Primary, FitnessColors.PrimaryDark)), RoundedCornerShape(18.dp)).padding(22.dp)) {
                Text(stringResource(R.string.fitness_this_month), color = FitnessColors.White, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row {
                    Metric(R.string.fitness_workouts, summary.workouts.toString(), Modifier.weight(1f))
                    Metric(R.string.fitness_total_time, stringResource(R.string.fitness_minutes_value, summary.totalMinutes), Modifier.weight(1f))
                    Metric(R.string.fitness_total_sets, summary.totalSets.toString(), Modifier.weight(1f))
                }
            }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.fitness_month_june_2026), color = FitnessColors.TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                MonthButton(Icons.AutoMirrored.Filled.KeyboardArrowLeft, R.string.fitness_previous_month)
                Spacer(modifier = Modifier.width(8.dp))
                MonthButton(Icons.AutoMirrored.Filled.KeyboardArrowRight, R.string.fitness_next_month)
            }
        }
        item { CalendarGrid() }
        item { Text(stringResource(R.string.fitness_recent_workouts), color = FitnessColors.TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun Metric(labelRes: Int, value: String, modifier: Modifier) {
    Column(modifier) {
        Text(stringResource(labelRes), color = FitnessColors.White, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(value, color = FitnessColors.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun MonthButton(icon: ImageVector, descriptionRes: Int) {
    Surface(onClick = {}, modifier = Modifier.size(40.dp), shape = CircleShape, color = FitnessColors.SurfaceMuted) {
        Box(contentAlignment = Alignment.Center) { Icon(icon, contentDescription = stringResource(descriptionRes), tint = FitnessColors.TextSecondary) }
    }
}

@Composable
private fun CalendarGrid() {
    val labels = listOf(R.string.fitness_week_sun, R.string.fitness_week_mon, R.string.fitness_week_tue, R.string.fitness_week_wed, R.string.fitness_week_thu, R.string.fitness_week_fri, R.string.fitness_week_sat)
    val days = listOf<String?>(null) + (1..30).map(Int::toString)
    Column(modifier = Modifier.fillMaxWidth().border(1.dp, FitnessColors.Border, RoundedCornerShape(16.dp)).padding(horizontal = 12.dp, vertical = 18.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Row { labels.forEach { CalendarCell(stringResource(it), Modifier.weight(1f)) } }
        days.chunked(7).forEach { week ->
            Row { week.forEach { CalendarCell(it.orEmpty(), Modifier.weight(1f)) }; repeat(7 - week.size) { Spacer(Modifier.weight(1f)) } }
        }
    }
}

@Composable
private fun CalendarCell(text: String, modifier: Modifier) {
    Box(modifier.height(22.dp), contentAlignment = Alignment.Center) { Text(text, color = FitnessColors.TextSecondary, fontSize = 13.sp) }
}
