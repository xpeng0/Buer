package com.cscyxp.finance.watchlist

import androidx.lifecycle.viewModelScope
import com.cscyxp.finance.StockExchange
import com.cscyxp.finance.entity.StockKey
import com.cscyxp.finance.repository.StockRepository
import com.cscyxp.finance.watchlist.vm.WatchlistViewModel
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WatchlistViewmodelTest {
    // 1. 创建一个虚拟的测试调度器
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var stockRepository: StockRepository

    @BeforeEach
    fun setup() {
        // 2. 将主线程替换为我们的虚拟调度器
        Dispatchers.setMain(testDispatcher)
        stockRepository = mockk(relaxed = true) // relaxed=true 允许我们不强制 mock 所有方法的返回值
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain() // 测试结束恢复原状
    }

    fun createViewModel(): WatchlistViewModel {
        return WatchlistViewModel(stockRepository)
    }
    @Test
    fun `test visibleFreqPolling - debounce 200ms and poll per 5s`() = runTest(testDispatcher) {
        val mockKeys = setOf(
            StockKey("000001", StockExchange.SHANG_HAI),
            StockKey("000002", StockExchange.SHANG_HAI)
        )
        val vm = createViewModel()
        try {
            runCurrent() // 执行监听
            vm.updateVisibleWatchStock(mockKeys)
            coVerify(exactly = 0) { stockRepository.updateCacheWithQt(mockKeys.toList()) }
            advanceTimeBy(201) // 验证防抖
            runCurrent()
            coVerify(exactly = 1) { stockRepository.updateCacheWithQt(mockKeys.toList()) }
            advanceTimeBy(1 * 1000) // 验证5s循环
            coVerify(exactly = 1) { stockRepository.updateCacheWithQt(mockKeys.toList()) }
            advanceTimeBy(4 * 1000 + 1)
            coVerify(exactly = 2) { stockRepository.updateCacheWithQt(mockKeys.toList()) }

        } finally {
            vm.viewModelScope.cancel()  // 取消死循环心跳
        }

    }

    @Test
    fun `test nearbyFreqPolling - debounce 200ms and poll per 2min`() = runTest(testDispatcher) {
        val mockKeys = setOf(
            StockKey("000001", StockExchange.SHANG_HAI),
            StockKey("000002", StockExchange.SHANG_HAI)
        )
        val vm = createViewModel()
        try {
            runCurrent() // 执行监听
            vm.updateNearbyWatchStock(mockKeys)
            coVerify(exactly = 0) { stockRepository.updateCacheWithQt(mockKeys.toList()) }
            advanceTimeBy(201) // 验证防抖
            runCurrent()
            coVerify(exactly = 1) { stockRepository.updateCacheWithQt(mockKeys.toList()) }
            advanceTimeBy(1 * 60 * 1000) // 验证2min循环
            coVerify(exactly = 1) { stockRepository.updateCacheWithQt(mockKeys.toList()) }
            advanceTimeBy(1 * 60 * 1000 + 1)
            coVerify(exactly = 2) { stockRepository.updateCacheWithQt(mockKeys.toList()) }

        } finally {
            vm.viewModelScope.cancel()  // 取消死循环心跳
        }

    }


}