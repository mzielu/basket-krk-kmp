package com.mzs.basket_krk.datautils

import com.mzs.basket_krk.domain.model.Round
import com.mzs.basket_krk.domain.model.SearchItem
import com.mzs.basket_krk.domain.model.Season
import kotlinx.datetime.LocalDate

object SeasonFakeData {
    fun season(
        id: Int = 42,
        num: Int = 69
    ) = Season(id = id, num = num)

    fun round(
        id: Int = 42,
        name: String = "some round",
        date: LocalDate = LocalDate(2024, 6, 14)
    ): Round = Round(id = id, name = name, date = date)
}
