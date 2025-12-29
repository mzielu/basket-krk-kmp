package com.mzs.basket_krk.domain.model

import kotlinx.datetime.LocalDate

data class MatchDetails(
    val id: Int,
    val idTmnt: Int,
    val tournament: TournamentType,
    val type: MatchType,
    val status: MatchStatus,
    val date: LocalDate,
    val time: String,
    val t1: MatchDetailsTeam,
    val t2: MatchDetailsTeam,
    val qtrs: String? = null,
    val description: String? = null,
    val arena: String? = null,
    val league: League? = null
)
