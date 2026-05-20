package com.cscyxp.finance.entity

import android.os.Parcelable
import com.cscyxp.finance.StockExchange
import kotlinx.parcelize.Parcelize

@Parcelize
data class StockKey(
    val symbol: String,
    val exchange: StockExchange,
): Parcelable
