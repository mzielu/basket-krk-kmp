package com.mzs.basket_krk.domain.model

enum class TeamRecordRange(val apiKey: String, val displayName: String) {
    ALL_TIME("t", "All-Time"),
    SEASON("s", "Season"),
    MATCH("m", "Match")
}

fun buildRecordCategory(stat: TeamRecordStatOption, range: TeamRecordRange): String =
    "${stat.apiKey}_${range.apiKey}"
