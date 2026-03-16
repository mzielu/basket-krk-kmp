package com.mzs.basket_krk.domain.model

data class TeamDetails(
    val id: Int,
    val name: String,
    val logoUrl: String,
    val seasons: List<Season>,
    val league: League?
)
