package com.cscyxp.buer.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ==========================================
// 1. 数据结构定义 (Data Models)
// ==========================================

data class MainCardData(
    val title: String,
    val subtitle: String,
    val value: String,
    val valueColor: Color = Color(0xFF111827),
    val icon: @Composable () -> Unit,
    val onClick: (() -> Unit)? = null
)

data class ToolItem(
    val name: String,
    val hasNotification: Boolean = false,
    val icon: @Composable () -> Unit
)

// ==========================================
// 2. 复用基础小组件 (Sub-composables)
// ==========================================

/**
 * 顶部带圆角背景的图标组件
 */
@Composable
fun CardIcon(
    backgroundColor: Color,
    icon: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .background(color = backgroundColor, shape = RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}

// ==========================================
// 3. 主卡片组件 (Main Dashboard Cards)
// ==========================================

@Composable
fun DashboardMainCard(
    data: MainCardData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (data.onClick != null) Modifier.clickable { data.onClick.invoke() }
                else Modifier
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            // 顶层：图标与今日数据
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                data.icon()

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Today", color = Color(0xFF9CA3AF), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = data.value,
                        color = data.valueColor,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 底层：标题与核心描述
            Text(
                text = data.title,
                color = Color(0xFF111827),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = data.subtitle,
                color = Color(0xFF6B7280),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

// ==========================================
// 4. 底部网格小工具卡片 (Grid Tool Cards)
// ==========================================

@Composable
fun ToolGridCard(
    item: ToolItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(72.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                item.icon()
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = item.name,
                    color = Color(0xFF111827),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // 右上角红点/灰点提示
            if (item.hasNotification) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(color = Color(0xFF9CA3AF), shape = RoundedCornerShape(3.dp))
                        .align(Alignment.Top)
                        .padding(top = 12.dp)
                )
            }
        }
    }
}

// ==========================================
// 5. 底部周总结深色面板 (Weekly Summary Panel)
// ==========================================

@Composable
fun WeeklySummaryPanel() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B132B)) // 深海蓝黑背景
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Weekly Summary",
                color = Color(0xFF9CA3AF),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Focus Column
                SummaryDataColumn(label = "Focus", value = "12h", valueColor = Color.White)
                // Saved Column
                SummaryDataColumn(label = "Saved", value = "$420", valueColor = Color.White)
                // Growth Column
                SummaryDataColumn(label = "Growth", value = "+8%", valueColor = Color(0xFF10B981)) // 翠绿
            }
        }
    }
}

@Composable
fun SummaryDataColumn(label: String, value: String, valueColor: Color) {
    Column {
        Text(text = label, color = Color(0xFF6B7280), fontSize = 13.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = value, color = valueColor, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}

// ==========================================
// 6. 主屏幕拼接布局 (Main Screen Layout)
// ==========================================

@Composable
fun BuerHomeScreen(
    onFinanceClick: () -> Unit = {},
    onBookkeepingClick: () -> Unit = {}
) {
    // 模拟主卡片高精度数据源
    val mainCards = listOf(
        MainCardData(
            title = "理财",
            subtitle = "Track your investments and monitor market trends",
            value = "+$2,847",
            valueColor = Color(0xFF111827),
            onClick = onFinanceClick,
            icon = {
                CardIcon(backgroundColor = Color(0xFFE8F5E9)) {
                    Icon(Icons.AutoMirrored.Default.TrendingUp, contentDescription = "理财", tint = Color(0xFF2E7D32))
                }
            }
        ),
        MainCardData(
            title = "番茄钟",
            subtitle = "Stay focused with time management techniques",
            value = "4 / 8",
            icon = {
                CardIcon(backgroundColor = Color(0xFFFFF3E0)) {
                    Icon(Icons.Default.Schedule, contentDescription = "番茄钟", tint = Color(0xFFEF6C00))
                }
            }
        ),
        MainCardData(
            title = "记账",
            subtitle = "Manage expenses and track your daily spending",
            value = "-$158",
            onClick = onBookkeepingClick,
            icon = {
                CardIcon(backgroundColor = Color(0xFFE8EAF6)) {
                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = "记账", tint = Color(0xFF3F51B5))
                }
            }
        )
    )

    // More Tools 网格数据源
    val toolItems = listOf(
        ToolItem("阅读", true) {
            CardIcon(backgroundColor = Color(0xFFF3E5F5)) { Icon(Icons.Default.Book, "", tint = Color(0xFF8E24AA)) }
        },
        ToolItem("健身", true) {
            CardIcon(backgroundColor = Color(0xFFFCE4EC)) { Icon(Icons.Default.FitnessCenter, "", tint = Color(0xFFD81B60)) }
        },
        ToolItem("日程", true) {
            CardIcon(backgroundColor = Color(0xFFE0F7FA)) { Icon(Icons.Default.DateRange, "", tint = Color(0xFF00ACC1)) }
        },
        ToolItem("目标", true) {
            CardIcon(backgroundColor = Color(0xFFFFF3E0)) { Icon(Icons.Default.Adjust, "", tint = Color(0xFFF4511E)) }
        },
        ToolItem("健康", true) {
            CardIcon(backgroundColor = Color(0xFFFFEBEE)) { Icon(Icons.Default.Favorite, "", tint = Color(0xFFE53935)) }
        },
        ToolItem("音乐", true) {
            CardIcon(backgroundColor = Color(0xFFE8F5E9)) { Icon(Icons.Default.MusicNote, "", tint = Color(0xFF43A047)) }
        }
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF9FAFB) // 浅灰底色
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp, top = 16.dp)
        ) {
            // 头部欢迎语区域
            item {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(text = "5月24日 星期日", color = Color(0xFF6B7280), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Welcome Back",
                        color = Color(0xFF111827),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 循环渲染三大主功能卡片
            items(mainCards.size) { index ->
                DashboardMainCard(data = mainCards[index])
            }

            // "More Tools" 隔离标题
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "More Tools",
                    color = Color(0xFF111827),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // 网格区域：由于 LazyColumn 内部不能直接嵌套独立的滚动网格，
            // 推荐在这里直接按行拼装或使用高度固定的大 Item 嵌套。此处采用 3 行跨度拼接。
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    for (i in toolItems.indices step 2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ToolGridCard(item = toolItems[i], modifier = Modifier.weight(1f))
                            if (i + 1 < toolItems.size) {
                                ToolGridCard(item = toolItems[i + 1], modifier = Modifier.weight(1f))
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // 底部周总结深色面板
            item {
                Spacer(modifier = Modifier.height(12.dp))
                WeeklySummaryPanel()
            }
        }
    }
}