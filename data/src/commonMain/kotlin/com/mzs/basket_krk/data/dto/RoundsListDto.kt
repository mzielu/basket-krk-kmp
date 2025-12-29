package com.mzs.basket_krk.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RoundsListDto(
    val data: List<RoundDto>
)

fun RoundsListDto.toDomain() = data.map { it.toDomain() }