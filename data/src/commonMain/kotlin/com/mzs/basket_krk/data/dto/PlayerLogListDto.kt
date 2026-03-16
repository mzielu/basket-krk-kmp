package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.PlayerLogList
import kotlinx.serialization.Serializable

@Serializable
data class PlayerLogListDto(val data: List<PlayerLogByTeamDto>)

fun PlayerLogListDto.toDomain() = PlayerLogList(data = data.map { it.toDomain() })
