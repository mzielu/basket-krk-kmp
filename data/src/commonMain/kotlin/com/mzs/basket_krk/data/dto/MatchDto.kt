package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.Match
import com.mzs.basket_krk.domain.model.MatchStatus
import com.mzs.basket_krk.domain.model.MatchType
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class MatchDto(
    val id: Int,
    val date: String,
    val time: String,
    val t1: MatchTeamDto,
    val t2: MatchTeamDto,
    val type: String,
    val status: String,
    val desc: String? = null,
    val arena: String? = null,
    val lg: LeagueDto? = null
)

fun MatchDto.toDomain() = Match(
    id = id,
    date = LocalDate.parse(date),
    time = time,
    team1 = t1.toDomain(),
    team2 = t2.toDomain(),
    type = MatchType.fromKey(type),
    status = MatchStatus.fromKey(status),
    description = desc,
    arena = arena,
    league = lg?.toDomain()
)