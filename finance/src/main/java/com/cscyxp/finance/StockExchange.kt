package com.cscyxp.finance

enum class StockExchange {
    SHANG_HAI,
    SHEN_ZHEN,
    HONG_KONG,
    US
}

enum class StockTrend {
    UP,    // 涨
    DOWN,  // 跌
    FLAT   // 平盘 (0.00%)
}

enum class SearchRange {
    ALL,
    CHINA,
    HK,
    US,
    FUND,
}