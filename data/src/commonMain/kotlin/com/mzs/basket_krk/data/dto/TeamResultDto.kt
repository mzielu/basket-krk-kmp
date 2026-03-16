package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.MatchStatus
import com.mzs.basket_krk.domain.model.MatchType
import com.mzs.basket_krk.domain.model.TeamResult
import kotlinx.serialization.Serializable

@Serializable
data class TeamResultDto(
    val id: Int,
    val opp: MatchTeamDto,
    val pts: Int,
    val date: String,
    val status: String,
    val type: String
)

fun TeamResultDto.toDomain() = TeamResult(
    id = id,
    opponent = opp.toDomain(),
    points = pts,
    date = date,
    status = MatchStatus.fromKey(status),
    type = MatchType.fromKey(type)
)
