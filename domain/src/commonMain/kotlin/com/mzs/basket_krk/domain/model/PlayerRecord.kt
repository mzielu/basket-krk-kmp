package com.mzs.basket_krk.domain.model

data class PlayerRecord(
    val recordType: PlayerRecordType,
    val value: Int,
    val times: Int,
    val matchId: Int,
    val date: String
)
