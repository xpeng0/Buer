package com.cscyxp.xpviews

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.max
import kotlin.math.min
import androidx.core.graphics.toColorInt

class TrendLineView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f // 线条粗细
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val path = Path()
    private var dataPoints: List<Float> = emptyList()

    private var startColor: Int = "#B9F6CA".toColorInt() // 浅绿
    private var endColor: Int = "#4CAF50".toColorInt()   // 深绿

    init {
        if (isInEditMode) {
            setData(listOf(
                2.1f, 3.2f, 4.3f, 6.0f, 5.5f, 4.5f, 5.1f
            ))
        }
    }

    fun setData(points: List<Float>) {
        this.dataPoints = points
        // 触发重新测量和绘制
        updateGradient()
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateGradient()
    }

    private fun updateGradient() {
        if (width > 0) {
            // 设置从左到右的线性渐变色
            linePaint.shader = LinearGradient(
                0f, 0f, width.toFloat(), 0f,
                startColor, endColor, Shader.TileMode.CLAMP
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (dataPoints.isEmpty() || dataPoints.size < 2) return

        path.reset()

        val width = width.toFloat()
        val height = height.toFloat()
        
        // 上下留出 padding，防止线条粗细导致边缘被裁剪
        val padding = linePaint.strokeWidth 
        val drawHeight = height - padding * 2
        val drawWidth = width

        // 找出数据的最大值和最小值，用于计算 Y 轴缩放比例
        var minData = dataPoints.minOrNull() ?: 0f
        var maxData = dataPoints.maxOrNull() ?: 0f
        
        // 如果数据全是一样的平线，稍微做点偏移防止除以 0
        if (minData == maxData) {
            minData -= 1f
            maxData += 1f
        }

        val stepX = drawWidth / (dataPoints.size - 1)
        val scaleY = drawHeight / (maxData - minData)

        // 起点
        var prevX = 0f
        var prevY = height - padding - (dataPoints[0] - minData) * scaleY
        path.moveTo(prevX, prevY)

        // 🌟 核心魔法：使用三次贝塞尔曲线 (Cubic Bezier) 连接每一个点，让线条变得丝滑
        for (i in 1 until dataPoints.size) {
            val currentX = i * stepX
            val currentY = height - padding - (dataPoints[i] - minData) * scaleY

            // 计算控制点 (取两点的 X 轴中间值)，这样曲线就会平滑过渡
            val controlX = (prevX + currentX) / 2
            
            path.cubicTo(
                controlX, prevY,     // 控制点 1
                controlX, currentY,  // 控制点 2
                currentX, currentY   // 终点
            )

            prevX = currentX
            prevY = currentY
        }

        canvas.drawPath(path, linePaint)
    }
}