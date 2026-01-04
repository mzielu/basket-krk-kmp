package com.mzs.basket_krk.domain.model

data class LeagueLeader(
    val player: SearchItem.Player,
    val team: SearchItem.Team,
    val value: Double,
    val position: Int,
    val games: Int,
    val made: Int? = null,
    val ats: Int? = null
)