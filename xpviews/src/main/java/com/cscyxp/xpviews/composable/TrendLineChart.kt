package com.cscyxp.xpviews.composable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TrendLineChart(
    modifier: Modifier,
    minutes: List<Float>,
    totalPoints: Int,
    touchIndex: Int? = null,
    onPointSelected: ((Int?) -> Unit)? = null,
) {

    Canvas(
        modifier = modifier
            .padding(4.dp)
            .pointerInput(minutes.size, totalPoints) {
                if (minutes.size < 2) return@pointerInput
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val stepX = size.width.toFloat() / (totalPoints - 1)

                    var currentIndex = ((down.position.x / stepX) + 0.5f).toInt()
                        .coerceIn(0, minutes.size - 1)
                    onPointSelected?.invoke(currentIndex)

                    do {
                        val event = awaitPointerEvent()
                        val pointer = event.changes.find { it.id == down.id }
                        if (pointer == null || !pointer.pressed) break
                        val newIndex = ((pointer.position.x / stepX) + 0.5f).toInt()
                            .coerceIn(0, minutes.size - 1)
                        if (newIndex != currentIndex) {
                            currentIndex = newIndex
                            onPointSelected?.invoke(currentIndex)
                        }
                        pointer.consume()
                    } while (true)
                    onPointSelected?.invoke(null)
                }
            }
    ) {
        if (minutes.size < 2) return@Canvas

        val canvasWidth = size.width
        val canvasHeight = size.height

        // 1. 核心数学计算
        val minData = minutes.minOrNull() ?: 0f
        val maxData = minutes.maxOrNull() ?: 0f
        val delta = if (maxData == minData) 1f else (maxData - minData)

        // 当数据不够时, 实现右侧留空不铺满
        val stepX = canvasWidth / (totalPoints - 1)
        val scaleY = canvasHeight / delta

        // 2. 勾勒贝赛尔曲线 Path
        val linePath = Path()
        var prevX = 0f
        var prevY = canvasHeight - (minutes[0] - minData) * scaleY
        linePath.moveTo(prevX, prevY)

        for (i in 1 until minutes.size) {
            val currentX = i * stepX
            val currentY = canvasHeight - (minutes[i] - minData) * scaleY
            val controlX = (prevX + currentX) / 2
            linePath.cubicTo(controlX, prevY, controlX, currentY, currentX, currentY)
            prevX = currentX
            prevY = currentY
        }

        // 3. 绘制折线图渐变色
        drawPath(
            path = linePath,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFB9F6CA), Color(0xFF4CAF50)),
                start = Offset(0f, 0f),
                end = Offset(canvasWidth, 0f)
            ),
            style = Stroke(width = 6f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // 4. 绘制触摸指示虚线
        touchIndex?.let { idx ->
            val lineX = idx * stepX
            drawLine(
                color = Color.Gray,
                start = Offset(lineX, 0f),
                end = Offset(lineX, canvasHeight),
                strokeWidth = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )
        }
    }
}

@Composable
@Preview
fun NormalTrendLineChart() {
    TrendLineChart(
        modifier = Modifier.size(200.dp, 100.dp),
        minutes= listOf(2.1f, 3.2f, 4.3f, 6.0f, 5.5f, 4.5f, 5.1f),
        totalPoints= 7,
    )
}
