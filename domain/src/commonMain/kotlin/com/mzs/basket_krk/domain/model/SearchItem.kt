package com.mzs.basket_krk.domain.model

sealed class SearchItem(open val id: Int) {
    data class Player(
        override val id: Int,
        val firstName: String,
        val lastName: String
    ) : SearchItem(id)

    data class Team(
        override val id: Int,
        val name: String,
        val logoPath: String?
    ) : SearchItem(id)
}