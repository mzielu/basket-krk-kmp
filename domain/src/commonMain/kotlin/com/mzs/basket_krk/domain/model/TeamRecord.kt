package com.mzs.basket_krk.domain.model

data class TeamRecord(
    val player: PlayerShort,
    val value: Int,
    val position: Int,
    val games: Int,
    val ats: Int?,
    val sNum: Int?,
    val matchId: Int?
)
