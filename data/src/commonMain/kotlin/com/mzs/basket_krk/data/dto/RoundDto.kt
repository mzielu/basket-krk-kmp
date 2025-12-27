package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.Round
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class RoundDto(
    val id: Int,
    val name: String,
    val date: String
)

fun RoundDto.toDomain(): Round {
    return Round(
        id = id,
        name = name,
        date = LocalDate.parse(date)
    )
}