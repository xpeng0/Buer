package com.cscyxp.fitness.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.cscyxp.fitness.exercise.ui.AddExerciseRoute
import com.cscyxp.fitness.home.ui.FitnessRoute
import com.cscyxp.fitness.template.ui.CreateTemplateRoute
import com.cscyxp.fitness.workout.ui.ActiveWorkoutRoute

fun NavGraphBuilder.fitnessNavGraph(navController: NavController) {
    composable<FitnessHome> {
        FitnessRoute(
            onBackClick = { navController.popBackStack() },
            onCreateTemplateClick = { navController.navigate(FitnessCreateTemplate) },
            onAddExerciseClick = { navController.navigate(FitnessAddExercise) },
            onStartWorkoutClick = { navController.navigate(FitnessActiveWorkout(it)) },
        )
    }

    composable<FitnessCreateTemplate> {
        CreateTemplateRoute(
            onBackClick = { navController.popBackStack() },
            onAddExerciseClick = { navController.navigate(FitnessAddExercise) },
        )
    }

    composable<FitnessAddExercise> {
        AddExerciseRoute(
            onBackClick = { navController.popBackStack() },
        )
    }

    composable<FitnessActiveWorkout> {
        ActiveWorkoutRoute(
            onCloseClick = { navController.popBackStack() },
        )
    }
}

fun NavController.navigateToFitness() {
    navigate(FitnessHome) {
        launchSingleTop = true
    }
}
