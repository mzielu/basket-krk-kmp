package com.mzs.basket_krk.domain.model

enum class PlayerRecordType { PTS, REB, AST, STL, BLK, EFF, FGM, FGA, FG3M, FG3A, FTM, FTA }

fun PlayerRecordType.getSign(): String = name

fun PlayerRecordType.toDescription(): String = when (this) {
    PlayerRecordType.PTS -> "Most points in a game"
    PlayerRecordType.REB -> "Most rebounds in a game"
    PlayerRecordType.AST -> "Most assists in a game"
    PlayerRecordType.STL -> "Most steals in a game"
    PlayerRecordType.BLK -> "Most blocks in a game"
    PlayerRecordType.EFF -> "Highest EFF score in a game"
    PlayerRecordType.FGM -> "Most fields goal made"
    PlayerRecordType.FGA -> "Most fields goal attempted"
    PlayerRecordType.FG3M -> "Most 3-point fields goal made"
    PlayerRecordType.FG3A -> "Most 3-point fields goal attempted"
    PlayerRecordType.FTM -> "Most free throws made"
    PlayerRecordType.FTA -> "Most free throws attempted"
}
