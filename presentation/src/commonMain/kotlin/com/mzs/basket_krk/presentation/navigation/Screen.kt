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

    @Serializable
    data object Standings : Screen()

    @Serializable
    data class PlayerDetails(val playerId: Int) : Screen()

    @Serializable
    data class TeamDetails(val teamId: Int) : Screen()
}
