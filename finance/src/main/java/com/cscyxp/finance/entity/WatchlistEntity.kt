package com.cscyxp.finance.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.cscyxp.finance.db.WatchlistConvert

@Entity(
    tableName = "watchlist",
    primaryKeys = ["symbol", "exchange"])
@TypeConverters(value = [WatchlistConvert::class])
data class WatchlistEntity(
    @Embedded
    val stockKey: StockKey
)
