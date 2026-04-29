package com.cscyxp.finance.search.ui.state

import androidx.annotation.ColorRes
import com.cscyxp.finance.R

enum class StockTag(
    val displayName: String,
    @ColorRes val textColorRes: Int,
    @ColorRes val bgColorRes: Int
) {
    A_SHARE("沪深", R.color.tag_ashare_text, R.color.tag_ashare_bg),
    HK_STOCK("港股", R.color.tag_hk_text, R.color.tag_hk_bg),
    US_STOCK("美股", R.color.tag_us_text, R.color.tag_us_bg),
    ETF("ETF", R.color.tag_etf_text, R.color.tag_etf_bg),
    FUND("基金", R.color.tag_fund_text, R.color.tag_fund_bg),
    INDEX("指数", R.color.tag_index_text, R.color.tag_index_bg),
    UNKNOWN("未知", R.color.tag_unknown_text, R.color.tag_unknown_bg);
}