package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.AllTimeLeader
import com.mzs.basket_krk.domain.model.PageableData
import kotlinx.serialization.Serializable

@Serializable
data class AllTimeResponseDto(
    val data: List<AllTimeLeaderDto>,
    val next: String? = null
)

fun AllTimeResponseDto.toDomain(): PageableData<AllTimeLeader> = PageableData(
    data = data.map { it.toDomain() },
    next = next
)