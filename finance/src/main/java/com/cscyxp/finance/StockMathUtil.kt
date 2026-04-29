package com.cscyxp.finance

import java.math.BigDecimal
import java.math.RoundingMode

object StockMathUtil {
    fun calculateChangePercent(currentPrice: Double, preventPrice: Double): String {
        val current = currentPrice.toBigDecimal()
        val prevent = preventPrice.toBigDecimal()
        val subtract = (current - prevent) * BigDecimal("100")
        val percent = subtract.divide(current, 2, RoundingMode.HALF_UP)
        return percent.toPercent()
    }
}