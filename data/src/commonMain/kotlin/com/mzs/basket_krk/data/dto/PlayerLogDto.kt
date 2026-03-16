package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.PlayerLog
import kotlinx.serialization.Serializable

@Serializable
data class PlayerLogDto(
    val id: Int,
    val opp: MatchTeamDto,
    val pts: Int,
    val stat: StatDto,
    val type: String,
    val date: String
)

fun PlayerLogDto.toDomain() = PlayerLog(
    id = id,
    opponent = opp.toDomain(),
    pts = pts,
    stat = stat.toDomain(),
    type = type,
    date = date
)
