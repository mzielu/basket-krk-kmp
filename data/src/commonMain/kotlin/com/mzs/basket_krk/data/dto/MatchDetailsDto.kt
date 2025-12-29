package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.MatchDetails
import com.mzs.basket_krk.domain.model.MatchStatus
import com.mzs.basket_krk.domain.model.MatchType
import com.mzs.basket_krk.domain.model.TournamentType
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class MatchDetailsDto(
    val id: Int,
    val id_tmnt: Int,
    val tournament: String,
    val type: String,
    val status: String,
    val date: String,
    val time: String,
    val t1: MatchDetailsTeamDto,
    val t2: MatchDetailsTeamDto,
    val qtrs: String? = null,
    val desc: String? = null,
    val arena: String? = null,
    val lg: LeagueDto? = null
)

fun MatchDetailsDto.toDomain() = MatchDetails(
    id = id,
    idTmnt = id_tmnt,
    tournament = TournamentType.fromKey(tournament),
    type = MatchType.fromKey(type),
    status = MatchStatus.fromKey(status),
    date = LocalDate.parse(date),
    time = time,
    t1 = t1.toDomain(),
    t2 = t2.toDomain(),
    qtrs = qtrs,
    description = desc,
    arena = arena,
    league = lg?.toDomain()
)
