package com.cscyxp.bookkeeping.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.cscyxp.bookkeeping.R
import com.cscyxp.bookkeeping.ui.AddTransactionScreen
import com.cscyxp.bookkeeping.ui.CategoryChartScreen
import com.cscyxp.bookkeeping.ui.ChartScreen
import com.cscyxp.bookkeeping.ui.TransactionListScreen

private enum class BookkeepingTab(val label: String, val icon: Int) {
    List("账单", R.drawable.wallet),
    Chart("统计", R.drawable.rounded_bar_chart_24),
}

fun NavGraphBuilder.bookkeepingNavGraph(navController: NavController) {
    composable<BookkeepingHome> {
        BookkeepingNavHost(
            onBackToHome = { navController.popBackStack() },
            onAddClick = { navController.navigate(BookkeepingAdd) },
            onCategoryChartClick = { categoryId ->
                navController.navigate(BookkeepingCategoryChart(categoryId))
            }
        )
    }

    composable<BookkeepingAdd> {
        AddTransactionScreen(
            onBackClick = { navController.popBackStack() }
        )
    }

    composable<BookkeepingCategoryChart> { backStackEntry ->
        val route = backStackEntry.toRoute<BookkeepingCategoryChart>()
        CategoryChartScreen(
            categoryId = route.categoryId,
            onBackClick = { navController.popBackStack() }
        )
    }
}

fun NavController.navigateToBookkeeping() {
    navigate(BookkeepingHome) {
        launchSingleTop = true
    }
}

@Composable
internal fun BookkeepingNavHost(
    onBackToHome: () -> Unit,
    onAddClick: () -> Unit,
    onCategoryChartClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                BookkeepingTab.entries.forEach { tab ->
                    val selected = when (tab) {
                        BookkeepingTab.List -> currentDestination?.hasRoute<BookkeepingList>() == true
                        BookkeepingTab.Chart -> currentDestination?.hasRoute<BookkeepingChart>() == true
                    }
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(tab.toRoute()) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(painterResource(tab.icon), contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BookkeepingList,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
        ) {
            composable<BookkeepingList> {
                BackHandler { onBackToHome() }
                TransactionListScreen(
                    onAddClick = onAddClick,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable<BookkeepingChart> {
                BackHandler { onBackToHome() }
                ChartScreen(
                    onCategoryClick = onCategoryChartClick,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

private fun BookkeepingTab.toRoute(): Any = when (this) {
    BookkeepingTab.List -> BookkeepingList
    BookkeepingTab.Chart -> BookkeepingChart
}
