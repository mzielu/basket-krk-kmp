package com.mzs.basket_krk.domain.model

enum class MatchType(val key: String) {
    REGULAR_SEASON("rs"),
    PLAYOFFS("po");

    companion object {
        fun fromKey(keyName: String): MatchType =
            MatchType.entries.find { it.key == keyName }
                ?: throw IllegalArgumentException("Unknown match type: $keyName")
    }
}
