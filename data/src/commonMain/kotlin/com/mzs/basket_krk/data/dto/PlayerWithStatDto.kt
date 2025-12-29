package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.PlayerWithStat
import kotlinx.serialization.Serializable

@Serializable
data class PlayerWithStatDto(
    val player: PlayerShortDto,
    val stat: StatDto
)

fun PlayerWithStatDto.toDomain() = PlayerWithStat(
    player = player.toDomain(),
    stat = stat.toDomain()
)
