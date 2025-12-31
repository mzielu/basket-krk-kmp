package com.mzs.basket_krk.datautils

import com.mzs.basket_krk.domain.model.SearchItem

object SearchFakeData {
    fun searchItemPlayer(
        id: Int = 1,
        firstName: String = "John",
        lastName: String = "Doe"
    ) = SearchItem.Player(
        id = id,
        firstName = firstName,
        lastName = lastName
    )

    fun searchItemTeam(
        id: Int = 1,
        name: String = "John",
        logo: String? = null
    ) = SearchItem.Team(
        id = id,
        name = name,
        logoPath = logo
    )
}
