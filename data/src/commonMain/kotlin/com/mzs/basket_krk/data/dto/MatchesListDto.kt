package com.mzs.basket_krk.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MatchesListDto(
    val data: List<MatchDto>,
    val next: String? = null
)

fun MatchesListDto.toDomain() = data.map { it.toDomain() }