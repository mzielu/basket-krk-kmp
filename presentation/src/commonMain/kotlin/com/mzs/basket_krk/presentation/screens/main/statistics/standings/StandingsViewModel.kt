package com.mzs.basket_krk.presentation.screens.main.statistics.standings

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.mzs.basket_krk.domain.base.onSuspendGeneralError
import com.mzs.basket_krk.domain.base.onSuspendSuccess
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.League
import com.mzs.basket_krk.domain.model.LeagueDetails
import com.mzs.basket_krk.domain.model.Season
import com.mzs.basket_krk.domain.usecase.GetLeagueDetails
import com.mzs.basket_krk.domain.usecase.GetLeagueDetailsUseCase
import com.mzs.basket_krk.domain.usecase.GetLeaguesForSeason
import com.mzs.basket_krk.domain.usecase.GetLeaguesForSeasonUseCase
import com.mzs.basket_krk.domain.usecase.GetLeaguesInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class StandingsViewModel(
    private val getLeaguesInfo: GetLeaguesInfo,
    private val getLeaguesForSeason: GetLeaguesForSeason,
    private val getLeagueDetails: GetLeagueDetails
) : ViewModel() {
    private val _viewState: MutableStateFlow<StandingsViewState> =
        MutableStateFlow(StandingsViewState())
    val viewState: StateFlow<StandingsViewState> = _viewState.asStateFlow()


    init {
        fetchInitData()

        viewModelScope.launch {
            viewState
                .map { it.selectedLeague }
                .distinctUntilChanged()
                .filterNotNull()
                .collect {
                    fetchLeagueDetails(leagueId = it.id)
                }
        }
    }

    fun onRefresh() {
        val vs = _viewState.value

        when {
            vs.selectedSeason == null -> fetchInitData()
            vs.selectedLeague == null -> fetchLeaguesData(seasonId = vs.selectedSeason.id)
            else -> fetchLeagueDetails(vs.selectedLeague.id)
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

    private fun fetchLeagueDetails(leagueId: Int) {
        viewModelScope.launch {
            _viewState.update { it.copy(fullScreenLoading = true, error = null) }

            getLeagueDetails
                .invoke(GetLeagueDetailsUseCase.Input(leagueId = leagueId))
                .onSuspendSuccess { details ->
                    _viewState.update {
                        it.copy(
                            leagueDetails = details,
                            fullScreenLoading = false
                        )
                    }
                }.onSuspendGeneralError { error ->
                    Logger.e("Error when fetching league details data", error)
                    _viewState.update { it.copy(error = error, fullScreenLoading = false) }
                }
        }
    }
}

@Immutable
data class StandingsViewState(
    val fullScreenLoading: Boolean = false,
    val seasons: List<Season> = emptyList(),
    val leagues: List<League> = emptyList(),
    val leagueDetails: LeagueDetails? = null,
    val selectedSeason: Season? = null,
    val selectedLeague: League? = null,
    val error: Failure? = null
)