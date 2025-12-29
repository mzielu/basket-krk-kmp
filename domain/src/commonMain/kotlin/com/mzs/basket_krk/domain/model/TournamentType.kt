package com.mzs.basket_krk.domain.model

enum class TournamentType(val key: String) {
    MBA("mba"),
    WMBA("wmba"),
    KNBA("knba");

    companion object {
        fun fromKey(keyName: String): TournamentType =
            TournamentType.entries.find { it.key == keyName }
                ?: throw IllegalArgumentException("Unknown tournament type: $keyName")
    }
}