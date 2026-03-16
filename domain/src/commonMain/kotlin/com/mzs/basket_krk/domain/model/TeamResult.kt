package com.mzs.basket_krk.domain.model

data class TeamResult(
    val id: Int,
    val opponent: MatchTeam,
    val points: Int,
    val date: String,
    val status: MatchStatus,
    val type: MatchType
)
