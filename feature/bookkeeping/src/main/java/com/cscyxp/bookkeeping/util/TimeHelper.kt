package com.cscyxp.bookkeeping.util

import java.time.Clock
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimeHelper @Inject constructor(
    private val clock: Clock,
) {
    fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }

    fun getTodayStartTimeMillis(): Long {
        return LocalDate.now(clock).atStartOfDay(clock.zone).toInstant().toEpochMilli()
    }

    fun getTodayEndTimeMillis(): Long {
        return LocalDate.now(clock).atTime(LocalTime.MAX).atZone(clock.zone).toInstant().toEpochMilli()
    }

    fun getCurrentMonthStartTimeMillis(): Long {
        val now = LocalDate.now(clock)
        return getMonthStartTimeMillis(now.year, now.month.value)
    }

    fun getCurrentMonthEndTimeMillis(): Long {
        val now = LocalDate.now(clock)
        return getMonthEndTimeMillis(now.year, now.month.value)
    }

    fun getMonthStartTimeMillis(year: Int, month: Int): Long {
        return LocalDate.of(year, month, 1)
            .atStartOfDay(clock.zone)
            .toInstant()
            .toEpochMilli()
    }

    fun getMonthEndTimeMillis(year: Int, month: Int): Long {
        return YearMonth.of(year, month)
            .atEndOfMonth()
            .atTime(LocalTime.MAX)
            .atZone(clock.zone)
            .toInstant()
            .toEpochMilli()
    }
}
