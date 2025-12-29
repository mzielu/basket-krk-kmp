package com.mzs.basket_krk.domain.model

data class MatchDetailsTeam(
    val id: Int,
    val name: String,
    val shortName: String,
    val logo: String,
    val points: Int,
    val stats: List<PlayerWithStat>? = null
)
