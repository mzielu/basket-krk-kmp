package com.mzs.basket_krk.domain.model

data class MatchTeam(
    val id: Int,
    val name: String,
    val shortName: String,
    val logoUrl: String,
    val points: Int
)