package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.LeaguesInfo
import kotlinx.serialization.Serializable

@Serializable
data class LeaguesInfoDto(
    val seasons: List<SeasonDto>,
    val leagues: List<LeagueDto>
)

fun LeaguesInfoDto.toDomain() = LeaguesInfo(
    seasons = seasons.map { it.toDomain() },
    leagues = leagues.map { it.toDomain() }
)