package com.mzs.basket_krk.datautils

import com.mzs.basket_krk.domain.model.PlayerShort

object PlayerFakeData {
    fun playerShort(
        id: Int = 42,
        name: String = "M. Zielinski"
    ) = PlayerShort(id = id, name = name)
}