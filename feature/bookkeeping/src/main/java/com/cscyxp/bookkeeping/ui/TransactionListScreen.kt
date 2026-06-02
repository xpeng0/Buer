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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cscyxp.bookkeeping.R
import com.cscyxp.bookkeeping.domain.Category
import com.cscyxp.bookkeeping.domain.DailyTransaction
import com.cscyxp.bookkeeping.domain.Transaction
import com.cscyxp.bookkeeping.ui.state.TransactionListUiState
import com.cscyxp.bookkeeping.util.format2f
import com.cscyxp.bookkeeping.vm.TransactionListViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
internal fun TransactionListScreen(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransactionListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showMonthPicker by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }

    TransactionListScreen(
        uiState = uiState,
        showMonthPicker = showMonthPicker,
        showCategoryPicker = showCategoryPicker,
        onAddClick = onAddClick,
        onMonthClick = { showMonthPicker = true },
        onCategoryClick = { showCategoryPicker = true },
        onMonthSelected = {
            viewModel.setMonth(it)
            showMonthPicker = false
        },
        onCategoryFilterTypeChange = viewModel::setCategoryDialogFilterType,
        onCategorySelected = {
            viewModel.setCategory(it)
            showCategoryPicker = false
        },
        onDialogDismiss = {
            showMonthPicker = false
            showCategoryPicker = false
        },
        modifier = modifier
    )
}

@Composable
private fun TransactionListScreen(
    uiState: TransactionListUiState,
    showMonthPicker: Boolean,
    showCategoryPicker: Boolean,
    onAddClick: () -> Unit,
    onMonthClick: () -> Unit,
    onCategoryClick: () -> Unit,
    onMonthSelected: (Int) -> Unit,
    onCategoryFilterTypeChange: (Int) -> Unit,
    onCategorySelected: (Category) -> Unit,
    onDialogDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .then(modifier),
    ) {
        when (uiState) {
            TransactionListUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is TransactionListUiState.Error -> {
                Text(
                    text = uiState.message,
                    color = Color(0xFFDC2626),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is TransactionListUiState.Content -> {
                TransactionListContent(
                    uiState = uiState,
                    onMonthClick = onMonthClick,
                    onCategoryClick = onCategoryClick
                )
            }
        }

        ExtendedFloatingActionButton(
            onClick = onAddClick,
            containerColor = Color.Black,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.round_add_24),
                contentDescription = "记一笔",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("记一笔")
        }
    }

    val contentState = uiState as? TransactionListUiState.Content
    if (showMonthPicker && contentState != null) {
        MonthPickerDialog(
            currentMonth = contentState.filter.month,
            onConfirm = onMonthSelected,
            onDismiss = onDialogDismiss
        )
    }

    if (showCategoryPicker && contentState != null) {
        CategoryPickerDialog(
            categories = contentState.topCategories,
            selectedType = contentState.categoryDialogFilterType,
            onTypeChange = onCategoryFilterTypeChange,
            onCategorySelected = onCategorySelected,
            onDismiss = onDialogDismiss
        )
    }
}

@Composable
private fun TransactionListContent(
    uiState: TransactionListUiState.Content,
    onMonthClick: () -> Unit,
    onCategoryClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            MonthInfoCard(
                month = uiState.filter.month,
                year = uiState.filter.year,
                categoryName = uiState.filter.category?.name ?: "全部类型",
                expense = uiState.expenseSumStr,
                income = uiState.incomeSumStr,
                balance = uiState.balanceStr,
                onMonthClick = onMonthClick,
                onCategoryClick = onCategoryClick
            )
        }

        item {
            Text(
                text = "最近交易",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        items(uiState.dailyTransactions, key = { it.date }) { daily ->
            DailyTransactionCard(daily)
        }
    }
}

@Composable
private fun MonthInfoCard(
    month: Int,
    year: Int,
    categoryName: String,
    expense: String,
    income: String,
    balance: String,
    onMonthClick: () -> Unit,
    onCategoryClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${month}月收支",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onMonthClick() }
                )
                Text(
                    text = categoryName,
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    modifier = Modifier
                        .background(Color(0xFFF3F4F6), RoundedCornerShape(8.dp))
                        .clickable { onCategoryClick() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AmountColumn(label = "总支出", amount = expense, color = Color(0xFFDC2626))
                AmountColumn(label = "总收入", amount = income, color = Color(0xFF16A34A))
                AmountColumn(label = "结余", amount = balance, color = Color(0xFF111827))
            }
        }
    }
}

@Composable
private fun AmountColumn(label: String, amount: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 12.sp, color = Color(0xFF6B7280))
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = amount, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun DailyTransactionCard(daily: DailyTransaction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = daily.date.format(DateTimeFormatter.ofPattern("M月d日 EEEE", Locale.CHINESE)),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF374151)
                )
                Row {
                    Text(text = "支出 ${daily.expense}", fontSize = 13.sp, color = Color(0xFFDC2626))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "收入 ${daily.income}", fontSize = 13.sp, color = Color(0xFF16A34A))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFF3F4F6))

            daily.transactions.forEach { transaction ->
                TransactionItem(transaction)
            }
        }
    }
}

@Composable
private fun TransactionItem(transaction: Transaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(categoryIconRes(transaction.category)),
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = transaction.category.name, fontSize = 15.sp, color = Color(0xFF111827))
            Text(
                text = formatTimestamp(transaction.date),
                fontSize = 12.sp,
                color = Color(0xFF9CA3AF)
            )
        }
        Text(
            text = if (transaction.type == 0) "-${transaction.amount.format2f()}" else "+${transaction.amount.format2f()}",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (transaction.type == 0) Color(0xFFDC2626) else Color(0xFF16A34A)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MonthPickerDialog(
    currentMonth: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedMonth by remember { mutableIntStateOf(currentMonth) }

    AlertDialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Text("选择月份", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (row in 0..2) {
                    Column {
                        for (col in 1..4) {
                            val m = row * 4 + col
                            FilterChip(
                                selected = m == selectedMonth,
                                onClick = { selectedMonth = m },
                                label = { Text("${m}月") },
                                modifier = Modifier.padding(2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) { Text("取消") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { onConfirm(selectedMonth) }) { Text("确认") }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryPickerDialog(
    categories: List<Category>,
    selectedType: Int,
    onTypeChange: (Int) -> Unit,
    onCategorySelected: (Category) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Text("选择分类", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            Row {
                FilterChip(
                    selected = selectedType == Category.TYPE_EXPAND,
                    onClick = { onTypeChange(Category.TYPE_EXPAND) },
                    label = { Text("支出") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = selectedType == Category.TYPE_INCOME,
                    onClick = { onTypeChange(Category.TYPE_INCOME) },
                    label = { Text("收入") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                items(categories) { category ->
                    Text(
                        text = category.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCategorySelected(category) }
                            .padding(vertical = 12.dp),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

private fun formatTimestamp(millis: Long): String {
    val date = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
    return date.format(DateTimeFormatter.ofPattern("MM/dd"))
}

private fun categoryIconRes(category: Category): Int {
    return try {
        val field = R.drawable::class.java.getField(category.icon)
        field.getInt(null)
    } catch (e: Exception) {
        R.drawable.wallet
    }
}
