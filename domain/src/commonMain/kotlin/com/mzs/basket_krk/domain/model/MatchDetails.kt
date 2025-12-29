package com.mzs.basket_krk.domain.model

data class MatchDetails(
    val id: Int,
    val idTmnt: Int,
    val tournament: String,
    val type: String,
    val status: String,
    val date: String,
    val time: String,
    val t1: MatchDetailsTeam,
    val t2: MatchDetailsTeam,
    val qtrs: String? = null,
    val desc: String? = null,
    val arena: String? = null,
    val lg: League? = null
)
