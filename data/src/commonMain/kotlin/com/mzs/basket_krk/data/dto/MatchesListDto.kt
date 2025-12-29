package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.Match
import com.mzs.basket_krk.domain.model.PageableData
import kotlinx.serialization.Serializable

@Serializable
data class MatchesListDto(
    val data: List<MatchDto>,
    val next: String? = null
)

fun MatchesListDto.toDomain(): PageableData<Match> {
    return PageableData(
        data = data.map { it.toDomain() },
        next = next
    )
}