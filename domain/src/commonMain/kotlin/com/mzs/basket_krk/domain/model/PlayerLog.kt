package com.mzs.basket_krk.domain.model

data class PlayerLog(
    val id: Int,
    val opponent: MatchTeam,
    val pts: Int,
    val stat: Stat,
    val type: String,
    val date: String
)
