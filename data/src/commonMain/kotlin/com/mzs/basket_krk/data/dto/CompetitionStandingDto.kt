package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.CompetitionStanding
import com.mzs.basket_krk.domain.model.SearchItem
import kotlinx.serialization.Serializable

@Serializable
data class CompetitionStandingDto(
    val team: TeamDto,
    val pos: Int,
    val w: Int,
    val l: Int,
    val wo: Int,
    val p: Int,
    val m: Int
)

fun CompetitionStandingDto.toDomain() = CompetitionStanding(
    team = team.toDomain(),
    pos = pos,
    w = w,
    l = l,
    wo = wo,
    p = p,
    m = m
)