package com.mzs.basket_krk.domain.model

data class Competition(
    val id: Int,
    val name: String,
    val standings: List<CompetitionStanding>
)