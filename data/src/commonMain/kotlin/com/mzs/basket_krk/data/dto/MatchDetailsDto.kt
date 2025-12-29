package com.mzs.basket_krk.data.dto

import com.mzs.basket_krk.domain.model.MatchDetails
import com.mzs.basket_krk.domain.model.MatchDetailsTeam
import com.mzs.basket_krk.domain.model.League
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
    tournament = tournament,
    type = type,
    status = status,
    date = date,
    time = time,
    t1 = t1.toDomain(),
    t2 = t2.toDomain(),
    qtrs = qtrs,
    desc = desc,
    arena = arena,
    lg = lg?.toDomain()
)
