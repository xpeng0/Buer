package com.cscyxp.bookkeeping.navigation

import kotlinx.serialization.Serializable

@Serializable internal object BookkeepingHome

@Serializable internal object BookkeepingList

@Serializable internal object BookkeepingChart

@Serializable internal object BookkeepingAdd

@Serializable internal data class BookkeepingCategoryChart(val categoryId: Long)
