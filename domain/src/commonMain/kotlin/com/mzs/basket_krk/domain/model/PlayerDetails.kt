package com.mzs.basket_krk.domain.model

data class PlayerDetails(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val seasons: List<Season>,
    val team: Team?
)
