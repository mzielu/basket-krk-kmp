package com.mzs.basket_krk.domain.model

data class CompetitionStanding(
    val team: SearchItem.Team,
    val pos: Int,
    val w: Int,
    val l: Int,
    val wo: Int,
    val p: Int,
    val m: Int
)