package com.cscyxp.fitness.template.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cscyxp.fitness.R
import com.cscyxp.fitness.ui.theme.FitnessColors

@Composable
internal fun TemplateNameField(value: String, onValueChange: (String) -> Unit) {
    Text(stringResource(R.string.fitness_template_name), color = FitnessColors.TextSecondary, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(stringResource(R.string.fitness_template_name_hint), color = FitnessColors.TextMuted) },
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = FitnessColors.SurfaceMuted,
            unfocusedContainerColor = FitnessColors.SurfaceMuted,
            focusedBorderColor = FitnessColors.Primary,
            unfocusedBorderColor = FitnessColors.Transparent,
        ),
        singleLine = true,
    )
}

@Composable
internal fun TemplateExerciseEditor() {
    Text(stringResource(R.string.fitness_template_exercises, 1), color = FitnessColors.TextSecondary, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    Spacer(modifier = Modifier.height(10.dp))
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(FitnessColors.SurfaceMuted).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.fitness_exercise_bench_press), color = FitnessColors.TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Surface(onClick = {}, modifier = Modifier.size(34.dp), shape = CircleShape, color = FitnessColors.DangerSoft) {
                Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.DeleteOutline, contentDescription = stringResource(R.string.fitness_delete_exercise), tint = FitnessColors.Danger, modifier = Modifier.size(19.dp)) }
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NumberStepper(R.string.fitness_sets, 3, Modifier.weight(1f))
            NumberStepper(R.string.fitness_reps, 10, Modifier.weight(1f))
            NumberStepper(R.string.fitness_weight_kg, 0, Modifier.weight(1f))
        }
    }
}

@Composable
private fun NumberStepper(labelRes: Int, initialValue: Int, modifier: Modifier = Modifier) {
    var value by remember { mutableIntStateOf(initialValue) }
    Column(modifier) {
        Text(stringResource(labelRes), color = FitnessColors.TextSecondary, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(FitnessColors.White).padding(horizontal = 4.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            IconButton(onClick = { if (value > 0) value-- }, modifier = Modifier.size(26.dp)) { Icon(Icons.Default.Remove, contentDescription = stringResource(R.string.fitness_decrease), modifier = Modifier.size(16.dp)) }
            Text(value.toString(), color = FitnessColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            IconButton(onClick = { value++ }, modifier = Modifier.size(26.dp)) { Icon(Icons.Default.Add, contentDescription = stringResource(R.string.fitness_increase), modifier = Modifier.size(16.dp)) }
        }
    }
}
