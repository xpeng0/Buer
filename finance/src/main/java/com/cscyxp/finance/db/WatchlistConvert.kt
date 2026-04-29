package com.cscyxp.finance.db

import androidx.room.TypeConverter
import com.cscyxp.finance.StockExchange
import com.cscyxp.finance.entity.StockKey
import com.google.gson.Gson

class WatchlistConvert {
    private val gson = Gson()

    @TypeConverter
    fun toExchange(json: String): StockExchange {
        return StockExchange.valueOf(json)
    }


    @TypeConverter
    fun fromExchange(exchange: StockExchange): String {
        return exchange.name
    }


}