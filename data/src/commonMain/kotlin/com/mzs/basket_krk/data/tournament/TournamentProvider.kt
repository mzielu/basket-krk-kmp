package com.mzs.basket_krk.data.tournament

import com.russhwolf.settings.Settings

private const val TOURNAMENT_KEY = "trnmt_key"
private const val DEFAULT_TOURNAMENT = "mba"

class TournamentProvider {
    private val settings = Settings()

    fun getCurrentKey(): String =
        settings.getStringOrNull(TOURNAMENT_KEY) ?: DEFAULT_TOURNAMENT

    fun setCurrentKey(key: String) {
        settings.putString(TOURNAMENT_KEY, key)
    }
}
