package com.cscyxp.xpviews

import android.content.res.Resources
import kotlin.math.ln

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Float.dp: Float
    get() = (this * Resources.getSystem().displayMetrics.density)

// 扩展函数
fun Double.log(base: Double): Double {
    return ln(this) / ln(base)
}
