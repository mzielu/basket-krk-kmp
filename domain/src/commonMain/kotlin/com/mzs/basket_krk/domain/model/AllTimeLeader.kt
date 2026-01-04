package com.mzs.basket_krk.domain.model

data class AllTimeLeader(
    val player: SearchItem.Player,
    val team: SearchItem.Team,
    val value: Int,
    val position: Int,
    val inf: String? = null
)