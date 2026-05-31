package com.cscyxp.xpviews.composable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun BarChart(
    entries: List<Pair<String, Double>>,
    modifier: Modifier = Modifier,
    barColor: Color = Color(0xFF4CAF50)
) {
    Canvas(modifier = modifier.fillMaxSize().padding(4.dp)) {
        if (entries.isEmpty()) return@Canvas

        val maxValue = entries.maxOf { it.second }
        val barWidth = size.width / (entries.size * 2f + 1f)
        val gap = barWidth

        entries.forEachIndexed { index, (_, value) ->
            val barHeight = (value / maxValue * size.height * 0.9f).toFloat()
            val x = gap + index * (barWidth + gap)
            val y = size.height - barHeight
            drawRect(
                color = barColor,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight)
            )
        }
    }
}

@Composable
fun PieChart(
    entries: List<Pair<String, Double>>,
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(
        Color(0xFF4CAF50), Color(0xFFFF9800), Color(0xFF2196F3),
        Color(0xFFF44336), Color(0xFF9C27B0), Color(0xFF00BCD4),
        Color(0xFFFFEB3B), Color(0xFF795548)
    )
) {
    Canvas(modifier = modifier.fillMaxSize().padding(8.dp)) {
        if (entries.isEmpty()) return@Canvas

        val total = entries.sumOf { it.second }
        if (total == 0.0) return@Canvas

        val radius = minOf(size.width, size.height) / 2f
        val center = Offset(size.width / 2f, size.height / 2f)
        var startAngle = -90f

        entries.forEachIndexed { index, (_, value) ->
            val sweepAngle = (value / total * 360).toFloat()
            val color = colors[index % colors.size]
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )
            startAngle += sweepAngle
        }
    }
}

@Preview
@Composable
fun BarChartPreview() {
    BarChart(
        entries = listOf("1月" to 1000.0, "2月" to 2000.0, "3月" to 1500.0),
        modifier = Modifier.size(200.dp, 120.dp)
    )
}

@Preview
@Composable
fun PieChartPreview() {
    PieChart(
        entries = listOf("食物" to 300.0, "交通" to 200.0, "娱乐" to 150.0),
        modifier = Modifier.size(200.dp)
    )
}
