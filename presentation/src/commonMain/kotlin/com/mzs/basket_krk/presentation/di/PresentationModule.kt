package com.mzs.basket_krk.presentation.di

import com.mzs.basket_krk.domain.usecase.GetAllTimeLeaders
import com.mzs.basket_krk.domain.usecase.GetAllTimeLeadersUseCase
import com.mzs.basket_krk.domain.usecase.GetLeagueDetails
import com.mzs.basket_krk.domain.usecase.GetLeagueDetailsUseCase
import com.mzs.basket_krk.domain.usecase.GetLeaguesForSeason
import com.mzs.basket_krk.domain.usecase.GetLeaguesForSeasonUseCase
import com.mzs.basket_krk.domain.usecase.GetLeaguesInfo
import com.mzs.basket_krk.domain.usecase.GetLeaguesInfoUseCase
import com.mzs.basket_krk.domain.usecase.GetMatchDetails
import com.mzs.basket_krk.domain.usecase.GetMatchDetailsUseCase
import com.mzs.basket_krk.domain.usecase.GetPlayerDetails
import com.mzs.basket_krk.domain.usecase.GetPlayerDetailsUseCase
import com.mzs.basket_krk.domain.usecase.GetPlayerGameLogs
import com.mzs.basket_krk.domain.usecase.GetPlayerGameLogsUseCase
import com.mzs.basket_krk.domain.usecase.GetPlayerRecords
import com.mzs.basket_krk.domain.usecase.GetPlayerRecordsUseCase
import com.mzs.basket_krk.domain.usecase.GetPlayerStats
import com.mzs.basket_krk.domain.usecase.GetPlayerStatsUseCase
import com.mzs.basket_krk.domain.usecase.GetTeamDetails
import com.mzs.basket_krk.domain.usecase.GetTeamDetailsUseCase
import com.mzs.basket_krk.domain.usecase.GetTeamRecords
import com.mzs.basket_krk.domain.usecase.GetTeamRecordsUseCase
import com.mzs.basket_krk.domain.usecase.GetTeamResults
import com.mzs.basket_krk.domain.usecase.GetTeamResultsUseCase
import com.mzs.basket_krk.domain.usecase.GetTeamRoster
import com.mzs.basket_krk.domain.usecase.GetTeamRosterUseCase
import com.mzs.basket_krk.domain.usecase.GetMatches
import com.mzs.basket_krk.domain.usecase.GetMatchesUseCase
import com.mzs.basket_krk.domain.usecase.GetPlatform
import com.mzs.basket_krk.domain.usecase.GetPlatformUseCase
import com.mzs.basket_krk.domain.usecase.GetRoundsForSeason
import com.mzs.basket_krk.domain.usecase.GetRoundsForSeasonUseCase
import com.mzs.basket_krk.domain.usecase.GetSearchItems
import com.mzs.basket_krk.domain.usecase.GetSearchItemsUseCase
import com.mzs.basket_krk.domain.usecase.GetSeasonsInfo
import com.mzs.basket_krk.domain.usecase.GetSeasonsInfoUseCase
import com.mzs.basket_krk.presentation.screens.main.MainViewModel
import com.mzs.basket_krk.presentation.screens.main.matches.MatchesViewModel
import com.mzs.basket_krk.presentation.screens.main.matches.pagination.BaseMatchesPagingSourceFactory
import com.mzs.basket_krk.presentation.screens.main.matches.pagination.MatchesPagingSourceFactory
import com.mzs.basket_krk.presentation.screens.main.more.MoreViewModel
import com.mzs.basket_krk.presentation.screens.main.search.SearchViewModel
import com.mzs.basket_krk.presentation.screens.main.search.pagination.BaseSearchItemsPagingSourceFactory
import com.mzs.basket_krk.presentation.screens.main.search.pagination.SearchItemsPagingSourceFactory
import com.mzs.basket_krk.presentation.screens.main.statistics.alltimeleaders.AllTimeLeadersViewModel
import com.mzs.basket_krk.presentation.screens.main.statistics.alltimeleaders.pagination.AllTimeLeadersPagingSourceFactory
import com.mzs.basket_krk.presentation.screens.main.statistics.alltimeleaders.pagination.BaseAllTimeLeadersPagingSourceFactory
import com.mzs.basket_krk.presentation.screens.main.statistics.standings.StandingsViewModel
import com.mzs.basket_krk.presentation.screens.matchdetails.MatchDetailsViewModel
import com.mzs.basket_krk.presentation.screens.playerdetails.PlayerDetailsViewModel
import com.mzs.basket_krk.presentation.screens.teamdetails.TeamDetailsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    // use cases
    single<GetSeasonsInfo> { GetSeasonsInfoUseCase(get()) }
    single<GetMatches> { GetMatchesUseCase(get()) }
    single<GetRoundsForSeason> { GetRoundsForSeasonUseCase(get()) }
    single<GetMatchDetails> { GetMatchDetailsUseCase(get()) }
    single<GetSearchItems> { GetSearchItemsUseCase(get()) }
    single<GetPlatform> { GetPlatformUseCase() }
    single<GetAllTimeLeaders> { GetAllTimeLeadersUseCase(get()) }
    single<GetLeagueDetails> { GetLeagueDetailsUseCase(get()) }
    single<GetLeaguesInfo> { GetLeaguesInfoUseCase(get()) }
    single<GetLeaguesForSeason> { GetLeaguesForSeasonUseCase(get()) }
    single<GetPlayerDetails> { GetPlayerDetailsUseCase(get()) }
    single<GetPlayerGameLogs> { GetPlayerGameLogsUseCase(get()) }
    single<GetPlayerStats> { GetPlayerStatsUseCase(get()) }
    single<GetPlayerRecords> { GetPlayerRecordsUseCase(get()) }
    single<GetTeamDetails> { GetTeamDetailsUseCase(get()) }
    single<GetTeamResults> { GetTeamResultsUseCase(get()) }
    single<GetTeamRoster> { GetTeamRosterUseCase(get()) }
    single<GetTeamRecords> { GetTeamRecordsUseCase(get()) }

    // data source factories
    single<BaseMatchesPagingSourceFactory> { MatchesPagingSourceFactory(get()) }
    single<BaseSearchItemsPagingSourceFactory> { SearchItemsPagingSourceFactory(get()) }
    single<BaseAllTimeLeadersPagingSourceFactory> { AllTimeLeadersPagingSourceFactory(get()) }

    // view models
    viewModelOf(::MainViewModel)
    viewModelOf(::MatchesViewModel)
    viewModel { (matchId: Int) -> MatchDetailsViewModel(matchId, get()) }
    viewModel { (playerId: Int) -> PlayerDetailsViewModel(playerId, get(), get(), get(), get()) }
    viewModel { (teamId: Int) -> TeamDetailsViewModel(teamId, get(), get(), get(), get()) }
    viewModelOf(::SearchViewModel)
    viewModelOf(::MoreViewModel)
    viewModelOf(::AllTimeLeadersViewModel)
    viewModelOf(::StandingsViewModel)
}