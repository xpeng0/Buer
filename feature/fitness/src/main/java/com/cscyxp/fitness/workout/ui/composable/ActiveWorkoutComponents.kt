package com.cscyxp.fitness.workout.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cscyxp.fitness.R
import com.cscyxp.fitness.ui.composable.FitnessSecondaryButton
import com.cscyxp.fitness.ui.theme.FitnessColors
import com.cscyxp.fitness.workout.ui.state.ActiveWorkoutExerciseUiModel
import com.cscyxp.fitness.workout.ui.state.ActiveWorkoutSetUiModel
import com.cscyxp.fitness.workout.ui.state.RestTimerUiModel
import kotlinx.coroutines.delay

@Composable
internal fun ActiveWorkoutHeader(name: String, completedSets: Int, totalSets: Int, onCloseClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(name, color = FitnessColors.TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(stringResource(R.string.fitness_active_progress, completedSets, totalSets), color = FitnessColors.TextSecondary, fontSize = 16.sp)
        }
        Surface(onClick = onCloseClick, modifier = Modifier.size(46.dp), shape = CircleShape, color = FitnessColors.SurfaceMuted) {
            Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Close, contentDescription = stringResource(R.string.fitness_active_close), tint = FitnessColors.TextSecondary) }
        }
    }
}

@Composable
internal fun ActiveExerciseCard(
    exercise: ActiveWorkoutExerciseUiModel,
    onToggleClick: () -> Unit,
    onDeleteExercise: () -> Unit,
    onAddSet: () -> Unit,
    onDeleteSet: (Int) -> Unit,
    onCompleteSet: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth().background(FitnessColors.SurfaceMuted, RoundedCornerShape(18.dp)).padding(14.dp)) {
        Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onToggleClick), verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(exercise.nameRes), modifier = Modifier.weight(1f), color = FitnessColors.TextPrimary, fontSize = 19.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = onDeleteExercise, modifier = Modifier.size(34.dp)) { Icon(Icons.Default.DeleteOutline, contentDescription = stringResource(R.string.fitness_delete_exercise), tint = FitnessColors.Danger) }
        }
        if (exercise.expanded) {
            Spacer(modifier = Modifier.height(10.dp))
            exercise.sets.forEachIndexed { index, set ->
                ActiveSetRow(index, set, { onDeleteSet(set.id) }, { onCompleteSet(set.id) })
                if (index != exercise.sets.lastIndex) Spacer(modifier = Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            FitnessSecondaryButton(stringResource(R.string.fitness_add_set), onAddSet)
        }
    }
}

@Composable
private fun ActiveSetRow(index: Int, set: ActiveWorkoutSetUiModel, onDeleteClick: () -> Unit, onCompleteClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().background(FitnessColors.White, RoundedCornerShape(14.dp)).padding(start = 12.dp, top = 8.dp, end = 6.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(stringResource(R.string.fitness_set_number, index + 1), color = FitnessColors.TextSecondary, fontSize = 15.sp, modifier = Modifier.width(56.dp))
        Text(stringResource(R.string.fitness_reps_value, set.reps), color = FitnessColors.TextPrimary, fontSize = 15.sp, modifier = Modifier.weight(1f))
        Text(stringResource(R.string.fitness_weight_value, set.weightKg), color = FitnessColors.TextPrimary, fontSize = 15.sp)
        IconButton(onClick = onDeleteClick, modifier = Modifier.size(34.dp)) { Icon(Icons.Default.DeleteOutline, contentDescription = stringResource(R.string.fitness_delete_set), tint = FitnessColors.TextMuted, modifier = Modifier.size(18.dp)) }
        Surface(onClick = onCompleteClick, modifier = Modifier.size(36.dp), shape = CircleShape, color = if (set.completed) FitnessColors.Primary else FitnessColors.SurfaceMuted) {
            Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Check, contentDescription = stringResource(R.string.fitness_complete_set), tint = if (set.completed) FitnessColors.White else FitnessColors.TextMuted, modifier = Modifier.size(20.dp)) }
        }
    }
}

@Composable
internal fun RestTimerDialog(timer: RestTimerUiModel, seconds: Int, onDismiss: () -> Unit, onSecondsChange: (Int) -> Unit) {
    LaunchedEffect(seconds) { if (seconds > 0) { delay(1_000); onSecondsChange(seconds - 1) } else onDismiss() }
    Dialog(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().background(FitnessColors.RestTimer, RoundedCornerShape(18.dp)).padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Timer, contentDescription = null, tint = FitnessColors.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.fitness_rest_time), color = FitnessColors.White, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Surface(onClick = onDismiss, color = FitnessColors.RestTimerAction, shape = RoundedCornerShape(18.dp)) { Text(stringResource(R.string.fitness_skip), color = FitnessColors.White, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(stringResource(R.string.fitness_rest_progress, stringResource(timer.exerciseNameRes), timer.completedSets, timer.totalSets), color = FitnessColors.RestTimerSecondaryText, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.fitness_timer_value, seconds.toString().padStart(2, '0')), color = FitnessColors.White, fontSize = 44.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                TimerButton(Icons.Default.Remove) { onSecondsChange(seconds - 10) }
                Spacer(modifier = Modifier.width(8.dp))
                TimerButton(Icons.Default.Add) { onSecondsChange(seconds + 10) }
            }
        }
    }
}

@Composable
private fun TimerButton(icon: ImageVector, onClick: () -> Unit) {
    Surface(onClick = onClick, color = FitnessColors.RestTimerAction, shape = CircleShape, modifier = Modifier.size(44.dp)) {
        Box(contentAlignment = Alignment.Center) { Icon(icon, contentDescription = null, tint = FitnessColors.White) }
    }
}
