package com.cscyxp.finance.tencent

import com.cscyxp.finance.SearchRange
import com.cscyxp.finance.StockDatasource
import com.cscyxp.finance.StockExchange
import com.cscyxp.finance.entity.KLineEntity
import com.cscyxp.finance.entity.StockEntity
import com.cscyxp.finance.entity.StockInfo
import com.cscyxp.finance.entity.StockKey
import com.cscyxp.finance.entity.StockMinute
import com.cscyxp.finance.entity.StockQuotation
import com.cscyxp.finance.retryIO
import com.cscyxp.finance.toKLineEntities
import javax.inject.Inject

private const val TAG = "TencentDataSource"
class TencentDataSource @Inject constructor(
    private val tencentStockApi: TencentStockApi
): StockDatasource {

    companion object {
        const val QT_INDEX_NAME = 1
        const val QT_INDEX_CURRENT_PRICE = 3
        const val QT_INDEX_YESTERDAY_CLOSE = 4
        const val QT_INDEX_OPEN = 5
        const val QT_INDEX_TIME = 30
        const val QT_INDEX_PERCENT = 32
        const val QT_INDEX_HIGH = 33
        const val QT_INDEX_LOW = 34
    }

    override suspend fun getStockRecentKLine(
        stockKey: StockKey,
        days: Int
    ): Result<StockEntity> {
        return runCatching {
            retryIO {
                val tencentSymbol = TencentStockUtil.getTencentSymbol(stockKey)
                val param = "$tencentSymbol,day,,,$days,qfq"

                val kLineData = tencentStockApi.getKLineData(param)
                val data = kLineData.data?.get(tencentSymbol)
                val tencentKLineEntities = data?.getKLineData()?.toKLineEntities() ?: emptyList()
                val info = data?.qt?.get(tencentSymbol)
                StockEntity(
                    name = info?.get(QT_INDEX_NAME) ?: "",
                    stockKey = stockKey,
                    currentPrice = info?.getOrNull(QT_INDEX_CURRENT_PRICE)?.toDoubleOrNull() ?: 0.00,
                    todayPercent = info?.getOrNull(QT_INDEX_PERCENT)?.toDoubleOrNull() ?: 0.00,
                    kLines = tencentKLineEntities.map { toKLineEntity(it) }
                )
            }
        }
    }

    override suspend fun getStockQuotation(stockKeys: List<StockKey>): Result<Map<StockKey, StockQuotation>> {
        return runCatching {
            retryIO {
                val responseBody = tencentStockApi.getQt(TencentStockUtil.getQtUrl(stockKeys))
                // 解析
                TencentStockUtil.parseQtResponse(responseBody.string())
            }
        }
    }

    override suspend fun searchStockInfo(input: String, range: SearchRange): Result<List<StockInfo>> {
        return runCatching {
            retryIO {
                val responseBody = tencentStockApi.fuzzySearchStockInfo(TencentStockUtil.getFuzzySearchUrl(input, range))
                // 解析
                TencentStockUtil.parseFuzzySearchResponse(responseBody.string())
            }
        }
    }

    override suspend fun getStockMinute(stockKey: StockKey): Result<StockMinute> {
        return runCatching {
            retryIO {
                val jsonObject = tencentStockApi.getStockMinute(TencentStockUtil.getTencentSymbol(stockKey))
                TencentStockUtil.parseMinuteResponse(stockKey, jsonObject)
            }
        }
    }

    private fun toKLineEntity(tencentKLineEntity: TencentKLineEntity): KLineEntity {
        return KLineEntity(
            date = tencentKLineEntity.date,
            open = tencentKLineEntity.open,
            close = tencentKLineEntity.close,
            high = tencentKLineEntity.high,
            low = tencentKLineEntity.low,
            volume = tencentKLineEntity.volume
        )
    }

}