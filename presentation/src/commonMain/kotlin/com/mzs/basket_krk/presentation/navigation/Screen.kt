package com.mzs.basket_krk.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object Main : Screen()

    @Serializable
    data object Settings : Screen()

    @Serializable
    data class MatchDetails(val matchId: Int) : Screen()

    @Serializable
    data object AllTimeLeaders : Screen()
}
