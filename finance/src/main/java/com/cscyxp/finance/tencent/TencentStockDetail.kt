package com.cscyxp.finance.tencent

import com.google.gson.annotations.SerializedName

data class TencentStockDetail(
    // K线数据。注意：根据你的请求参数，这里可能是 "day" 也可能是 "qfqday"
    @SerializedName("day") val dayKLine: List<List<String>>? = null,
    @SerializedName("qfqday") val qfqDayKLine: List<List<String>>? = null,
    @SerializedName("hfqday") val hfqDayKLine: List<List<String>>? = null,

    // qt 对象里面的 key 也是动态的（股票代码 和 "market"），也用 Map 接
    val qt: Map<String, List<String>>? = null,

    val prec: String? = null,
    val version: String? = null
) {
    // 💡 辅助方法：智能获取 K 线数据，不管是 day 还是 qfqday 都有防错处理
    fun getKLineData(): List<List<String>> {
        return qfqDayKLine ?: hfqDayKLine ?: dayKLine ?: emptyList()
    }
}