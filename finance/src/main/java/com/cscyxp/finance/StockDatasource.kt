package com.cscyxp.finance

import com.cscyxp.finance.entity.StockEntity
import com.cscyxp.finance.entity.StockInfo
import com.cscyxp.finance.entity.StockKey
import com.cscyxp.finance.entity.StockMinute
import com.cscyxp.finance.entity.StockQuotation

interface StockDatasource {
    suspend fun getStockRecentKLine(stockKey: StockKey, days: Int): Result<StockEntity>

    suspend fun getStockQuotation(stockKeys: List<StockKey>): Result<Map<StockKey, StockQuotation>>

    suspend fun searchStockInfo(input: String, range: SearchRange): Result<List<StockInfo>>

    suspend fun getStockMinute(stockKey: StockKey): Result<StockMinute>
}