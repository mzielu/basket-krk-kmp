package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.PlayerWithStat
import kotlinx.serialization.Serializable

@Serializable
data class TeamRosterDto(val data: List<PlayerWithStatDto>)

fun TeamRosterDto.toDomain(): List<PlayerWithStat> = data.map { it.toDomain() }
