package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.PlayerRecord
import com.mzs.basket_krk.domain.model.PlayerRecordType
import kotlinx.serialization.Serializable

@Serializable
data class PlayerRecordsDto(
    val m1: String,
    val a1: String,
    val fgm: String,
    val fga: String,
    val m3: String,
    val a3: String,
    val pt: String,
    val a: String,
    val r: String,
    val b: String,
    val s: String,
    val eff: String
)

fun PlayerRecordsDto.toDomain(): List<PlayerRecord> {
    val raw = listOf(
        PlayerRecordType.PTS to pt,
        PlayerRecordType.REB to r,
        PlayerRecordType.AST to a,
        PlayerRecordType.STL to s,
        PlayerRecordType.BLK to b,
        PlayerRecordType.EFF to eff,
        PlayerRecordType.FGM to fgm,
        PlayerRecordType.FGA to fga,
        PlayerRecordType.FG3M to m3,
        PlayerRecordType.FG3A to a3,
        PlayerRecordType.FTM to m1,
        PlayerRecordType.FTA to a1
    )
    return raw.mapNotNull { (type, rawValue) ->
        val parts = rawValue.split("/")
        val value = parts[0].toIntOrNull() ?: 0
        if (value <= 0) null
        else PlayerRecord(
            recordType = type,
            value = value,
            times = parts[1].toInt(),
            matchId = parts[2].toInt(),
            date = parts[3]
        )
    }
}
