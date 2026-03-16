package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.PlayerStat
import kotlinx.serialization.Serializable

@Serializable
data class PlayerStatListDto(val data: List<PlayerStatDto>)

fun PlayerStatListDto.toDomain(): List<PlayerStat> = data.map { it.toDomain() }
