package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.MatchDetailsTeam
import com.mzs.basket_krk.domain.model.PlayerWithStat
import kotlinx.serialization.Serializable

@Serializable
data class MatchDetailsTeamDto(
    val id: Int,
    val name: String,
    val s_name: String,
    val logo: String,
    val pts: Int,
    val stats: List<PlayerWithStatDto>? = null
)

fun MatchDetailsTeamDto.toDomain() = MatchDetailsTeam(
    id = id,
    name = name,
    shortName = s_name,
    logo = logo,
    points = pts,
    stats = stats?.map { it.toDomain() }
)
