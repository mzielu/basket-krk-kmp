package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.PlayerStat
import kotlinx.serialization.Serializable

@Serializable
data class PlayerStatDto(
    val s: Int,
    val t: TeamDto,
    val lg: LeagueDto,
    val stat: StatDto
)

fun PlayerStatDto.toDomain() = PlayerStat(
    season = s,
    team = t.toTeam(),
    league = lg.toDomain(),
    stat = stat.toDomain()
)
