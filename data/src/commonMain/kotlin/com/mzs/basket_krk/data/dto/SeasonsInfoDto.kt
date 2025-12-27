package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.SeasonsInfo
import kotlinx.serialization.Serializable

@Serializable
data class SeasonsInfoDto(
    val seasons: List<SeasonDto>,
    val rounds: List<RoundDto>
)

fun SeasonsInfoDto.toDomain() = SeasonsInfo(
    seasons = seasons.map { it.toDomain() },
    rounds = rounds.map { it.toDomain() }
)