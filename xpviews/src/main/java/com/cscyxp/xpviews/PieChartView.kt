package com.cscyxp.xpviews

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.addListener
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.toColorInt
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

private const val TAG = "PieChartView"

/**
 * 中空扇形统计图
 * 最多只展示10个分类。超过10个的末尾分类自动合并为更多分类
 * 颜色采用同一颜色降低透明度的方案
 * 数据不均时采用 平方根缩放 + 最小角度(超额比例补偿)方案
 */
class PieChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr) {

    data class PieEntry(
        val label: String,
        val value: Double
    )

    private var data: List<PieEntry> = emptyList()
    private var optimizedData: List<Double> = data.map { it.value }

    private val piePaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
        textSize = 20f
        color = Color.BLACK
    }

    private val pathPaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
        strokeWidth = 1f
        color = Color.BLACK
    }

    private var innerCirclePath = Path().apply {
        addCircle(width / 2f, height / 2f, radius / 2, Path.Direction.CW)
    }

    // 文字高度
    val textHeight = textPaint.fontMetrics.descent - textPaint.fontMetrics.ascent

    private val rectF = RectF(width / 2f - radius, height / 2f - radius, width / 2f + radius, height / 2f + radius)

    private val horizontalLineWidth = 30f
    private val textSpace = 20f

    init {
        initView(attrs)
        if (isInEditMode) {
            setData((List(11) {
                val num = it +1
                PieEntry("", num * 10.0)
            }.sortedBy { -it.value }))
        }
    }

    private fun initView(attrs: AttributeSet?) {
        context.withStyledAttributes(attrs, R.styleable.PieChartView) {
            piePaint.color = getColor(R.styleable.BarChartView_barColor, Color.BLUE)
        }
    }

    private val radius get() = min(width, height) / 3f
    private val centerX get() = width / 2f
    private val centerY get() = height / 2f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // 300
        val x = width / 2f
        val y = height / 2f
        rectF.set(x - radius, y - radius, x + radius, y + radius)
        innerCirclePath = Path().apply {
            addCircle(width / 2f, height / 2f, radius / 2, Path.Direction.CW)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        if (isInEditMode) {
            minAngleCompensation()
        }
        var startAngle = -90f
        // 裁剪掉内部圆形区域
        canvas.clipOutPath(innerCirclePath)
        Log.i(TAG, "onDraw: data $data")
        data.forEachIndexed { i, entry ->
            // 计算所占角度
            val sum = optimizedData.sumOf { it }
            var sweepAngle = (optimizedData[i] * 360 / sum).toFloat()
            if (i == data.lastIndex) sweepAngle = 270 - startAngle
            Log.i(TAG, "onDraw: angle $sweepAngle")
            piePaint.color = piePaint.color and 0x00ffffff or ((255 - 15 * (i + 1)) shl 24)
            // 留1度空隙
            canvas.drawArc(rectF, startAngle, (sweepAngle - 1f) * progress, true, piePaint)
            // 计算扇形中心角度（弧度）
            val midAngle = startAngle + (sweepAngle - 1) / 2
            val midAngleRad = Math.toRadians(midAngle.toDouble()).toFloat()

            // 引导线起点
            val x1 = centerX + (radius + 10) * cos(midAngleRad)
            val y1 = centerY + (radius + 10) * sin(midAngleRad)

            val isRight = x1 > centerX

            // 转折点
            val x2 = centerX + (radius + 60) * cos(midAngleRad)
            val y2 = centerY + (radius + 60) * sin(midAngleRad)

            // 终点
            val x3 = if (isRight) x2 + horizontalLineWidth else x2 - horizontalLineWidth
            val y3 = y2

            val path = Path().apply {
                moveTo(x1, y1)
                lineTo(x2, y2)
                lineTo(x3, y3)
            }
            val pathMeasure = PathMeasure(path, false)
            path.reset()
            pathMeasure.getSegment(0f, pathMeasure.length * progress, path, true)
            canvas.drawPath(path, pathPaint)

            val text = entry.label + " " + "%.2f%%".format(entry.value / data.sumOf { it.value } * 100 )
            val textWidth = textPaint.measureText(text)
            val textX = if (isRight) {
                x3 + textSpace
            } else {
                x3 - textSpace - textWidth
            }
            // 文字y轴对lineEndY居中
            val textY = y3 + textHeight / 2 - 5

            canvas.drawText(text, textX, textY, textPaint)

            startAngle += sweepAngle
        }
    }

    fun setData(data: List<PieEntry>) {
        // 只保留正值及前9项。后续项合并
        this.data = data.filter { it.value > 0 }.sortedByDescending { it.value }
        if (this.data.size > 10) {
            val other = this.data.drop(9).sumOf { it.value }
            val mutableList = this.data.take(9).toMutableList()
            mutableList.add(PieEntry("更多分类", other))
            this.data = mutableList
        }

        // 合并
        sqrtOptimize()
        // 最小角度补偿
        minAngleCompensation()
        Log.i(TAG, "data: ${this.data.map { it.value }}")
        Log.i(TAG, "optimizedData: $optimizedData")
        startAnimator()
    }


    /**
     * 平方根缩放
     */
    private fun sqrtOptimize() {
        if (data.isEmpty()) return
        optimizedData = data.map { it.value }
        if (optimizedData.max() / optimizedData.min() > 100) {
            optimizedData = optimizedData.map { sqrt(it) }
        }
    }

    val minAngle = 18f
    /**
     * 最小角度补偿(基于超额比例)
     */
    private fun minAngleCompensation() {
        val sum = optimizedData.sum()
        val min = sum * minAngle / 360

        // 需求总量
        val needSum = optimizedData.filter { it < min }.sumOf { min - it }
        // 可补偿总量
        val excessSum = optimizedData.filter { it > min }.sumOf { it - min }
        optimizedData =  optimizedData.map {
            if (it > min) {
                // 按超额比例补偿
                val compensationRation =  (it - min) / excessSum
                it - compensationRation * needSum
            } else {
                min
            }
        }
    }

    private val enableAnimator = true
    private var progress = 1f


    private fun startAnimator() {
        if (enableAnimator) {
            ValueAnimator.ofFloat(0f, 1.0f).apply {
                duration = 800 // 动画时长（毫秒）
                interpolator = DecelerateInterpolator() // 先快后慢
                addUpdateListener {
                    progress = it.animatedValue as Float
                    invalidate() // 重绘
                }
                addListener(
                    onEnd = {
                        invalidate()
                    }
                )
                start()
            }
        }
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (enableAnimator and !isInEditMode) {
            startAnimator()
        }
    }
}