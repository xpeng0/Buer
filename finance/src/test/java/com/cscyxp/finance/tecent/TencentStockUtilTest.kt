package com.cscyxp.finance.tecent

import com.cscyxp.finance.SearchRange
import com.cscyxp.finance.StockExchange
import com.cscyxp.finance.entity.StockInfo
import com.cscyxp.finance.entity.StockKey
import com.cscyxp.finance.entity.StockMinute
import com.cscyxp.finance.entity.StockQuotation
import com.cscyxp.finance.search.ui.state.StockTag
import com.cscyxp.finance.shouldBe
import com.cscyxp.finance.tencent.TencentStockUtil
import com.cscyxp.finance.util.JsonUtil
import com.google.gson.JsonParser
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import kotlin.collections.buildMap

class TencentStockUtilTest {
    @Test
    fun `test getTencentSymbol - should return tencent symbol correctly`() {
        val symbol1 = "123456"
        val symbol2 = "987053"
        val key1 = StockKey(symbol1, StockExchange.SHANG_HAI)
        val key2 = StockKey(symbol2, StockExchange.SHEN_ZHEN)
        assertAll (
            { TencentStockUtil.getTencentSymbol(key1).shouldBe("sh$symbol1") },
            { TencentStockUtil.getTencentSymbol(key2).shouldBe("sz$symbol2") }
        )
    }

    @Test
    fun `test getStockKey - should parse tencent symbol to stockKey correctly`() {
        val symbol1 = "123456"
        val symbol2 = "987053"
        val tencentSymbol1 = "sh123456"
        val tencentSymbol2 = "sz987053"
        val key1 = StockKey(symbol1, StockExchange.SHANG_HAI)
        val key2 = StockKey(symbol2, StockExchange.SHEN_ZHEN)
        assertAll (
            { TencentStockUtil.getStockKey(tencentSymbol1).shouldBe(key1) },
            { TencentStockUtil.getStockKey(tencentSymbol2).shouldBe(key2) }
        )
    }

    @Test
    fun `test getQtUrl - should build qt url correctly`() {
        val stockKeys = listOf(
            StockKey("000905", StockExchange.SHANG_HAI),
            StockKey("000001", StockExchange.SHEN_ZHEN),
            StockKey("00700", StockExchange.HONG_KONG),
        )
        TencentStockUtil.getQtUrl(stockKeys).shouldBe("https://qt.gtimg.cn/q=sh000905,sz000001,hk00700")
    }

    @Test
    fun `test parseQtResponse - should parse qt response correctly`() {
        val qtString = "v_sh000905=\"1~中证500~000905~7972.85~7898.63~7958.81~195487780~0~0~0.00~0~0.00~0~0.00~0~0.00~0~0.00~0~0.00~0~0.00~0~0.00~0~0.00~0~0.00~0~~20260410145239~74.22~0.94~8070.65~7953.50~7972.85/195487780/417509654603~195487780~41750965~1.74~36.03~~8070.65~7953.50~1.48~163699.93~178322.53~0.00~-1~-1~1.13~0~8007.18~~~~~~41750965.4603~0.0000~0~\n" +
                "~ZS~6.79~4.83~~~~8683.98~5492.65~4.33~-4.62~1.24~1123484542367~~-5.13~32.28~1123484542367~~~43.81~-0.08~~CNY~0~~0.00~0~\";\n" +
                "v_sz000001=\"51~平安银行~000001~11.09~11.10~11.10~465555~209799~255410~11.08~10874~11.07~7566~11.06~6411~11.05~9449~11.04~2449~11.10~5434~11.11~3391~11.12~7224~11.13~10006~11.14~3837~~20260410145239~-0.01~-0.09~11.13~11.07~11.09/465555/516904502~465555~51690~0.24~5.05~~11.13~11.07~0.54~2152.08~2152.12~0.48~12.21~9.99~0.61~6857~11.10~5.05~5.05~~~0.46~51690.4502~0.0000~0~\n" +
                "~GP-A~-2.80~-1.60~5.39~7.73~0.72~13.09~10.23~1.37~1.37~-4.73~19405600653~19405918198~10.29~-12.50~19405600653~~~7.65~-0.09~~CNY~0~~11.00~10328~\";\n" +
                "v_hk00700=\"100~腾讯控股~00700~503.000~508.500~508.000~14841523.0~0~0~503.000~0~0~0~0~0~0~0~0~0~503.000~0~0~0~0~0~0~0~0~0~14841523.0~2026/04/10\n" +
                "14:37:40~-5.500~-1.08~514.000~501.500~503.000~14841523.0~7522797952.640~0~18.44~~0~0~2.46~45901.7561~45901.7561~TENCENT~0.90~683.000~427.500~0.84~34.56~0~0~0~0~0~18.44~3.62~0.16~100~-16.03~3.93~GP~19.48~11.27~-2.14~-9.12~-18.34~9125597636.00~9125597636.00~18.44~4.531~506.875~-2.61~HKD~1~30\";"
        val key1 = StockKey("000905", StockExchange.SHANG_HAI)
        val key2 = StockKey("000001", StockExchange.SHEN_ZHEN)
        val key3 = StockKey("00700", StockExchange.HONG_KONG)
        val mockZz500 = StockQuotation(
            open = 7958.81,
            close = 7972.85,
            high = 8070.65,
            low = 7953.50,
            percent = 0.94,
            time = 202604101452
        )

        val mockPab = StockQuotation(
            open = 11.10,
            close = 11.09,
            high = 11.13,
            low = 11.07,
            percent = -0.09,
            time = 202604101452
        )

        val mockTencent = StockQuotation(
            open = 508.000,
            close = 503.000,
            high = 514.000,
            low = 501.500,
            percent = -1.08,
            time = 202604101437
        )
        val stockQuotations = buildMap {
            put(key1, mockZz500)
            put(key2, mockPab)
            put(key3, mockTencent)
        }
        TencentStockUtil.parseQtResponse(qtString).shouldBe(stockQuotations)
    }

    @Test
    fun `test getFuzzySearchUrl`() {
        val expect = "https://smartbox.gtimg.cn/s3/?v=2&q=红利低波&t=all"
        TencentStockUtil.getFuzzySearchUrl("红利低波", SearchRange.ALL).shouldBe(expect)
    }

    @Test
    fun `test parseFuzzySearchResponse - should parse fuzzy search response correctly`() {
        val searchString = "v_hint=\"sh~512890~\\u7ea2\\u5229\\u4f4e\\u6ce2ETF\\u534e\\u6cf0\\u67cf\\u745e~hldbetfhtbr~ETF^sh~563020~\\u7ea2\\u5229\\u4f4e\\u6ce2ETF\\u6613\\u65b9\\u8fbe~hldbetfyfd~ETF^sh~515450~\\u7ea2\\u5229\\u4f4e\\u6ce250ETF\\u5357\\u65b9~hldb50etfnf~ETF^sz~159307~\\u7ea2\\u5229\\u4f4e\\u6ce2100ETF\\u535a\\u65f6~hldb100etfbs~ETF^sh~515100~\\u7ea2\\u5229\\u4f4e\\u6ce2100ETF\\u666f\\u987a~hldb100etfjs~ETF^sz~159547~\\u7ea2\\u5229\\u4f4e\\u6ce2ETF\\u534e\\u590f~hldbetfhx~ETF^sz~159549~\\u7ea2\\u5229\\u4f4e\\u6ce2ETF\\u5929\\u5f18~hldbetfth~ETF^sz~159525~\\u7ea2\\u5229\\u4f4e\\u6ce2ETF\\u5bcc\\u56fd~hldbetffg~ETF^sh~560150~\\u7ea2\\u5229\\u4f4e\\u6ce2ETF\\u6cf0\\u5eb7~hldbetftk~ETF^sh~560520~\\u7ea2\\u5229\\u4f4e\\u6ce2100ETF\\u5927\\u6210~hldb100etfdc~ETF\""
        val stockInfos = listOf(
            StockInfo(
                stockKey = StockKey("512890", StockExchange.SHANG_HAI),
                stockName = "红利低波ETF华泰柏瑞",
                stockTag = StockTag.ETF
            ),
            StockInfo(
                stockKey = StockKey("563020", StockExchange.SHANG_HAI),
                stockName = "红利低波ETF易方达",
                stockTag = StockTag.ETF
            ),
            StockInfo(
                stockKey = StockKey("515450", StockExchange.SHANG_HAI),
                stockName = "红利低波50ETF南方",
                stockTag = StockTag.ETF
            ),
            StockInfo(
                stockKey = StockKey("159307", StockExchange.SHEN_ZHEN),
                stockName = "红利低波100ETF博时",
                stockTag = StockTag.ETF
            ),
            StockInfo(
                stockKey = StockKey("515100", StockExchange.SHANG_HAI),
                stockName = "红利低波100ETF景顺",
                stockTag = StockTag.ETF
            ),
            StockInfo(
                stockKey = StockKey("159547", StockExchange.SHEN_ZHEN),
                stockName = "红利低波ETF华夏",
                stockTag = StockTag.ETF
            ),
            StockInfo(
                stockKey = StockKey("159549", StockExchange.SHEN_ZHEN),
                stockName = "红利低波ETF天弘",
                stockTag = StockTag.ETF
            ),
            StockInfo(
                stockKey = StockKey("159525", StockExchange.SHEN_ZHEN),
                stockName = "红利低波ETF富国",
                stockTag = StockTag.ETF
            ),
            StockInfo(
                stockKey = StockKey("560150", StockExchange.SHANG_HAI),
                stockName = "红利低波ETF泰康",
                stockTag = StockTag.ETF
            ),
            StockInfo(
                stockKey = StockKey("560520", StockExchange.SHANG_HAI),
                stockName = "红利低波100ETF大成",
                stockTag = StockTag.ETF
            )
        )
        TencentStockUtil.parseFuzzySearchResponse(searchString).shouldBe(stockInfos)
    }

    @Test
    fun `test parseMinuteResponse`() {
        val stockKey = StockKey("000001", StockExchange.SHANG_HAI)
        val jsonString = JsonUtil.readFileFromResources("tencent_stock_minute_success.json")
        val jsonObject = JsonParser.parseString(jsonString).asJsonObject
        val stockMinute = StockMinute(
            stockKey = StockKey("000001", StockExchange.SHANG_HAI),
            stockName = "上证指数",
            minutes = listOf(
                4168.45, 4165.5, 4166.13, 4168.01, 4168.25, 4167.25, 4165.57, 4168.78, 4169.08, 4169.4,
                4170.05, 4169.34, 4167.53, 4166.76, 4164.88, 4165.97, 4168.42, 4166.14, 4165.76, 4165.37,
                4167.3, 4168.23, 4167.84, 4169.49, 4170.25, 4170.9, 4169.57, 4167.06, 4164.61, 4165.15,
                4165.3, 4165.77, 4166.74, 4167.28, 4167.34, 4168.02, 4169.28, 4169.06, 4170.71, 4172.27,
                4171.16, 4168.51, 4167.36, 4168.08, 4166.32, 4164.71, 4163.32, 4164.65, 4165.09, 4165.79,
                4166.84, 4164.4, 4166.32, 4165.47, 4165.43, 4167.08, 4167.3, 4166.27, 4167.24, 4166.18,
                4167.22, 4167.48, 4167.11, 4168.13, 4168.08, 4167.22, 4167.62, 4168.61, 4168.87, 4170.03,
                4169.8, 4167.24, 4166.47, 4167.19, 4165.69, 4166.38, 4168.36, 4169.43, 4169.77, 4169.72,
                4171.23, 4171.19, 4169.71, 4169.05, 4169.53, 4168.52, 4168.44, 4168.77, 4167.59, 4166.99,
                4166.7, 4164.68, 4165.47, 4167.7, 4166.69, 4167.5, 4167.96, 4167.78, 4166.94, 4167.83,
                4168.1, 4166.8, 4165.79, 4166.5, 4166.42, 4167.21, 4168.56, 4169.65, 4168.6, 4168.55,
                4170.36, 4169.38, 4169.52, 4169.47, 4168.63, 4169.84, 4170.33, 4170.95, 4171.67, 4170.84,
                4170.75, 4170.75, 4169.99, 4169.78, 4170.39, 4169.79, 4168.22, 4169.19, 4167.93, 4166.21,
                4168.49, 4168.45, 4169.24, 4168.44, 4170.49, 4170.59, 4170.86, 4169.39, 4170.63, 4171.55,
                4170.55, 4170.61, 4171.05, 4171.47, 4171.27, 4168.32, 4170.39, 4170.53, 4170.23, 4168.96,
                4168.72, 4168.78, 4168.83, 4170.53, 4171.31, 4171.12, 4171.65, 4171.34, 4171.33, 4170.92,
                4171.01, 4170.07, 4169.96, 4171.28, 4171.57, 4172.57, 4172.58, 4174.95, 4175.76, 4176.3,
                4175.56, 4177.28, 4176.82, 4178.18, 4177.09, 4176.33, 4176.27, 4176.28, 4177.2, 4175.96,
                4173.92, 4173.64, 4175.17, 4176.14, 4176.16, 4175.81, 4175.63, 4174.87, 4176.72, 4176.54,
                4175.66, 4175.68, 4175.02, 4174.87
            ),
            currentPrice = 4174.87,
            todayPercent = 0.35,
            high = 4178.3,
            low = 4163.14,
            time = 20260507141106
        )
        TencentStockUtil.parseMinuteResponse(stockKey, jsonObject).shouldBe(stockMinute)
    }

}