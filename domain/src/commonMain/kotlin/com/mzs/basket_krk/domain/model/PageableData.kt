package com.mzs.basket_krk.domain.model

data class PageableData<T>(
    val data: List<T>,
    val next: String?
) {
    companion object {
        fun <T> empty(): PageableData<T> {
            return PageableData(
                data = emptyList(),
                next = null
            )
        }
    }
}