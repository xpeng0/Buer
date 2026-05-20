package com.cscyxp.finance

import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import com.cscyxp.finance.entity.StockKey
import com.cscyxp.finance.entity.WatchStock
import com.cscyxp.finance.entity.WatchlistEntity
import com.cscyxp.finance.tencent.TencentKLineEntity
import kotlinx.coroutines.delay
import java.io.IOException
import java.math.BigDecimal
import java.util.Locale

fun List<List<String>>.toKLineEntities(): List<TencentKLineEntity> {
    return this.mapNotNull { rawArray ->
        // 防御性编程：确保数组长度足够，防止角标越界崩溃
        if (rawArray.size >= 6) {
            try {
                TencentKLineEntity(
                    date = rawArray[0],
                    open = rawArray[1].toDoubleOrNull() ?: 0.0,
                    close = rawArray[2].toDoubleOrNull() ?: 0.0,
                    high = rawArray[3].toDoubleOrNull() ?: 0.0,
                    low = rawArray[4].toDoubleOrNull() ?: 0.0,
                    volume = rawArray[5].toDoubleOrNull() ?: 0.0
                )
            } catch (e: Exception) {
                null // 解析失败的数据直接丢弃
            }
        } else {
            null
        }
    }
}

fun Double.format2f(): String {
    return String.format(Locale.getDefault(), "%.2f", this)
}

fun String.toDoubleOrZero(): Double {
    return this.toDoubleOrNull() ?: 0.00
}

fun Double.toPercent(): String {
    val percent = BigDecimal(String.format(Locale.US, "%.2f", this))
    return when {
        percent > BigDecimal.ZERO -> "+$percent%"
        percent < BigDecimal.ZERO -> "$percent%"
        else -> "0.00%"
    }
}

fun BigDecimal.toPercent(): String {
    val percent = this
    return when {
        percent > BigDecimal.ZERO -> "+$percent%"
        percent < BigDecimal.ZERO -> "$percent%"
        else -> "0.00%"
    }
}

/**
 * 优雅的重试外挂
 * @param times 重试次数
 * @param initialDelay 初始延迟时间（毫秒）
 * @param maxDelay 最大延迟时间
 * @param factor 延迟倍数（指数退避）
 */
suspend fun <T> retryIO(
    times: Int = 3,
    initialDelay: Long = 1000,
    maxDelay: Long = 5000,
    factor: Double = 2.0,
    block: suspend () -> T
): T {
    var currentDelay = initialDelay
    repeat(times - 1) {
        try {
            return block() // 🌟 成功了直接返回，不再执行后续重试
        } catch (e: IOException) {
            // 只有网络异常才重试，逻辑错误（空指针等）不重试
            Log.w("RetryIO", "请求失败，准备进行第 ${it + 1} 次重试...")
        }
        delay(currentDelay)
        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
    }
    return block() // 最后一次尝试，失败了就直接抛出异常给外层 runCatching
}

/**
 * 将含有 \uXXXX 的字符串解码为正常的中文
 */
fun String.decodeUnicode(): String {
    // 匹配 \u 加上 4 个十六进制字符的正则表达式
    // 注意：在 Kotlin 原生字符串 """ 中，\\u 代表匹配字面上的 \u
    val regex = Regex("""\\u([0-9a-fA-F]{4})""")

    // replace 函数会自动遍历所有匹配到的结果
    return regex.replace(this) { matchResult ->
        // matchResult.groupValues[1] 拿到的就是括号里的 4 位十六进制数，比如 "7ea2"
        val hexString = matchResult.groupValues[1]

        try {
            // 将 16 进制字符串转为 Int，再强转为 Char，最后变回 String
            hexString.toInt(16).toChar().toString()
        } catch (e: Exception) {
            // 万一解析失败，原样返回该片段，不影响其他部分的解析
            matchResult.value
        }
    }
}

fun View.goneOrVisible() {
    if (this.isVisible) {
        this.visibility = View.GONE
    } else {
        this.visibility = View.VISIBLE
    }
}

fun WatchlistEntity.toWatchStock(): WatchStock {
    return WatchStock(
        stockKey = StockKey(symbol, exchange),
        stockName = stockName
    )
}