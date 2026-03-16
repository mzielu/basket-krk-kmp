package com.mzs.basket_krk.domain.model

data class PlayerStat(
    val season: Int,
    val team: Team,
    val league: League,
    val stat: Stat
)
