package com.cscyxp.finance.tencent

import android.util.Log
import com.cscyxp.finance.SearchRange
import com.cscyxp.finance.StockExchange
import com.cscyxp.finance.decodeUnicode
import com.cscyxp.finance.entity.StockInfo
import com.cscyxp.finance.entity.StockKey
import com.cscyxp.finance.entity.StockMinute
import com.cscyxp.finance.entity.StockQuotation
import com.cscyxp.finance.toDoubleOrZero
import com.cscyxp.finance.search.ui.state.StockTag
import com.google.gson.JsonObject

object TencentStockUtil {
    fun getTencentSymbol(stockKey: StockKey): String {
        val symbol = stockKey.symbol
        return when(stockKey.exchange) {
            StockExchange.SHANG_HAI -> "sh$symbol"
            StockExchange.SHEN_ZHEN -> "sz$symbol"
            StockExchange.HONG_KONG -> "hk$symbol"
            StockExchange.US -> "us$symbol"
        }
    }

    fun getStockKey(tencentSymbol: String): StockKey? {
        val symbol = tencentSymbol.substring(2)
        return when(tencentSymbol.substring(0, 2)) {
            "sh" -> StockKey(symbol, StockExchange.SHANG_HAI)
            "sz" -> StockKey(symbol, StockExchange.SHEN_ZHEN)
            "hk" -> StockKey(symbol, StockExchange.HONG_KONG)
            else -> throw RuntimeException("不存在的交易所编号")
        }
    }

    fun getExchange(tencentExchange: String): StockExchange {
        return when(tencentExchange) {
            "sh" -> StockExchange.SHANG_HAI
            "sz" -> StockExchange.SHEN_ZHEN
            "hk" -> StockExchange.HONG_KONG
            "us" -> StockExchange.US
            else -> throw RuntimeException("不存在的交易所编号")
        }
    }

    fun getQtUrl(stockKeys: List<StockKey>): String {
        val params = stockKeys.joinToString(separator = ",") {
            getTencentSymbol(it)
        }
        return "https://qt.gtimg.cn/q=$params"
    }

    /**
    v_sh000905="1~中证500~000905~7898.63~7945.15~7865.21~190555650~0~0~0.00~0~0.00~0~0.00~0~0.00~0~0.00~0~0.00~0~0.00~0~0.00~0~0.00~0~0.00~0~~20260409161310~-46.52~-0.59~7947.68~7845.96~7898.63/190555650/398032241410~190555650~39803224~1.70~35.69~~7947.68~7845.96~1.28~161691.92~176684.83~0.00~-1~-1~1.08~0~7899.33~~~~~~39803224.1410~0.0000~0~
    ~ZS~5.80~1.92~~~~8683.98~5492.65~1.69~-6.01~1.08~1119081514053~~4.90~32.02~1119081514053~~~45.20~-0.03~~CNY~0~~0.00~0~";
    v_sz000001="51~平安银行~000001~11.10~11.22~11.17~602365~199626~402739~11.09~2236~11.08~4490~11.07~3808~11.06~7583~11.05~9367~11.10~1855~11.11~4409~11.12~3569~11.13~2695~11.14~2920~~20260409161309~-0.12~-1.07~11.22~11.06~11.10/602365/669239285~602365~66924~0.31~5.05~~11.22~11.06~1.43~2154.02~2154.06~0.48~12.34~10.10~0.71~12036~11.11~5.05~5.05~~~0.45~66923.9285~0.0000~0~
    ~GP-A~-2.72~-0.45~5.39~7.73~0.72~13.09~10.22~1.46~1.93~-4.88~19405600653~19405918198~28.04~-14.25~19405600653~~~8.80~0.09~~CNY~0~~11.00~11847~";
     */
    fun parseQtResponse(qtString: String): Map<StockKey, StockQuotation> {
        // 分号隔开不同stock 并去除换行符
        val stocks = qtString.split(";").map { it.replace("\n", "").replace("\r", "") }
        return buildMap {
            for (str in stocks) {
                // 1. 基础校验：过滤空行和格式错误的数据
                if (str.isBlank() || !str.contains("=")) continue


                // 2. 解析并校验 Key (使用 substringBefore 比 indexOf 更优雅)
                val prefix = str.substringBefore("=")
                val tencentSymbol = prefix.replace("\n", "").replace("v_", "").trim()

                // 🌟 核心防御：如果找不到对应的 StockKey，直接跳过这条数据，不加入 Map！
                val stockKey = getStockKey(tencentSymbol) ?: continue

                // 3. 解析并校验 Value
                val contentMatch = Regex("\"(.*?)\"").find(str) ?: continue
                val content = contentMatch.groupValues[1]
                val qtArray = content.split("~")

                // 🌟 核心防御：防止数组越界。如果没有足够的数据字段，直接跳过！
                if (qtArray.size < 35) continue
                // 4. 组装实体
                val quotation = StockQuotation(
                    open = qtArray[TencentDataSource.QT_INDEX_OPEN].toDoubleOrZero(),
                    close = qtArray[TencentDataSource.QT_INDEX_CURRENT_PRICE].toDoubleOrZero(),
                    high = qtArray[TencentDataSource.QT_INDEX_HIGH].toDoubleOrZero(),
                    low = qtArray[TencentDataSource.QT_INDEX_LOW].toDoubleOrZero(),
                    percent = qtArray[TencentDataSource.QT_INDEX_PERCENT].toDoubleOrZero(),
                    time = parseQtTime(qtArray[TencentDataSource.QT_INDEX_TIME])
                )

                // 5. 校验全部通过，安全存入 Map
                put(stockKey, quotation)
            }
        }
    }

    fun getFuzzySearchUrl(input: String, range: SearchRange): String {
        val type = when (range) {
            SearchRange.ALL -> "all"
            SearchRange.CHINA -> "gp"
            SearchRange.HK -> "hk"
            SearchRange.US -> "us"
            SearchRange.FUND -> "jj"
        }
        return "https://smartbox.gtimg.cn/s3/?v=2&q=${input}&t=${type}"
    }

    /**
    v_hint="sh~512890~\u7ea2\u5229\u4f4e\u6ce2ETF\u534e\u6cf0\u67cf\u745e~hldbetfhtbr~ETF^sh~563020~\u7ea2\u5229\u4f4e\u6ce2ETF\u6613\u65b9\u8fbe~hldbetfyfd~ETF^sh~515450~\u7ea2\u5229\u4f4e\u6ce250ETF\u5357\u65b9~hldb50etfnf~ETF^sz~159307~\u7ea2\u5229\u4f4e\u6ce2100ETF\u535a\u65f6~hldb100etfbs~ETF^sh~515100~\u7ea2\u5229\u4f4e\u6ce2100ETF\u666f\u987a~hldb100etfjs~ETF^sz~159547~\u7ea2\u5229\u4f4e\u6ce2ETF\u534e\u590f~hldbetfhx~ETF^sz~159549~\u7ea2\u5229\u4f4e\u6ce2ETF\u5929\u5f18~hldbetfth~ETF^sz~159525~\u7ea2\u5229\u4f4e\u6ce2ETF\u5bcc\u56fd~hldbetffg~ETF^sh~560150~\u7ea2\u5229\u4f4e\u6ce2ETF\u6cf0\u5eb7~hldbetftk~ETF^sh~560520~\u7ea2\u5229\u4f4e\u6ce2100ETF\u5927\u6210~hldb100etfdc~ETF"
     */
    fun parseFuzzySearchResponse(searchString: String): List<StockInfo> {
        try {
            val str = searchString.replace("\n", "").replace("\r", "")
            // 获取""之间的内容
            val contentMatch = Regex("\"(.*?)\"").find(str) ?: return emptyList()
            val content = contentMatch.groupValues[1]
            // Log.d("parseFuzzySearchResponse", content)
            val infos = content.split("^")
            return infos.map { info ->
                val array = info.split("~")
                StockInfo(
                    stockKey = StockKey(array[1], getExchange(array[0])),
                    stockName = array[2].decodeUnicode(),
                    mapStockTag(array[0], array[4])
                )
            }
        } catch (e: Exception) {
            // Log.e("parseFuzzySearchResponse", "转化搜索结果出错: $e")
            return emptyList()
        }


    }

    fun mapStockTag(exchange: String, tag: String): StockTag {
        return when (tag) {
            "ZS" -> StockTag.INDEX
            "ETF" -> StockTag.ETF
            "KJ", "FJ" -> StockTag.FUND
            "GP", "GP-A" -> {
                when (exchange) {
                    "sh", "sz" -> StockTag.A_SHARE
                    "hk" -> StockTag.HK_STOCK
                    "us" -> StockTag.US_STOCK
                    "jj" -> StockTag.FUND
                    else -> StockTag.UNKNOWN
                }
            }
            else -> StockTag.UNKNOWN
        }
    }

    fun parseMinuteResponse(stockKey: StockKey, jsonObject: JsonObject): StockMinute {
        val tencentSymbol = getTencentSymbol(stockKey)
        val data = jsonObject.getAsJsonObject("data")?.getAsJsonObject(tencentSymbol)
        val minutes = data
            ?.getAsJsonObject("data")
            ?.getAsJsonArray("data")
        val qt = data
            ?.getAsJsonObject("qt")
            ?.getAsJsonArray(tencentSymbol)
        if (minutes == null || qt == null) throw RuntimeException("分时数据异常")
        return StockMinute(
            stockKey = stockKey,
            stockName = qt[TencentDataSource.QT_INDEX_NAME].asString,
            minutes = minutes.mapNotNull {
                val split = it.asString.split(" ")
                split[1].toDoubleOrNull()
            },
            currentPrice = qt[TencentDataSource.QT_INDEX_CURRENT_PRICE].asDouble,
            todayPercent = qt[TencentDataSource.QT_INDEX_PERCENT].asDouble,
            high = qt[TencentDataSource.QT_INDEX_HIGH].asDouble,
            low = qt[TencentDataSource.QT_INDEX_LOW].asDouble,
            time = parseQtTime(qt[TencentDataSource.QT_INDEX_TIME].asString)
        )
    }

    fun parseQtTime(qtTime: String): Long {
        return qtTime
            .replace(" ", "").replace("\n", "").replace("/", "").replace(":", "")
            .substring(0, 12).toLong()
    }
}