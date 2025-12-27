package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.Season
import kotlinx.serialization.Serializable

@Serializable
data class SeasonDto(
    val id: Int,
    val num: Int
)

fun SeasonDto.toDomain() = Season(
    id = id,
    num = num
)