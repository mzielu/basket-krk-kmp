package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.LeagueLeader
import kotlinx.serialization.Serializable

@Serializable
data class LeagueLeaderDto(
    val player: PlayerDto,
    val team: TeamDto,
    val value: Double,
    val position: Int,
    val games: Int,
    val made: Int? = null,
    val ats: Int? = null
)

fun LeagueLeaderDto.toDomain() = LeagueLeader(
    player = player.toDomain(),
    team = team.toDomain(),
    value = value,
    position = position,
    games = games,
    made = made,
    ats = ats
)