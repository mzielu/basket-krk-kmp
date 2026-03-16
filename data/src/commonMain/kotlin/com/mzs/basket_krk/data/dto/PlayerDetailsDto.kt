package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.PlayerDetails
import kotlinx.serialization.Serializable

@Serializable
data class PlayerDetailsDto(
    val id: Int,
    val fn: String,
    val ln: String,
    val seasons: List<SeasonDto>,
    val t: TeamDto? = null
)

fun PlayerDetailsDto.toDomain() = PlayerDetails(
    id = id,
    firstName = fn,
    lastName = ln,
    seasons = seasons.map { it.toDomain() },
    team = t?.toTeam()
)
