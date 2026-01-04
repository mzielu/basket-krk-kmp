package com.mzs.basket_krk.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LeagueListDto(
    val data: List<LeagueDto>
)

fun LeagueListDto.toDomain() = data.map { it.toDomain() }