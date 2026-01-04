package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.Competition
import kotlinx.serialization.Serializable

@Serializable
data class CompetitionDto(
    val id: Int,
    val name: String,
    val data: List<CompetitionStandingDto>
)

fun CompetitionDto.toDomain() = Competition(
    id = id,
    name = name,
    standings = data.map { it.toDomain() }
)