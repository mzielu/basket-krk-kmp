package com.mzs.basket_krk.domain.model

data class LeagueDetails(
    val id: Int,
    val name: String,
    val competitions: List<Competition>
)