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
    private val amountPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val defaultBarWidth = 20.dp

    private val availableWidth get() = width - paddingLeft - paddingRight
    private val availableHeight get() = height - paddingBottom - paddingTop
    private val rectF = RectF()
    private var barWidth = 0f
    private var barHeightDensity = 0f
    // 总空隙数
    val gapSize get() = data.size + 1
    // 空隙与宽度比例
    private var gapRatio = 1.5f
    // 字体高度
    private val textHeight get() = textPaint.fontMetrics.descent - textPaint.fontMetrics.ascent
    // 字体与bar空隙
    private val textSpace = 15f
    // 数字高度
    private val amountHeight get() = amountPaint.fontMetrics.descent - amountPaint.fontMetrics.ascent
    // 字体与bar空隙
    private val amountSpace = 25f
    private val barBottom get() = availableHeight - textHeight - textSpace

    init {
        // 初始化逻辑，比如读取自定义属性
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet?) {
        // 这里可以解析 attrs
        context.withStyledAttributes(attrs, R.styleable.BarChartView) {
            barPaint.color = getColor(R.styleable.BarChartView_barColor, Color.BLUE)
            textPaint.color = getColor(R.styleable.BarChartView_textColor, Color.BLACK)
            textPaint.textSize = getDimension(R.styleable.BarChartView_textSize, 30f)
            amountPaint.textSize = getDimension(R.styleable.BarChartView_amountSize, 22f)
        }
    }

    data class BarEntry(
        val label: String,
        val value: Float,
    )

    private var data: List<BarEntry> = let{
        if (isInEditMode) {
            List(6) {
                BarEntry((it + 1).toString() + "月", (it + 1) * 200f)
            }
        } else {
            emptyList()
        }
    }

    fun setData(data: List<BarEntry>) {
        this.data = data
        Log.d(TAG, "setData: $data")
        // 重新计算尺寸bar宽度
        barWidth = availableWidth / (data.size + gapSize * gapRatio)
        // 重新计算单位高度密度
        val barHeightTotal = availableHeight - textHeight - textSpace - amountHeight - amountSpace
        barHeightDensity = barHeightTotal / (data.maxByOrNull { it.value }?.value ?: 1f)
        requestLayout() // 不能仅invalidate重新布局。还要重新测量(wrap_content下，宽高由data size决定)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val viewWidth = (defaultBarWidth * (data.size + gapRatio * (data.size + 1))).toInt()
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
        var barLeft = paddingLeft + barWidth * gapRatio
        data.forEach {
            var barHeight = barHeightDensity * it.value
            if (enableAnimator and !isInEditMode) {
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
            var textWidth = textPaint.measureText(it.label)
            var textOffset = (barWidth - textWidth) / 2f
            canvas.drawText(it.label, barLeft + textOffset, height - paddingBottom - textPaint.fontMetrics.descent, textPaint)
            val amountStr = "%.2f".format(it.value)
            textWidth = amountPaint.measureText(amountStr)
            textOffset = (barWidth - textWidth) / 2f
            canvas.drawText(amountStr, barLeft + textOffset, barTop - amountSpace, amountPaint)
            barLeft += barWidth
            barLeft += barWidth * gapRatio
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // 计算柱子宽度
        barWidth = availableWidth / (data.size + gapSize * gapRatio)
        // 计算柱子高度密度
        val barHeightTotal = availableHeight - textHeight - textSpace - amountHeight - amountSpace
        barHeightDensity = barHeightTotal / (data.maxByOrNull { it.value }?.value ?: 1f)
        log()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (enableAnimator) {
            startAnimator()
        }
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