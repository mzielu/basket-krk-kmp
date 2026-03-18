package com.mzs.basket_krk.presentation.screens.main.statistics.seasonleaders

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.mzs.basket_krk.domain.base.onSuspendGeneralError
import com.mzs.basket_krk.domain.base.onSuspendSuccess
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.League
import com.mzs.basket_krk.domain.model.LeagueLeader
import com.mzs.basket_krk.domain.model.LeagueStatLeaderOption
import com.mzs.basket_krk.domain.model.Season
import com.mzs.basket_krk.domain.usecase.GetLeagueLeaders
import com.mzs.basket_krk.domain.usecase.GetLeagueLeadersUseCase
import com.mzs.basket_krk.domain.usecase.GetLeaguesForSeason
import com.mzs.basket_krk.domain.usecase.GetLeaguesForSeasonUseCase
import com.mzs.basket_krk.domain.usecase.GetLeaguesInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class SeasonLeadersViewModel(
    private val getLeaguesInfo: GetLeaguesInfo,
    private val getLeaguesForSeason: GetLeaguesForSeason,
    private val getLeagueLeaders: GetLeagueLeaders
) : ViewModel() {
    private val _viewState: MutableStateFlow<SeasonLeadersViewState> =
        MutableStateFlow(SeasonLeadersViewState())
    val viewState: StateFlow<SeasonLeadersViewState> = _viewState.asStateFlow()

    init {
        fetchInitData()

        viewModelScope.launch {
            viewState
                .map { Pair(it.selectedLeague, it.selectedStatOption) }
                .distinctUntilChanged()
                .collect { (league, statOption) ->
                    if (league != null) {
                        fetchLeaders(leagueId = league.id, statOption = statOption)
                    }
                }
        }
    }

    fun onRefresh() {
        val vs = _viewState.value

        when {
            vs.selectedSeason == null -> fetchInitData()
            vs.selectedLeague == null -> fetchLeaguesData(seasonId = vs.selectedSeason.id)
            else -> fetchLeaders(leagueId = vs.selectedLeague.id, statOption = vs.selectedStatOption)
        }
    }

    fun onLeagueSelected(newLeague: League) {
        _viewState.update { it.copy(selectedLeague = newLeague) }
    }

    fun onSeasonSelected(newSeason: Season) {
        if (newSeason != _viewState.value.selectedSeason) {
            _viewState.update { it.copy(selectedSeason = newSeason, selectedLeague = null) }
            fetchLeaguesData(seasonId = newSeason.id)
        }
    }

    fun onStatOptionChanged(newOption: LeagueStatLeaderOption) {
        _viewState.update { it.copy(selectedStatOption = newOption) }
    }

    private fun fetchInitData() {
        viewModelScope.launch {
            _viewState.update { it.copy(fullScreenLoading = true, error = null) }

            getLeaguesInfo()
                .onSuspendSuccess { info ->
                    val sortedSeasons = info.seasons.sortedByDescending { it.num }
                    _viewState.update {
                        it.copy(
                            seasons = sortedSeasons,
                            leagues = info.leagues,
                            selectedSeason = sortedSeasons.firstOrNull(),
                            selectedLeague = info.leagues.firstOrNull(),
                            fullScreenLoading = false
                        )
                    }
                }.onSuspendGeneralError { error ->
                    Logger.e("Error when fetching leagues data", error)
                    _viewState.update { it.copy(error = error, fullScreenLoading = false) }
                }
        }
    }

    private fun fetchLeaguesData(seasonId: Int) {
        viewModelScope.launch {
            _viewState.update { it.copy(fullScreenLoading = true, error = null) }

            getLeaguesForSeason(input = GetLeaguesForSeasonUseCase.Input(seasonId = seasonId))
                .onSuspendSuccess { leagues ->
                    _viewState.update {
                        it.copy(
                            leagues = leagues,
                            selectedLeague = leagues.firstOrNull(),
                            fullScreenLoading = false
                        )
                    }
                }.onSuspendGeneralError { error ->
                    Logger.e("Error when fetching leagues data", error)
                    _viewState.update { it.copy(error = error, fullScreenLoading = false) }
                }
        }
    }

    private fun fetchLeaders(leagueId: Int, statOption: LeagueStatLeaderOption) {
        viewModelScope.launch {
            _viewState.update { it.copy(fullScreenLoading = true, error = null) }

            getLeagueLeaders(input = GetLeagueLeadersUseCase.Input(leagueId = leagueId, statOption = statOption))
                .onSuspendSuccess { result ->
                    _viewState.update {
                        it.copy(
                            leaders = result,
                            fullScreenLoading = false
                        )
                    }
                }.onSuspendGeneralError { error ->
                    Logger.e("Error when fetching leaders data", error)
                    _viewState.update { it.copy(error = error, fullScreenLoading = false) }
                }
        }
    }
}

@Immutable
data class SeasonLeadersViewState(
    val fullScreenLoading: Boolean = false,
    val seasons: List<Season> = emptyList(),
    val leagues: List<League> = emptyList(),
    val leaders: List<LeagueLeader> = emptyList(),
    val selectedSeason: Season? = null,
    val selectedLeague: League? = null,
    val selectedStatOption: LeagueStatLeaderOption = LeagueStatLeaderOption.PTS,
    val error: Failure? = null
)
