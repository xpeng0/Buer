package com.cscyxp.fitness.navigation

import kotlinx.serialization.Serializable

@Serializable
internal object FitnessHome

@Serializable
internal object FitnessCreateTemplate

@Serializable
internal object FitnessAddExercise

@Serializable
internal data class FitnessActiveWorkout(val templateName: String)
