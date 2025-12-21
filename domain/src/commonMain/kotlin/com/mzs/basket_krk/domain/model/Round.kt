package com.mzs.basket_krk.domain.model

import kotlinx.datetime.LocalDate

data class Round(
    val id: Int,
    val name: String,
    val date: LocalDate
)