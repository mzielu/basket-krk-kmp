package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.LeagueDetails
import kotlinx.serialization.Serializable

@Serializable
data class LeagueDetailsDto(
    val id: Int,
    val name: String,
    val competitions: List<CompetitionDto>
)

fun LeagueDetailsDto.toDomain() = LeagueDetails(
    id = id,
    name = name,
    competitions = competitions.map { it.toDomain() }
)
