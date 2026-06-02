package com.cscyxp.fitness.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cscyxp.fitness.ui.theme.FitnessColors

@Composable
internal fun FitnessPageHeader(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 0.5.dp, color = FitnessColors.Border)
            .padding(start = 14.dp, top = 18.dp, end = 22.dp, bottom = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = FitnessColors.TextPrimary,
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, color = FitnessColors.TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun FitnessGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(14.dp),
        color = FitnessColors.Transparent,
        shadowElevation = if (enabled) 6.dp else 0.dp,
    ) {
        Box(
            modifier = Modifier.background(
                Brush.horizontalGradient(
                    if (enabled) listOf(FitnessColors.Primary, FitnessColors.PrimaryDark)
                    else listOf(FitnessColors.PrimaryDisabled, FitnessColors.PrimaryDisabled)
                )
            ),
            contentAlignment = Alignment.Center,
        ) {
            Text(text, color = FitnessColors.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
internal fun FitnessSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp),
        shape = RoundedCornerShape(12.dp),
        color = FitnessColors.White,
    ) {
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Add, contentDescription = null, tint = FitnessColors.TextSecondary, modifier = Modifier.size(19.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text, color = FitnessColors.TextSecondary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
    }
}
