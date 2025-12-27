package com.mzs.basket_krk.domain.model

enum class MatchStatus(val key: String) {
    NON_STARTED("ns"),
    IN_PROGRESS("ip"),
    FINISHED("me"),
    WALKOVER("wo");

    companion object {
        fun fromKey(keyName: String): MatchStatus =
            MatchStatus.entries.find { it.key == keyName }
                ?: throw IllegalArgumentException("Unknown match status: $keyName")
    }
}

fun MatchStatus.inProgressOrEnded(): Boolean {
    return this == MatchStatus.IN_PROGRESS || this == MatchStatus.FINISHED || this == MatchStatus.WALKOVER
}