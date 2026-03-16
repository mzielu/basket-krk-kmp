package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.TeamResultList
import kotlinx.serialization.Serializable

@Serializable
data class TeamResultListDto(
    val data: List<TeamResultDto>,
    val lg: LeagueDto
)

fun TeamResultListDto.toDomain() = TeamResultList(
    data = data.map { it.toDomain() },
    league = lg.toDomain()
)
