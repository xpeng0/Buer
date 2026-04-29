package com.cscyxp.finance.repository

import com.cscyxp.finance.SearchRange
import com.cscyxp.finance.StockDatasource
import com.cscyxp.finance.dao.WatchlistDao
import com.cscyxp.finance.entity.StockEntity
import com.cscyxp.finance.entity.StockInfo
import com.cscyxp.finance.entity.StockKey
import com.cscyxp.finance.entity.StockQuotation
import com.cscyxp.finance.entity.WatchlistEntity
import com.cscyxp.finance.hilt.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlin.collections.map

@OptIn(ExperimentalCoroutinesApi::class)
class StockRepository @Inject constructor(
    private val stockDatasource: StockDatasource,
    private val watchlistDao: WatchlistDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher // 方便测试注入
) {
    // 缓存
    private val watchlistCache = ConcurrentHashMap<CacheKey, StockEntity>()

    suspend fun getStockKLine(stockKey: StockKey, days: Int): Result<StockEntity> {
        return stockDatasource.getStockRecentKLine(stockKey, days)
    }

    suspend fun getWatchlist(): List<WatchlistEntity> {
        return watchlistDao.getWatchlist()
    }

    fun getWatchlistFlow(): Flow<List<WatchlistEntity>> {
        return watchlistDao.getWatchlistFlow()
    }

    fun getWatchlistKLines(days: Int): Flow<List<KLineResult>> {
        return watchlistDao.getWatchlistFlow().map { watchlist ->
            coroutineScope {
                watchlist.map {
                    async {
                        val cacheKey = CacheKey(
                            stockKey = it.stockKey,
                            days = days
                        )
                        KLineResult(it.stockKey, getStockKLine(it.stockKey, days))
                    }
                }
            }.awaitAll()
        }
    }

    fun getWatchlistStockQuotation(): Flow<Result<Map<StockKey, StockQuotation>>> {
        return watchlistDao.getWatchlistFlow().flatMapLatest { watchlist ->
            if (watchlist.isEmpty()) {
                flowOf(Result.success(emptyMap()))
            } else {
                flow {
                    while (true) {
                        emit(
                            stockDatasource.getStockQuotation(
                                stockKeys = watchlist.map { it.stockKey }
                            )
                        )
                        delay(5000)
                    }

                }.flowOn(ioDispatcher)

            }
        }
    }

    suspend fun getStockQuotation(stockKeys: List<StockKey>): Result<Map<StockKey, StockQuotation>> {
        return stockDatasource.getStockQuotation(stockKeys)
    }

    suspend fun addStockToWatchlist(stockKey: StockKey) {
        watchlistDao.insertOne(WatchlistEntity(
            stockKey = stockKey
        ))
    }

    suspend fun removeStockFromWatchlist(stockKey: StockKey) {
        watchlistDao.deleteOne(WatchlistEntity(
            stockKey = stockKey
        ))
    }

    suspend fun searchStockInfo(input: String, range: SearchRange = SearchRange.ALL): Result<List<StockInfo>> {
        return stockDatasource.searchStockInfo(input, range)
    }
}

data class CacheKey(
    val stockKey: StockKey,
    val days: Int
)

data class KLineResult(
    val stockKey: StockKey,
    val result: Result<StockEntity>
)

