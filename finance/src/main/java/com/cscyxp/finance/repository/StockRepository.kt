package com.cscyxp.finance.repository

import com.cscyxp.finance.SearchRange
import com.cscyxp.finance.StockDatasource
import com.cscyxp.finance.dao.WatchlistDao
import com.cscyxp.finance.entity.StockEntity
import com.cscyxp.finance.entity.StockInfo
import com.cscyxp.finance.entity.StockKey
import com.cscyxp.finance.entity.StockMinute
import com.cscyxp.finance.entity.StockQuotation
import com.cscyxp.finance.entity.WatchStock
import com.cscyxp.finance.entity.WatchlistEntity
import com.cscyxp.finance.hilt.IoDispatcher
import com.cscyxp.finance.toWatchStock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.map

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class StockRepository @Inject constructor(
    private val stockDatasource: StockDatasource,
    private val watchlistDao: WatchlistDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher // 方便测试注入
) {

    suspend fun getStockKLine(stockKey: StockKey, days: Int): Result<StockEntity> {
        return stockDatasource.getStockRecentKLine(stockKey, days)
    }

    suspend fun getWatchlist(): List<WatchStock> {
        return watchlistDao.getWatchlist().map { it.toWatchStock() }
    }

    fun getWatchlistFlow(): Flow<List<WatchStock>> {
        return watchlistDao.getWatchlistFlow().map {
            it.map {
                it.toWatchStock()
            }
        }
    }

    fun getWatchlistKLines(days: Int): Flow<List<KLineResult>> {
        return watchlistDao.getWatchlistFlow().map { watchlist ->
            coroutineScope {
                watchlist.map {
                    val stockKey = StockKey(it.symbol, it.exchange)
                    async {
                        KLineResult(stockKey, getStockKLine(stockKey, days))
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
                                stockKeys = watchlist.map { StockKey(it.symbol, it.exchange) }
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

    suspend fun getStockMinute(stockKey: StockKey): Result<StockMinute> {
        return stockDatasource.getStockMinute(stockKey)
    }

    suspend fun addStockToWatchlist(stockKey: StockKey, stockName: String) {
        watchlistDao.insertOne(WatchlistEntity(
            symbol = stockKey.symbol,
            exchange = stockKey.exchange,
            stockName = stockName
        ))
        updateCache(listOf(stockKey))
    }

    suspend fun removeStockFromWatchlist(stockKey: StockKey, stockName: String) {
        watchlistDao.deleteOne(WatchlistEntity(
            symbol = stockKey.symbol,
            exchange = stockKey.exchange,
            stockName = stockName
        ))
    }

    suspend fun searchStockInfo(input: String, range: SearchRange = SearchRange.ALL): Result<List<StockInfo>> {
        return stockDatasource.searchStockInfo(input, range)
    }

    private val _watchlistCacheMap = MutableStateFlow<Map<StockKey, WatchlistCache>>(emptyMap())
    val watchlistCacheMap: StateFlow<Map<StockKey, WatchlistCache>> = _watchlistCacheMap

    suspend fun updateCacheWithQt(keys: List<StockKey>) {
        val cacheMap = _watchlistCacheMap.value
        val reCacheKeys = keys.filter {
            val cache = cacheMap[it]
            cache == null || System.currentTimeMillis() - cache.updateTimeMillis > 10 * 60 * 1000
        }

        // 并发查询
        val (minutes, qtMap) = coroutineScope {
            val minutesDef = reCacheKeys.map {
                    async {
                        getStockMinute(it).getOrNull()
                    }
                }
            val qtDef = async {
                getStockQuotation(keys).getOrNull()
            }
            // 同步等待
            minutesDef.awaitAll().filterNotNull() to qtDef.await()
        }

        // 统一更新
        _watchlistCacheMap.update { oldCache ->
            val newCacheMap = oldCache.toMutableMap()
            // 用全量分时数据重建缓存
            minutes.forEach {
                val key = it.stockKey
                newCacheMap[key] = WatchlistCache(
                    stockName = it.stockName,
                    stockKey = it.stockKey,
                    currentPrice = it.currentPrice,
                    preClosePrice = it.preClosePrice,
                    todayPercent = it.todayPercent,
                    high = it.high,
                    low = it.low,
                    lastTime = it.time,
                    minutes = it.minutes,
                    updateTimeMillis = System.currentTimeMillis()
                )
            }

            // 用qt增量更新缓存
            qtMap?.forEach { (key, qt) ->
                val cache = newCacheMap[key]
                cache?.let { old ->
                    val newMinutes = old.minutes.toMutableList()
                    if (qt.time == old.lastTime && newMinutes.isNotEmpty()) {
                        newMinutes.removeAt(newMinutes.size - 1)
                    }
                    // 追加时间点数据
                    if (newMinutes.size < 250) { // 防御性编程 防爆
                        newMinutes.add(qt.close)
                    }
                    // 用qt增量更新缓存
                    val newCache  = old.copy(
                        currentPrice = qt.close,
                        todayPercent = qt.percent,
                        high = qt.high,
                        low = qt.low,
                        lastTime = qt.time,
                        minutes = newMinutes,
                        updateTimeMillis = System.currentTimeMillis()
                    )
                    newCacheMap[key] = newCache
                }
            }
            newCacheMap
        }
    }

    suspend fun updateCache(keys: List<StockKey>) {
        val cacheMap = _watchlistCacheMap.value
        val reCacheKeys = keys.filter {
            val cache = cacheMap[it]
            cache == null || System.currentTimeMillis() - cache.updateTimeMillis > 10 * 60 * 1000
        }

        // 并发查询
        val minutes = coroutineScope {
            val minutesDef = reCacheKeys.map {
                async {
                    getStockMinute(it).getOrNull()
                }
            }
            // 同步等待
            minutesDef.awaitAll().filterNotNull()
        }
        // 更新
        _watchlistCacheMap.update { oldCache ->
            val newCacheMap = oldCache.toMutableMap()
            // 用全量分时数据重建缓存
            minutes.forEach {
                val key = it.stockKey
                newCacheMap[key] = WatchlistCache(
                    stockName = it.stockName,
                    stockKey = it.stockKey,
                    currentPrice = it.currentPrice,
                    preClosePrice = it.preClosePrice,
                    todayPercent = it.todayPercent,
                    high = it.high,
                    low = it.low,
                    lastTime = it.time,
                    minutes = it.minutes,
                    updateTimeMillis = System.currentTimeMillis()
                )
            }

            newCacheMap
        }
    }




    data class WatchlistCache(
        val stockName: String,
        val stockKey: StockKey,
        val currentPrice: Double,
        val preClosePrice: Double,
        val todayPercent: Double,
        val high: Double,
        val low: Double,
        val minutes: List<Double>,
        val lastTime: Long,
        val updateTimeMillis: Long
    )
}

data class KLineResult(
    val stockKey: StockKey,
    val result: Result<StockEntity>
)

