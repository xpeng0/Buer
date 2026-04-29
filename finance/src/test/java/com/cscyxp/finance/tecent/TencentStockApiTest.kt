package com.cscyxp.finance.tecent

import com.cscyxp.finance.tencent.TencentStockApi
import com.cscyxp.finance.tencent.TencentDataSource
import com.cscyxp.finance.util.JsonUtil
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TencentStockApiTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: TencentStockApi
    private val kLines = listOf(
        listOf(
            "2026-03-27",
            "7539.200",
            "7737.610",
            "7785.380",
            "7539.200",
            "188205066.000"
        ),
        listOf(
            "2026-03-30",
            "7655.15",
            "7753.72",
            "7766.11",
            "7610.55",
            "197838569"
        ),
    )

    @BeforeEach
    fun setup() {
        // 1. 初始化并启动 MockWebServer
        mockWebServer = MockWebServer()
        mockWebServer.start()

        // 2. 手动构建 Retrofit 实例
        // 关键点：mockWebServer.url("/") 会返回类似 "http://localhost:59123/" 的本地地址
        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TencentStockApi::class.java)
    }

    @AfterEach
    fun teardown() {
        // 测试结束后关闭服务器，释放端口
        mockWebServer.shutdown()
    }

    @Test
    fun `test getKLineData parses SUCCESS JSON correctly`() = runTest {
        val symbol = "sh000905"
        val arg = "sh000905,day,,,30,qfq"
        // 1. 读取本地 JSON 文件
        val mockJsonString = JsonUtil.readFileFromResources("tencent_stock_success.json")

        // 2. 让假服务器排队准备一个响应 (HTTP 200，内容就是我们的 JSON)
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockJsonString)
        )

        // 3. 调用你的接口 (这里实际上是发往本地的 mockWebServer)
        val response = api.getKLineData(arg)

        // 4. 断言 (Assertions) - 验证你的数据类映射是否正确
        val data = response.data?.get(symbol)
        val qt = data?.qt?.get(symbol)
        assertAll(
            { assertEquals(0, response.code) },
            { assertEquals("", response.msg) },
            { assertEquals(kLines, data?.getKLineData()) },
            {
                assertEquals(
                    "中证500",
                    qt?.getOrNull(TencentDataSource.QT_INDEX_NAME)
                )
            },
            {
                assertEquals(
                    "7753.72",
                    qt?.getOrNull(TencentDataSource.QT_INDEX_CURRENT_PRICE)
                )
            },
            {
                assertEquals(
                    "0.21",
                    qt?.getOrNull(TencentDataSource.QT_INDEX_PERCENT)
                )
            },
        )


        // 5. 验证请求路径是否拼接正确
        val recordedRequest = mockWebServer.takeRequest()
        val url = recordedRequest.requestUrl
        assertAll(
            { assertEquals("GET", recordedRequest.method) },
            { assertEquals("/appstock/app/fqkline/get", url?.encodedPath) },
            { assertEquals(arg, url?.queryParameter("param")) },
        )
    }

}