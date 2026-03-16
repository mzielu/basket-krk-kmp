package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.PlayerShort
import com.mzs.basket_krk.domain.model.TeamRecord
import kotlinx.serialization.Serializable

@Serializable
data class TeamRecordDto(
    val player: PlayerInRecordDto,
    val value: Int,
    val position: Int,
    val games: Int,
    val ats: Int? = null,
    val s_num: Int? = null,
    val match_id: Int? = null
)

@Serializable
data class PlayerInRecordDto(
    val id: Int,
    val fn: String,
    val ln: String,
    val t: TeamDto? = null
)

fun TeamRecordDto.toDomain() = TeamRecord(
    player = PlayerShort(id = player.id, name = "${player.fn} ${player.ln}"),
    value = value,
    position = position,
    games = games,
    ats = ats,
    sNum = s_num,
    matchId = match_id
)
