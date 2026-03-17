package com.mzs.basket_krk.domain.model

enum class TeamRecordStatOption(val apiKey: String, val displayName: String) {
    PTS("pts", "PTS"),
    AST("ast", "AST"),
    REB("reb", "REB"),
    STL("stl", "STL"),
    BLK("blk", "BLK"),
    EFF("eff", "EFF"),
    FT("ft", "FT"),
    FG("fg", "FG"),
    FG3("fg3", "3FG")
}
