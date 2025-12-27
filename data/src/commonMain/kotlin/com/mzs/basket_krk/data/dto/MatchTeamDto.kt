package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.MatchTeam
import kotlinx.serialization.Serializable

@Serializable
data class MatchTeamDto(
    val id: Int,
    val name: String,
    val s_name: String,
    val logo: String,
    val pts: Int
)

fun MatchTeamDto.toDomain() = MatchTeam(
    id = id,
    name = name,
    shortName = s_name,
    logoUrl = logo,
    points = pts
)