package com.cscyxp.finance.repository

import app.cash.turbine.test
import com.cscyxp.finance.StockDatasource
import com.cscyxp.finance.StockExchange
import com.cscyxp.finance.dao.WatchlistDao
import com.cscyxp.finance.entity.StockKey
import com.cscyxp.finance.entity.StockQuotation
import com.cscyxp.finance.entity.WatchlistEntity
import com.cscyxp.finance.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StockRepositoryTest {
    // 1. 准备 Mock 对象
    private val mockDao = mockk<WatchlistDao>(relaxed = true)
    private val mockDatasource = mockk<StockDatasource>(relaxed = true)

    // 2. 🌟 准备专门用于测试的 Dispatcher，它允许我们操纵虚拟时间！
    private val testDispatcher = UnconfinedTestDispatcher()

    fun createRepository(): StockRepository {
        return StockRepository(mockDatasource, mockDao, testDispatcher)
    }
    @Test
    fun `test getWatchlistStockQuotation - should map when watchlist update`() = runTest(testDispatcher) {
        val repository = createRepository()
        val watchlistFlow = MutableStateFlow<List<WatchlistEntity>>(emptyList())
        val key = StockKey("11111", StockExchange.SHANG_HAI)
        val name = "aaa"
        val watchlist = listOf(WatchlistEntity(key, name))
        every { mockDao.getWatchlistFlow() } returns watchlistFlow
        val result = Result.success(mapOf(key to StockQuotation(1.0, 1.0, 2.0, 2.0, 1.0, 1111)))
        coEvery { mockDatasource.getStockQuotation(any()) } returns result
        repository.getWatchlistStockQuotation().test {
            awaitItem().shouldBe(Result.success(emptyMap())) // 空列表对应空map
            watchlistFlow.emit(watchlist)
            awaitItem().shouldBe(result)
        }
    }

    @Test
    fun `test getWatchlistStockQuotation - should emit quotation per 5s`() = runTest(testDispatcher) {
        val repository = createRepository()
        val key = StockKey("11111", StockExchange.SHANG_HAI)
        val name = "aaa"
        val watchlist = listOf(WatchlistEntity(key, name))
        val watchlistFlow = MutableStateFlow<List<WatchlistEntity>>(watchlist)
        every { mockDao.getWatchlistFlow() } returns watchlistFlow
        val result1 = Result.success(mapOf(key to StockQuotation(1.0, 1.0, 1.0, 1.0, 1.0, 1111)))
        val result2 = Result.success(mapOf(key to StockQuotation(2.0, 2.0, 2.0, 2.0, 2.0, 2222)))
        coEvery {
            mockDatasource.getStockQuotation(any())
        } returnsMany listOf(result1, result2)
        repository.getWatchlistStockQuotation().test {
            awaitItem().shouldBe(result1) // 初始值
            advanceTimeBy(5000) // 没有这个也能通过 turbine自动快进？
            awaitItem().shouldBe(result2)
        }
    }



}