package com.cscyxp.finance.details.ui.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cscyxp.finance.R
import com.cscyxp.finance.StockTrend
import com.cscyxp.finance.details.ui.state.StockDetailUiState
import com.cscyxp.finance.details.vm.StockDetailViewModel
import com.cscyxp.xpviews.composable.TrendLineChart

@Composable
fun StockDetailScreenRoute(
    onBackClick: () -> Unit = {},
    viewModel: StockDetailViewModel = hiltViewModel()
) {
    val state by viewModel.stateFlow.collectAsState()
    Scaffold(
        containerColor = Color(0xFFF1F5F9) // 浅灰背景色
    ) { paddingValues ->
        AnimatedContent(
            targetState = state,
            contentKey = { it::class },
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "ScreenTransition",
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { currentState ->
            when (currentState) {
                is StockDetailUiState.Error -> {}
                is StockDetailUiState.Loading -> {

                }
                is StockDetailUiState.Success -> {
                    StockDetailScreenSuccess(
                        state = currentState,
                        onPointSelected = viewModel::onPointSelected,
                        onBackClick = onBackClick
                    )
                }
            }
        }

    }

}


@Composable
fun StockDetailScreenSuccess(
    state: StockDetailUiState.Success,
    onPointSelected: (Int?) -> Unit,
    onBackClick: () -> Unit = {},
) {
    val periods = listOf("分时", "近1月", "近3月", "近1年")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        // 头部信息 (股票名、代码)
        item { StockHeader(symbol = state.stockKey.symbol, name = state.stockName, onBackClick = onBackClick) }

        // 价格区域
        item { PriceSection(price = state.displayPrice, change = state.displayPercent, trend = state.todayTrend) }

        // 时间周期选择器
        item {
            PeriodSelector(
                periods = periods,
                selectedIndex = 0,
                onOptionSelected = {  }
            )
        }

        // 🌟 你的自定义折线图桥接区域
        item { CustomTrendLineChart(
            minutes = state.minutes,
            touchIndex = state.touchInfo?.index,
            onPointSelected = onPointSelected,
            ) }

        // 底部 2x2 统计网格
        item { StatisticsGrid() }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

// ==========================================
// 2. 各个子组件拆解
// ==========================================

@Composable
fun StockHeader(
    symbol: String,
    name: String,
    onBackClick: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "返回上一页",
            tint = Color.Black,
            modifier = Modifier.clickable { onBackClick() }
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Text(
                text = symbol,
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
        }
        
    }
}

@Composable
fun PriceSection(
    price: String,
    change: String,
    trend: StockTrend
) {
    val changeColor = when (trend) {
        StockTrend.UP -> colorResource(R.color.stock_red)
        StockTrend.DOWN -> colorResource(R.color.stock_green)
        StockTrend.FLAT -> colorResource(R.color.stock_flat)
    }

    Row(verticalAlignment = Alignment.Bottom) {
        Text(
            text = "$$price",
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF0F172A)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = change,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = changeColor,
            modifier = Modifier.padding(bottom = 6.dp)
        )
    }
}

@Composable
fun PeriodSelector(
    periods: List<String>,
    selectedIndex: Int = 0,
    onOptionSelected: (Int) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        periods.forEachIndexed { index, period ->
            val isSelected = index == selectedIndex

            // 🌟 使用基础的 Box 来完全控制背景、边框和形状
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .clip(RoundedCornerShape(20.dp)) // 🌟 🌟 🌟 代码定义圆角形状，代替 XML shape
                    .then(
                        if (isSelected) {
                            // 🌟 🌟 🌟 代码定义选中状态的背景，代替 XML selector
                            Modifier.background(Color(0xFF1F2937)) // 深黑色背景
                        } else {
                            Modifier.background(Color.White) // 浅灰色背景
                        }
                    )
                    .clickable { onOptionSelected(index) } // 🌟 使其可点击并更新状态
                    .padding(vertical = 12.dp), // 🌟 按钮内部的上下内边距
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = period,
                    // 🌟 🌟 🌟 代码根据选中状态改变文字颜色
                    color = if (isSelected) Color.White else Color(0xFF6B7280), // 选中时白色，未选中时深灰色
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun CustomTrendLineChart(
    minutes: List<Float>,
    touchIndex: Int? = null,
    onPointSelected: (Int?) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        TrendLineChart(
            modifier = Modifier.fillMaxSize(),
            minutes = minutes,
            totalPoints = 240,
            touchIndex = touchIndex,
            onPointSelected = onPointSelected
        )
    }
}

@Composable
fun StatisticsGrid() {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StatItem(label = "30-Day High", value = "$182.34")
            StatItem(label = "30-Day Low", value = "$165.23")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StatItem(label = "Volume", value = "110.6M")
            StatItem(label = "Market Cap", value = "$1.250B")
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(modifier =
        Modifier
            .height(70.dp)
            .width(130.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(15.dp)
            )
            .padding(10.dp),
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color(0xFF64748B)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF0F172A)
        )
    }

}