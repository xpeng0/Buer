package com.cscyxp.bookkeeping.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cscyxp.bookkeeping.R
import com.cscyxp.bookkeeping.domain.Category
import com.cscyxp.bookkeeping.domain.KeyAction
import com.cscyxp.bookkeeping.vm.AddViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddTransactionScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddViewModel = hiltViewModel()
) {
    val selectedCategoryState by viewModel.selectedCategoryState.collectAsState()
    val amount by viewModel.amount.collectAsState()
    val date by viewModel.date.collectAsState()
    var categoryPages by remember { mutableStateOf<List<List<Category>>>(emptyList()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showSubCategoryPicker by remember { mutableStateOf(false) }
    var subCategoryList by remember { mutableStateOf<List<Category>>(emptyList()) }

    LaunchedEffect(Unit) {
        categoryPages = viewModel.getTopCategoryGrids()
    }

    LaunchedEffect(Unit) {
        viewModel.back.collect { onBackClick() }
    }

    LaunchedEffect(Unit) {
        viewModel.openDatePicker.collect { showDatePicker = true }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBackClick) { Text("← 返回") }
            Text(
                text = "记一笔",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(64.dp))
        }

        if (categoryPages.isNotEmpty()) {
                val pagerState = rememberPagerState(
                    initialPage = selectedCategoryState.page.coerceAtLeast(0),
                    pageCount = { categoryPages.size }
                )

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) { page ->
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categoryPages.getOrElse(page) { emptyList() }) { category ->
                            val isSelected = selectedCategoryState.page == page &&
                                    categoryPages[page].indexOf(category) == selectedCategoryState.grid
                            CategoryGridItem(
                                category = category,
                                isSelected = isSelected,
                                onClick = {
                                    val wasReselected = selectedCategoryState.page == page &&
                                            categoryPages[page].indexOf(category) == selectedCategoryState.grid
                                    viewModel.onTagClick(
                                        page,
                                        categoryPages[page].indexOf(category),
                                        category
                                    )
                                    if (!wasReselected && !category.sonCategories.isNullOrEmpty()) {
                                        subCategoryList = category.sonCategories!!
                                        showSubCategoryPicker = true
                                    }
                                }
                            )
                        }
                    }
                }

                if (categoryPages.size > 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(categoryPages.size) { index ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 3.dp)
                                    .size(if (index == pagerState.currentPage) 8.dp else 6.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (index == pagerState.currentPage) Color.Black
                                        else Color(0xFFD1D5DB)
                                    )
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFF3F4F6))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (selectedCategoryState.category != null) {
                        Text(
                            text = selectedCategoryState.category!!.name,
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Text(text = "¥", fontSize = 24.sp, color = Color(0xFF9CA3AF))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = amount.ifEmpty { "0" },
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            NumberPad(onKeyClick = { viewModel.handleKeyAction(it) })
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        viewModel.onDateCheckListener(millis)
                    }
                    showDatePicker = false
                }) { Text("确认") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("取消") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showSubCategoryPicker) {
        SubCategoryPickerDialog(
            categories = subCategoryList,
            onCategorySelected = { subCategory ->
                viewModel.onSonCategoryClick(subCategory)
                showSubCategoryPicker = false
            },
            onDismiss = { showSubCategoryPicker = false }
        )
    }
}

@Composable
private fun CategoryGridItem(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFFE8F5E9) else Color(0xFFF9FAFB))
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(
                try {
                    R.drawable::class.java.getField(category.icon).getInt(null) as Int
                } catch (e: Exception) {
                    R.drawable.wallet
                }
            ),
            contentDescription = category.name,
            modifier = Modifier.size(24.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = category.name,
            fontSize = 11.sp,
            color = Color(0xFF374151),
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubCategoryPickerDialog(
    categories: List<Category>,
    onCategorySelected: (Category) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Text("选择子分类", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.heightIn(max = 300.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    CategoryGridItem(
                        category = category,
                        isSelected = false,
                        onClick = { onCategorySelected(category) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NumberPad(onKeyClick: (KeyAction) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF9FAFB))
            .padding(8.dp)
    ) {
        val layout = KeyAction.LAYOUT_ORDER
        for (row in 0..3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (col in 0..3) {
                    val key = layout.find { it.row == row && it.column == col }
                    if (key != null) {
                        NumberKey(key = key, onClick = { onKeyClick(key) })
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.NumberKey(key: KeyAction, onClick: () -> Unit) {
    val bgColor = when (key) {
        KeyAction.OK -> Color(0xFF2196F3)
        else -> Color.White
    }
    val textColor = when (key) {
        KeyAction.OK -> Color.White
        KeyAction.DATE -> Color(0xFF2196F3)
        else -> Color.Black
    }
    val displayText = when (key) {
        KeyAction.DATE -> "日期"
        KeyAction.DELETE -> "⌫"
        KeyAction.DOT -> "."
        KeyAction.OK -> "OK"
        else -> key.keyName
    }

    Box(
        modifier = Modifier
            .weight(1f)
            .padding(3.dp)
            .height(60.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayText,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}
