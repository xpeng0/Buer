package com.cscyxp.finance.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.cscyxp.finance.details.ui.composable.StockDetailScreenRoute
import com.cscyxp.finance.search.ui.composable.SearchScreenRoute
import com.cscyxp.finance.watchlist.ui.composable.WatchlistScreenRoute

fun NavGraphBuilder.financeNavGraph(navController: NavController) {
    composable<FinanceWatchlist> {
        WatchlistScreenRoute(
            onSearchClick = { navController.navigate(FinanceSearch) },
            onStockClick = { stockKey ->
                navController.navigate(FinanceDetail(stockKey.symbol, stockKey.exchange.name))
            }
        )
    }

    composable<FinanceDetail> {
        StockDetailScreenRoute(
            onBackClick = { navController.popBackStack() }
        )
    }

    composable<FinanceSearch> {
        SearchScreenRoute(
            onBackClick = { navController.popBackStack() },
            onStockClick = { stockKey ->
                navController.navigate(FinanceDetail(stockKey.symbol, stockKey.exchange.name))
            }
        )
    }
}

fun NavController.navigateToFinance() {
    navigate(FinanceWatchlist)
}
