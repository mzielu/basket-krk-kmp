package com.mzs.basket_krk.domain.model

enum class TeamRecordRange(val apiKey: String) {
    ALL_TIME("t"),
    SEASON("s"),
    MATCH("m")
}

fun buildRecordCategory(stat: TeamRecordStatOption, range: TeamRecordRange): String =
    "${stat.apiKey}_${range.apiKey}"
