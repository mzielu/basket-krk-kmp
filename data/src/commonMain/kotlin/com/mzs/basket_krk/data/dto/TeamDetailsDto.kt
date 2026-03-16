package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.TeamDetails
import kotlinx.serialization.Serializable

@Serializable
data class TeamDetailsDto(
    val id: Int,
    val name: String,
    val logo: String,
    val seasons: List<SeasonDto>,
    val last_league: LeagueDto? = null
)

fun TeamDetailsDto.toDomain() = TeamDetails(
    id = id,
    name = name,
    logoUrl = logo,
    seasons = seasons.map { it.toDomain() },
    league = last_league?.toDomain()
)
