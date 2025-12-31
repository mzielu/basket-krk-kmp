package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.PageableData
import com.mzs.basket_krk.domain.model.SearchItem
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponseDto(
    val data: List<SearchResultDto>,
    val next: String? = null
)

fun SearchResponseDto.toDomain(): PageableData<SearchItem> = PageableData(
    data = data.map { it.toDomain() },
    next = next
)