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
    val qtrs: List<String> = emptyList(),
    val description: String? = null,
    val arena: String? = null,
    val league: League? = null
) {
    val statsEmpty: Boolean
        get() = t1.stats.isNullOrEmpty() && t2.stats.isNullOrEmpty()
}
