package com.cscyxp.finance.watchlist.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cscyxp.finance.StockExchange
import com.cscyxp.finance.StockTrend
import com.cscyxp.finance.entity.StockKey
import com.cscyxp.finance.watchlist.ui.state.WatchlistUiState
import com.cscyxp.finance.watchlist.vm.WatchlistViewModel
import com.cscyxp.xpviews.composable.TrendLineChart
import kotlin.math.max
import kotlin.math.min



@Composable
fun StockWatchlistScreen(
    modifier: Modifier = Modifier,
    uiStates: List<WatchlistUiState>,
    selectedPeriod: Int = 30,
    onPeriodSelected: (Int) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onStockClick: (WatchlistUiState) -> Unit = {},
    listState: LazyListState = rememberLazyListState(),
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .systemBarsPadding()
    ) {
        Text(
            text = "Watchlist",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(20.dp))

        SearchBar(onClick = onSearchClick)

        Spacer(modifier = Modifier.height(12.dp))

        PeriodSelector(
            selectedPeriod = selectedPeriod,
            onPeriodSelected = onPeriodSelected
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = uiStates,
                key = { it.stockKey }
            ) { state ->
                StockListItem(
                    state = state,
                    onClick = { onStockClick(state) }
                )
                HorizontalDivider(
                    color = Color(0xFFF5F5F5),
                    thickness = 2.dp
                )
            }
        }
    }
}

@Composable
fun WatchlistScreenRoute(
    onSearchClick: () -> Unit,
    onStockClick: (StockKey) -> Unit,
    viewModel: WatchlistViewModel = hiltViewModel(),
) {
    val uiStates by viewModel.watchlistUiStates.collectAsState()
    val listState = rememberLazyListState()
    val currentStates by rememberUpdatedState(uiStates)

    LaunchedEffect(listState) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo to currentStates
        }.collect { (visibleItems, states) ->
            if (visibleItems.isEmpty() || states.isEmpty()) return@collect
            val firstVisible = visibleItems.first().index
            val lastVisible = visibleItems.last().index
            val totalCount = states.size

            val redZoneKeys = states.subList(firstVisible, lastVisible + 1).map { it.stockKey }.toSet()
            val firstNearby = max(0, firstVisible - 10)
            val lastNearby = min(totalCount - 1, lastVisible + 10)
            val blueZoneKeys = states.subList(firstNearby, lastNearby + 1).map { it.stockKey }.toSet() - redZoneKeys

            viewModel.updateVisibleWatchStock(redZoneKeys)
            viewModel.updateNearbyWatchStock(blueZoneKeys)
        }
    }

    StockWatchlistScreen(
        uiStates = uiStates,
        listState = listState,
        onSearchClick = onSearchClick,
        onStockClick = { state -> onStockClick(state.stockKey) }
    )
}

@Preview(showBackground = true)
@Composable
private fun StockWatchlistScreenPreview() {
    val mockMinutes = listOf(
        100.0, 101.5, 103.2, 102.8, 104.1, 105.0, 103.7, 106.2, 107.5, 106.8,
        108.3, 109.1, 107.6, 110.2, 111.5, 113.0, 114.2, 115.8, 117.3, 116.5,
        119.0, 120.5, 122.1, 124.3, 126.7, 128.9, 127.4, 130.2, 132.8, 135.0,
        137.5, 136.2, 138.8, 140.1, 142.6, 145.3, 148.0, 150.5, 153.2, 156.8
    )

    val mockStates = listOf(
        WatchlistUiState.Success(
            stockKey = StockKey("AAPL", StockExchange.SHANG_HAI),
            stockName = "Apple Inc.",
            currentPrice = "185.32",
            todayPercent = "+3.67%",
            todayTrend = StockTrend.UP,
            high = 190.5,
            low = 180.0,
            minutes = mockMinutes
        ),
        WatchlistUiState.Loading(
            stockKey = StockKey("GOOGL", StockExchange.SHEN_ZHEN),
            stockName = "Alphabet Inc."
        ),
        WatchlistUiState.Success(
            stockKey = StockKey("TSLA", StockExchange.SHANG_HAI),
            stockName = "Tesla Inc.",
            currentPrice = "245.10",
            todayPercent = "-1.23%",
            todayTrend = StockTrend.DOWN,
            high = 250.0,
            low = 240.0,
            minutes = mockMinutes.map { it - 20.0 }
        ),
        WatchlistUiState.Success(
            stockKey = StockKey("000001", StockExchange.SHEN_ZHEN),
            stockName = "平安银行",
            currentPrice = "12.85",
            todayPercent = "0.00%",
            todayTrend = StockTrend.FLAT,
            high = 13.2,
            low = 12.5,
            minutes = mockMinutes.map { it / 10.0 }
        )
    )

    StockWatchlistScreen(
        uiStates = mockStates,
        selectedPeriod = 30
    )
}

@Preview(showBackground = true)
@Composable
private fun SearchBarPreview() {
    SearchBar(onClick = {})
}

@Preview(showBackground = true)
@Composable
private fun PeriodChipPreview() {
    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PeriodChip(text = "7D", isSelected = true, onClick = {})
        PeriodChip(text = "30D", isSelected = false, onClick = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun StockListItemPreview() {
    StockListItem(
        state = WatchlistUiState.Success(
            stockKey = StockKey("TELA", StockExchange.SHANG_HAI),
            stockName = "TELA Bio",
            currentPrice = "185.32",
            todayPercent = "+3.67%",
            todayTrend = StockTrend.UP,
            high = 190.5,
            low = 180.0,
            minutes = listOf(
                100.0, 101.5, 103.2, 102.8, 104.1, 105.0, 103.7, 106.2, 107.5, 106.8,
                108.3, 109.1, 107.6, 110.2, 111.5, 113.0, 114.2, 115.8, 117.3, 116.5,
                119.0, 120.5, 122.1, 124.3, 126.7, 128.9, 127.4, 130.2, 132.8, 135.0,
                137.5, 136.2, 138.8, 140.1, 142.6, 145.3, 148.0, 150.5, 153.2, 156.8
            )
        ),
        onClick = {}
    )
}

@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(Color(0xFFEBEBF0), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color(0xFFA0A0A5),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Search stocks...",
                color = Color(0xFFA0A0A5),
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun PeriodSelector(
    modifier: Modifier = Modifier,
    selectedPeriod: Int,
    onPeriodSelected: (Int) -> Unit,
) {
    Row(modifier = modifier) {
        PeriodChip(
            text = "7D",
            isSelected = selectedPeriod == 7,
            onClick = { onPeriodSelected(7) }
        )
        Spacer(modifier = Modifier.width(12.dp))
        PeriodChip(
            text = "30D",
            isSelected = selectedPeriod == 30,
            onClick = { onPeriodSelected(30) }
        )
    }
}

@Composable
private fun PeriodChip(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) Color.Black else Color(0xFFEBEBF0))
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            color = if (isSelected) Color.White else Color.Black
        )
    }
}

@Composable
private fun StockListItem(
    modifier: Modifier = Modifier,
    state: WatchlistUiState,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 10.dp)
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.width(80.dp)
        ) {
            Text(
                text = state.stockName,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = state.stockKey.symbol,
                fontSize = 10.sp,
                color = Color(0xFF9E9E9E),
                maxLines = 1
            )
        }

        if (state is WatchlistUiState.Success) {
            TrendLineChart(
                modifier = Modifier
                    .width(180.dp)
                    .height(35.dp)
                    .padding(start = 5.dp),
                minutes = state.minutes
                    .filterIndexed { idx, _ ->
                        idx % 3 == 0 || idx == state.minutes.lastIndex
                    }
                    .map { it.toFloat() },
                totalPoints = 80
            )
        } else {
            Spacer(
                modifier = Modifier
                    .width(180.dp)
                    .height(30.dp)
                    .padding(start = 5.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        if (state is WatchlistUiState.Success) {
            val percentColor = when (state.todayTrend) {
                StockTrend.UP -> Color(0xFFDC4848)
                StockTrend.DOWN -> Color(0xFF72DC48)
                StockTrend.FLAT -> Color(0xFFA0A0A5)
            }
            Text(
                text = state.todayPercent,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = percentColor,
                textAlign = TextAlign.End,
                modifier = Modifier.padding(start = 10.dp)
            )
        }
    }
}
