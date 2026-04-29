package com.cscyxp.finance.tencent

// 1. 最外层响应
data class TencentStockResponse(
    val code: Int,
    val msg: String,
    // 关键：因为 "sh000905" 是动态的，所以必须用 Map
    // Key 是股票代码，Value 是具体数据
    val data: Map<String, TencentStockDetail>?
)