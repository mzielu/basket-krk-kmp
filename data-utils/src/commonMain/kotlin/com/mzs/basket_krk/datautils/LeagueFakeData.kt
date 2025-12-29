package com.mzs.basket_krk.datautils

import com.mzs.basket_krk.domain.model.League

object LeagueFakeData {
    fun league(
        id: Int = 42,
        name: String = "Super League"
    ) = League(id = id, name = name)
}
