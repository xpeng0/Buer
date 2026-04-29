package com.cscyxp.finance.tecent

import com.cscyxp.finance.StockExchange
import com.cscyxp.finance.entity.StockKey
import com.cscyxp.finance.hilt.FinanceNetworkModule
import com.cscyxp.finance.tencent.TencentStockApi
import com.cscyxp.finance.shouldBe
import com.cscyxp.finance.tencent.TencentDataSource
import com.cscyxp.finance.tencent.TencentStockDetail
import com.cscyxp.finance.tencent.TencentStockResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TencentDataSourceTest {
    private val api = mockk<TencentStockApi>(relaxed = true)

    @BeforeEach
    fun setup() {

    }

    @AfterEach
    fun teardown() {

    }

    fun createDataSource(): TencentDataSource {
        return TencentDataSource(api)
    }

    @Test
    fun `getStockRecentKLine - query param should be correct`() = runTest {
        val dataSource = createDataSource()
        val symbol = "000905"
        val days = 30
        dataSource.getStockRecentKLine(StockKey(symbol, StockExchange.SHANG_HAI), 30)
        coVerify {
            api.getKLineData("sh$symbol,day,,,$days,qfq")
        }
    }

    @Test
    fun `getStockRecentKLine - should return stock entity correctly`() = runTest {
        val symbol = "000905"
        val exchange = StockExchange.SHANG_HAI
        val days = 30
        val generatorStockResponse = TencentStockGenerator.generatorRandomStockResponse(
            StockKey(symbol, exchange),
            days
        )
        coEvery { api.getKLineData(any()) } returns generatorStockResponse.tencentStockResponse
        val dataSource = createDataSource()
        val stockEntity = dataSource.getStockRecentKLine(StockKey(symbol, exchange), days)
        stockEntity.shouldBe(generatorStockResponse.stockEntity)
    }


}