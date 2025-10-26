package com.cscyxp.xpviews

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.addListener
import kotlin.math.min
import androidx.core.content.withStyledAttributes

private const val TAG = "BarChartView"
class BarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr) {

    private val barPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val defaultBarWidth = 20.dp

    private var availableLeft = left - paddingLeft
    private var availableRight = right - paddingRight
    private var availableTop = left - paddingTop
    private var availableBottom = right - paddingBottom
    private val availableWidth get() = availableRight - availableLeft
    private val availableHeight get() = availableBottom - availableTop
    private val rectF = RectF()
    private var barWidth = 0f
    private var barHeightDensity = 0f
    private var barBottom = bottom - height * 0.95f
    // 空隙与宽度比例
    private var gapRatio = 1f

    init {
        // 初始化逻辑，比如读取自定义属性
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet?) {
        // 这里可以解析 attrs
        context.withStyledAttributes(attrs, R.styleable.BarChartView) {
            barPaint.color = getColor(R.styleable.BarChartView_barColor, Color.BLUE)
            textPaint.color = getColor(R.styleable.BarChartView_textColor, Color.BLACK)
        }
    }

    data class BarEntry(
        val label: String,
        val value: Float,
    )

    private var data: List<BarEntry> = let{
        if (isInEditMode) {
            List(5) {
                BarEntry("aa", (it + 1) * 200f)
            }
        } else {
            emptyList()
        }
    }

    fun setData(data: List<BarEntry>) {
        this.data = data
        invalidate() // 重新绘制
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val viewWidth = (defaultBarWidth * (data.size + gapRatio * (data.size - 1))).toInt()
        val defaultWidth =  viewWidth + paddingLeft + paddingRight
        val measuredWidth = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> min(widthSize, defaultWidth)
            MeasureSpec.UNSPECIFIED -> defaultWidth
            else -> defaultWidth
        }

        val defaultHeight = (viewWidth * 0.8 + paddingTop + paddingBottom).toInt()
        val measuredHeight = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> min(heightSize, defaultHeight)
            MeasureSpec.UNSPECIFIED -> defaultHeight
            else -> defaultHeight
        }

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 根据 data 绘制柱状图
        var barLeft = availableLeft.toFloat()
        data.forEach {
            var barHeight = barHeightDensity * it.value
            if (enableAnimator) {
                barHeight *= progress
            }
            val barTop = barBottom - barHeight
            rectF.set(
                barLeft,
                barTop,
                barLeft + barWidth,
                barBottom
            )
            canvas.drawRect(rectF, barPaint)
            textPaint.textSize = availableHeight * 0.05f
            var textWidth = textPaint.measureText(it.label)
            var textOffset = (barWidth - textWidth) / 2f
            canvas.drawText(it.label, barLeft + textOffset, availableBottom.toFloat(), textPaint)
            textPaint.textSize = availableHeight * 0.03f
            textWidth = textPaint.measureText(it.value.toString())
            textOffset = (barWidth - textWidth) / 2f
            canvas.drawText(it.value.toString(), barLeft + textOffset, barTop - availableHeight * 0.02f, textPaint)
            barLeft += barWidth
            barLeft += barWidth * gapRatio
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        availableLeft = left + paddingLeft
        availableRight = right - paddingRight
        availableTop = top + paddingTop
        availableBottom = bottom - paddingBottom
        // 总空隙数
        val gapSize = data.size - 1
        // 计算柱子宽度
        barWidth = availableWidth / (data.size + gapSize * gapRatio)
        // 计算柱子高度密度
        val barHeightTotal = availableHeight * 0.9f
        barHeightDensity = barHeightTotal / (data.maxByOrNull { it.value }?.value ?: 1f)
        barBottom = availableBottom * 0.95f
        log()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimator()
    }

    fun log(){
        Log.i(TAG, toString())
    }

    override fun toString(): String {
        return "BarChartView(paint=$barPaint, availableWidth=$availableWidth, availableHeight=$availableHeight, rectF=$rectF, barWidth=$barWidth, barHeightDensity=$barHeightDensity, barBottom=$barBottom, gapRatio=$gapRatio, data=$data)"
    }


    private var enableAnimator = true
    private var progress = 0f

    private fun startAnimator() {
        if (enableAnimator) {
            ValueAnimator.ofFloat(0f, 1.0f).apply {
                duration = 1000 // 动画时长（毫秒）
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


}