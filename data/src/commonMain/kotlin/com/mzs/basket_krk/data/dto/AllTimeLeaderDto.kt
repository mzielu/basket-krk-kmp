package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.AllTimeLeader
import com.mzs.basket_krk.domain.model.SearchItem
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
    player = SearchResultDto.Player(data = player).toDomain() as SearchItem.Player,
    team = SearchResultDto.Team(data = team).toDomain() as SearchItem.Team,
    value = value,
    position = position,
    inf = inf
)