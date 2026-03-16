package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.PlayerLogByTeam
import kotlinx.serialization.Serializable

@Serializable
data class PlayerLogByTeamDto(val t: TeamDto, val logs: List<PlayerLogDto>)

fun PlayerLogByTeamDto.toDomain() = PlayerLogByTeam(
    team = t.toTeam(),
    logs = logs.map { it.toDomain() }
)
