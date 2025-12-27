package com.mzs.basket_krk.domain.model

import kotlinx.datetime.LocalDate

data class Match(
    val id: Int,
    val team1: MatchTeam,
    val team2: MatchTeam,
    val date: LocalDate,
    val time: String,
    val type: MatchType,
    val status: MatchStatus,
    val description: String?,
    val arena: String?,
    val league: League?
)