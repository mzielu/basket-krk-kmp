package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.PlayerShort
import kotlinx.serialization.Serializable

@Serializable
data class PlayerShortDto(
    val id: Int,
    val name: String
)

fun PlayerShortDto.toDomain() = PlayerShort(
    id = id,
    name = name
)
