package com.cscyxp.buer.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cscyxp.bookkeeping.navigation.bookkeepingNavGraph
import com.cscyxp.bookkeeping.navigation.navigateToBookkeeping
import com.cscyxp.buer.home.ui.BuerHomeScreen
import com.cscyxp.finance.navigation.financeNavGraph
import com.cscyxp.finance.navigation.navigateToFinance
import com.cscyxp.fitness.navigation.fitnessNavGraph
import com.cscyxp.fitness.navigation.navigateToFitness

@Composable
fun BuerApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Home,
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(400)) },
        exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(400)) },
        popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(400)) },
        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(400)) }
    ) {
        composable<Home> {
            BuerHomeScreen(
                onFinanceClick = { navController.navigateToFinance() },
                onBookkeepingClick = { navController.navigateToBookkeeping() },
                onFitnessClick = { navController.navigateToFitness() }
            )
        }

        bookkeepingNavGraph(navController)

        financeNavGraph(navController)

        fitnessNavGraph(navController)
    }
}
