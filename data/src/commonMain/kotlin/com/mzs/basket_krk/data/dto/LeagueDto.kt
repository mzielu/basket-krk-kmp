package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.League
import kotlinx.serialization.Serializable

@Serializable
data class LeagueDto(
    val id: Int,
    val name: String
)

fun LeagueDto.toDomain() = League(
    id = id,
    name = name
)