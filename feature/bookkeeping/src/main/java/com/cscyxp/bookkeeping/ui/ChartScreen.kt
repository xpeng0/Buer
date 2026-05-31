package com.cscyxp.bookkeeping.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cscyxp.bookkeeping.domain.CategoryChart
import com.cscyxp.bookkeeping.util.format2f
import com.cscyxp.bookkeeping.vm.ChartViewModel
import com.cscyxp.xpviews.PieChartView
import com.cscyxp.xpviews.composable.BarChart
import com.cscyxp.xpviews.composable.PieChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ChartScreen(
    onCategoryClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChartViewModel = hiltViewModel()
) {
    val filter by viewModel.filter.collectAsState()
    val barEntries by viewModel.recentSixMonthBarEntry.collectAsState(initial = emptyList())
    val chartState by viewModel.chartUIState.collectAsState(initial = emptyList<PieChartView.PieEntry>() to emptyList<CategoryChart>())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .then(modifier),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                MonthSelector(
                    selectedMonth = filter.month,
                    onMonthSelected = { viewModel.setMonth(it) }
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "近6月支出趋势",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        BarChart(
                            entries = barEntries.map { it.label to it.value.toDouble() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                }
            }

            val (pieEntries, categoryCharts) = chartState
            if (pieEntries.isNotEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "${filter.month}月支出分布",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            PieChart(
                                entries = pieEntries.map { it.label to it.value },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                            )
                        }
                    }
                }
            }

            if (categoryCharts.isNotEmpty()) {
                item {
                    Text(
                        "分类详情",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )
                }
                items(categoryCharts) { chart ->
                    CategoryChartItem(
                        chart = chart,
                        onClick = { onCategoryClick(chart.category.id) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
private fun MonthSelector(
    selectedMonth: Int,
    onMonthSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (month in 1..12) {
            FilterChip(
                selected = month == selectedMonth,
                onClick = { onMonthSelected(month) },
                label = { Text("${month}月", fontSize = 12.sp) },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color.Black,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun CategoryChartItem(
    chart: CategoryChart,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chart.category.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF111827)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { chart.progress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(Color(0xFFF3F4F6), RoundedCornerShape(4.dp)),
                    color = Color(0xFF4CAF50),
                    trackColor = Color(0xFFF3F4F6)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = chart.value.format2f(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )
        }
    }
}
