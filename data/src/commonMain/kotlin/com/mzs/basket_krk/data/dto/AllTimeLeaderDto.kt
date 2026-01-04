package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.AllTimeLeader
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AllTimeLeaderDto(
    val player: PlayerDto,
    @SerialName("cur_team") val team: TeamDto,
    val value: Int,
    val position: Int,
    val inf: String? = null
)

fun AllTimeLeaderDto.toDomain() = AllTimeLeader(
    player = player.toDomain(),
    team = team.toDomain(),
    value = value,
    position = position,
    inf = inf
)