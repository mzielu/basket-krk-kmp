package com.mzs.basket_krk.datautils

import com.mzs.basket_krk.datautils.SearchFakeData.searchItemPlayer
import com.mzs.basket_krk.datautils.SearchFakeData.searchItemTeam
import com.mzs.basket_krk.domain.model.AllTimeLeader
import com.mzs.basket_krk.domain.model.Competition
import com.mzs.basket_krk.domain.model.CompetitionStanding
import com.mzs.basket_krk.domain.model.League
import com.mzs.basket_krk.domain.model.LeagueDetails
import com.mzs.basket_krk.domain.model.SearchItem

object LeagueFakeData {
    fun league(
        id: Int = 42,
        name: String = "Super League"
    ) = League(id = id, name = name)

    fun leagueDetails(
        id: Int = 42,
        name: String = "Super League",
        competitions: List<Competition> = listOf(competition())
    ) = LeagueDetails(id = id, name = name, competitions = competitions)

    fun allTimeLeader(
        player: SearchItem.Player = searchItemPlayer(),
        team: SearchItem.Team = searchItemTeam(),
        value: Int = 1000,
        position: Int = 1,
        inf: String? = "25.2"
    ) = AllTimeLeader(
        player = player,
        team = team,
        value = value,
        position = position,
        inf = inf
    )

    fun competition(
        id: Int = 1,
        name: String = "Competition Name",
        standings: List<CompetitionStanding> = listOf(competitionStanding())
    ) = Competition(
        id = id,
        name = name,
        standings = standings
    )

    fun competitionStanding(
        pos: Int = 1,
        team: SearchItem.Team = searchItemTeam(),
        w: Int = 10,
        l: Int = 2,
        p: Int = 22,
        m: Int = 10,
        wo: Int = 0

    ) = CompetitionStanding(
        pos = pos,
        team = team,
        w = w,
        l = l,
        p = p,
        m = m,
        wo = wo
    )
}
