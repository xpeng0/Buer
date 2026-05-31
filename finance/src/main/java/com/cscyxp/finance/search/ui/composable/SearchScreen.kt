package com.cscyxp.finance.search.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cscyxp.finance.R
import com.cscyxp.finance.entity.StockKey
import com.cscyxp.finance.search.ui.state.SearchResultState
import com.cscyxp.finance.search.ui.state.SearchScreenUiState
import com.cscyxp.finance.search.ui.state.StockSearchItemUiState
import com.cscyxp.finance.search.ui.state.StockTag
import com.cscyxp.finance.search.vm.SearchViewModel

@Composable
fun SearchScreenRoute(
    onBackClick: () -> Unit,
    onStockClick: (StockKey) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val screenState by viewModel.searchScreenState.collectAsState(
        initial = SearchScreenUiState.SearchBoard()
    )
    val inputText by viewModel.inputFlow.collectAsState(initial = "")

    SearchScreen(
        screenState = screenState,
        inputText = inputText,
        onInputChange = viewModel::changeSearchInput,
        onToggleWatch = viewModel::watchStock,
        onBackClick = onBackClick,
        onStockClick = onStockClick
    )
}

@Composable
private fun SearchScreen(
    screenState: SearchScreenUiState,
    inputText: String,
    onInputChange: (String) -> Unit,
    onToggleWatch: (Boolean, StockKey, String) -> Unit,
    onBackClick: () -> Unit,
    onStockClick: (StockKey) -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
    ) {
        SearchTopBar(onBackClick = onBackClick)

        OutlinedTextField(
            value = inputText,
            onValueChange = onInputChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .focusRequester(focusRequester),
            placeholder = { Text("搜索股票代码或名称") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2962FF),
                unfocusedBorderColor = Color(0xFFE0E0E0)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        when (val state = screenState) {
            is SearchScreenUiState.SearchBoard -> {
                // TODO: 显示搜索历史 / 热门搜索
            }
            is SearchScreenUiState.SearchResult -> {
                when (val result = state.resultState) {
                    is SearchResultState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is SearchResultState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(result.message, color = Color.Gray)
                        }
                    }
                    is SearchResultState.Success -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(
                                items = result.stockSearchItems,
                                key = { it.stockKey.toString() }
                            ) { item ->
                                SearchResultItem(
                                    item = item,
                                    onClick = { onStockClick(item.stockKey) },
                                    onToggle = { add ->
                                        onToggleWatch(add, item.stockKey, item.stockName)
                                    }
                                )
                                HorizontalDivider(
                                    color = Color(0xFFF5F5F5),
                                    thickness = 1.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "返回",
                tint = Color.Black
            )
        }
        Text(
            text = "搜索股票",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
private fun SearchResultItem(
    item: StockSearchItemUiState,
    onClick: () -> Unit,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.stockName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.stockKey.symbol,
                    fontSize = 12.sp,
                    color = Color(0xFF9E9E9E)
                )
                Spacer(modifier = Modifier.width(8.dp))
                StockTagChip(tag = item.stockTag)
            }
        }

        IconButton(
            onClick = { onToggle(!item.isWatched) },
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    if (item.isWatched) Color.Black
                    else Color(0xFFF0F0F0)
                )
        ) {
            val iconRes = if (item.isWatched) R.drawable.ic_remove_round_24
            else R.drawable.ic_add_round_24
            val tint = if (item.isWatched) Color.White else Color.Black
            Icon(
                painter = androidx.compose.ui.res.painterResource(iconRes),
                contentDescription = if (item.isWatched) "移除自选" else "添加自选",
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun StockTagChip(tag: StockTag) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(colorResource(tag.bgColorRes))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = tag.displayName,
            fontSize = 10.sp,
            color = colorResource(tag.textColorRes)
        )
    }
}
