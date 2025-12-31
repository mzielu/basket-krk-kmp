package com.mzs.basket_krk.domain.model

sealed class SearchItem(open val id: Long) {
    data class Player(
        override val id: Long,
        val firstName: String,
        val lastName: String
    ) : SearchItem(id)

    data class Team(
        override val id: Long,
        val name: String,
        val logoPath: String?
    ) : SearchItem(id)
}