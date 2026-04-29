package com.cscyxp.finance.tecent

import com.cscyxp.finance.StockExchange
import com.cscyxp.finance.entity.KLineEntity
import com.cscyxp.finance.entity.StockEntity
import com.cscyxp.finance.entity.StockKey
import com.cscyxp.finance.tencent.TencentDataSource
import com.cscyxp.finance.tencent.TencentStockDetail
import com.cscyxp.finance.tencent.TencentStockResponse
import com.cscyxp.finance.tencent.TencentStockUtil
import java.time.LocalDate
import kotlin.math.max
import kotlin.math.min
import kotlin.math.nextUp
import kotlin.random.Random

object TencentStockGenerator {
    fun generatorRandomStockResponse(stockKey: StockKey, days: Int): GeneratorStockResponse {
        val tencentSymbol = TencentStockUtil.getTencentSymbol(stockKey)
        val generatorStockDetail = generatorRandomStockDetail(stockKey, days)
        val response = TencentStockResponse(
            code = 1,
            msg = "",
            data = mapOf(Pair(tencentSymbol, generatorStockDetail.tencentStockDetail))
        )
        return GeneratorStockResponse(response, generatorStockDetail.stockEntity)
    }



    fun generatorRandomStockDetail(stockKey: StockKey, days: Int): GeneratorStockDetail {
        val stockName = "啦啦啦啦"
        val currentPrice = 9999.99
        val percent = 1.23
        val today = LocalDate.now()
        val kLines = mutableListOf<List<String>>()
        val kLineEntities = mutableListOf<KLineEntity>()
        val tencentSymbol = TencentStockUtil.getTencentSymbol(stockKey)
        for(i in 0 until days) {
            val stockMax = 10000.0
            val date = today.minusDays(i.toLong()).toString()
            val open = Random.nextDouble(stockMax)
            val close = Random.nextDouble(stockMax)
            val high = Random.nextDouble(max(open, close), stockMax)
            val low = Random.nextDouble(0.0, min(open, close).nextUp())
            val volume = Random.nextDouble()
            val kline = listOf(
                date,
                open.toString(),
                close.toString(),
                high.toString(),
                low.toString(),
                volume.toString()
            )
            val kLineEntity = KLineEntity(date, open, close, high, low, volume)
            kLines.addFirst(kline)
            kLineEntities.addFirst(kLineEntity)
        }
        val qtList = MutableList(40) { "" }
        qtList[TencentDataSource.QT_INDEX_NAME] = stockName
        qtList[TencentDataSource.QT_INDEX_CURRENT_PRICE] = currentPrice.toString()
        qtList[TencentDataSource.QT_INDEX_PERCENT] = percent.toString()
        val qt = mapOf(Pair(tencentSymbol, qtList))
        val tencentStockDetail = TencentStockDetail(
            dayKLine = kLines,
            qt = qt
        )
        return GeneratorStockDetail(
            tencentStockDetail = tencentStockDetail,
            stockEntity = StockEntity(
                name = stockName,
                stockKey = stockKey,
                currentPrice = currentPrice,
                todayPercent = percent.toDouble(),
                kLines = kLineEntities
            )
        )
    }
}
data class GeneratorStockDetail(
    val tencentStockDetail: TencentStockDetail,
    val stockEntity: StockEntity
)

data class GeneratorStockResponse(
    val tencentStockResponse: TencentStockResponse,
    val stockEntity: StockEntity
)