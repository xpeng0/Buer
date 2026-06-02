package com.cscyxp.finance.navigation

import kotlinx.serialization.Serializable

/**
 * Finance 模块内部路由定义。
 * 标记为 internal，外部模块不可直接引用。
 */
@Serializable internal object FinanceWatchlist

@Serializable internal data class FinanceDetail(val symbol: String, val exchange: String)

@Serializable internal object FinanceSearch
