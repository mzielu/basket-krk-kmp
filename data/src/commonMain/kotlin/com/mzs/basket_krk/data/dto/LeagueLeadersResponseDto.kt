package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.LeagueLeader
import kotlinx.serialization.Serializable

@Serializable
data class LeagueLeadersResponseDto(
    val data: List<LeagueLeaderDto>
)

fun LeagueLeadersResponseDto.toDomain(): List<LeagueLeader> = data.map { it.toDomain() }