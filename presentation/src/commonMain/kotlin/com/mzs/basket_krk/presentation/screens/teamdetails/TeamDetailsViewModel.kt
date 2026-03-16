package com.mzs.basket_krk.presentation.screens.teamdetails

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.mzs.basket_krk.domain.base.onSuspendGeneralError
import com.mzs.basket_krk.domain.base.onSuspendSuccess
import com.mzs.basket_krk.domain.model.PlayerWithStat
import com.mzs.basket_krk.domain.model.Season
import com.mzs.basket_krk.domain.model.TeamDetails
import com.mzs.basket_krk.domain.model.TeamRecord
import com.mzs.basket_krk.domain.model.TeamRecordRange
import com.mzs.basket_krk.domain.model.TeamRecordStatOption
import com.mzs.basket_krk.domain.model.TeamResult
import com.mzs.basket_krk.domain.model.TeamResultList
import com.mzs.basket_krk.domain.model.MatchStatus
import com.mzs.basket_krk.domain.model.MatchType
import com.mzs.basket_krk.domain.model.buildRecordCategory
import com.mzs.basket_krk.domain.usecase.GetTeamDetails
import com.mzs.basket_krk.domain.usecase.GetTeamDetailsUseCase
import com.mzs.basket_krk.domain.usecase.GetTeamRecords
import com.mzs.basket_krk.domain.usecase.GetTeamRecordsUseCase
import com.mzs.basket_krk.domain.usecase.GetTeamResults
import com.mzs.basket_krk.domain.usecase.GetTeamResultsUseCase
import com.mzs.basket_krk.domain.usecase.GetTeamRoster
import com.mzs.basket_krk.domain.usecase.GetTeamRosterUseCase
import com.mzs.basket_krk.presentation.base.ViewStateData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TeamDetailsViewModel(
    private val teamId: Int,
    private val getTeamDetails: GetTeamDetails,
    private val getTeamResults: GetTeamResults,
    private val getTeamRoster: GetTeamRoster,
    private val getTeamRecords: GetTeamRecords
) : ViewModel() {

    private val _viewState: MutableStateFlow<TeamDetailsViewState> =
        MutableStateFlow(TeamDetailsViewState())
    val viewState: StateFlow<TeamDetailsViewState> = _viewState.asStateFlow()

    init {
        fetchTeamDetails()
    }

    private fun fetchTeamDetails() {
        viewModelScope.launch {
            _viewState.update { it.copy(teamDetails = it.teamDetails.loading()) }
            getTeamDetails(input = GetTeamDetailsUseCase.Input(teamId = teamId))
                .onSuspendSuccess { details ->
                    _viewState.update {
                        it.copy(
                            teamDetails = it.teamDetails.data(details),
                            selectedSeason = details.seasons.firstOrNull()
                        )
                    }
                    // Auto-fetch first tab (Results) after team details load
                    details.seasons.firstOrNull()?.let { season ->
                        fetchResultsIfNeeded(season.id)
                    }
                }.onSuspendGeneralError { error ->
                    Logger.e("Error when fetching team details", error)
                    _viewState.update { it.copy(teamDetails = it.teamDetails.error(error)) }
                }
        }
    }

    fun onTabSelected(index: Int) {
        val season = _viewState.value.selectedSeason ?: return
        when (index) {
            0 -> fetchResultsIfNeeded(season.id)
            1 -> fetchRosterIfNeeded(season.id)
            2 -> fetchRecordsIfNeeded()
        }
    }

    fun retry() {
        fetchTeamDetails()
    }

    fun onSeasonSelected(season: Season) {
        _viewState.update {
            it.copy(
                selectedSeason = season,
                results = ViewStateData(null),
                roster = ViewStateData(null),
                winsLosses = null,
                pointDifferential = null
            )
        }
        // Re-fetch results for new season (reset cache)
        fetchResultsForSeason(season.id)
    }

    private fun fetchResultsIfNeeded(seasonId: Int) {
        val current = _viewState.value.results
        if (current.data != null && !current.isError) return
        fetchResultsForSeason(seasonId)
    }

    private fun fetchResultsForSeason(seasonId: Int) {
        viewModelScope.launch {
            _viewState.update { it.copy(results = it.results.loading()) }
            getTeamResults(input = GetTeamResultsUseCase.Input(teamId = teamId, seasonId = seasonId))
                .onSuspendSuccess { resultList ->
                    val wl = computeWinsLosses(resultList.data)
                    val pm = computePlusMinus(resultList.data)
                    _viewState.update {
                        it.copy(
                            results = it.results.data(resultList),
                            winsLosses = wl,
                            pointDifferential = pm
                        )
                    }
                }.onSuspendGeneralError { error ->
                    Logger.e("Error when fetching team results", error)
                    _viewState.update { it.copy(results = it.results.error(error)) }
                }
        }
    }

    private fun fetchRosterIfNeeded(seasonId: Int) {
        val current = _viewState.value.roster
        if (current.data != null && !current.isError) return
        viewModelScope.launch {
            _viewState.update { it.copy(roster = it.roster.loading()) }
            getTeamRoster(input = GetTeamRosterUseCase.Input(teamId = teamId, seasonId = seasonId))
                .onSuspendSuccess { roster ->
                    _viewState.update { it.copy(roster = it.roster.data(roster)) }
                }.onSuspendGeneralError { error ->
                    Logger.e("Error when fetching team roster", error)
                    _viewState.update { it.copy(roster = it.roster.error(error)) }
                }
        }
    }

    private fun fetchRecordsIfNeeded() {
        val current = _viewState.value.records
        if (current.data != null && !current.isError) return
        val category = buildRecordCategory(
            _viewState.value.selectedRecordStatOption,
            _viewState.value.selectedRecordRange
        )
        viewModelScope.launch {
            _viewState.update { it.copy(records = it.records.loading()) }
            getTeamRecords(input = GetTeamRecordsUseCase.Input(teamId = teamId, category = category))
                .onSuspendSuccess { records ->
                    _viewState.update { it.copy(records = it.records.data(records)) }
                }.onSuspendGeneralError { error ->
                    Logger.e("Error when fetching team records", error)
                    _viewState.update { it.copy(records = it.records.error(error)) }
                }
        }
    }

    private fun computeWinsLosses(results: List<TeamResult>): Pair<Int, Int> {
        val ended = results.filter {
            (it.status == MatchStatus.FINISHED || it.status == MatchStatus.WALKOVER)
                && it.type == MatchType.REGULAR_SEASON
        }
        val wins = ended.count { it.points > it.opponent.points }
        val losses = ended.count { it.opponent.points > it.points }
        return wins to losses
    }

    private fun computePlusMinus(results: List<TeamResult>): Int {
        val ended = results.filter {
            (it.status == MatchStatus.FINISHED || it.status == MatchStatus.WALKOVER)
                && it.type == MatchType.REGULAR_SEASON
        }
        return ended.sumOf { it.points } - ended.sumOf { it.opponent.points }
    }
}

@Immutable
data class TeamDetailsViewState(
    val teamDetails: ViewStateData<TeamDetails?> = ViewStateData(null),
    val results: ViewStateData<TeamResultList?> = ViewStateData(null),
    val roster: ViewStateData<List<PlayerWithStat>?> = ViewStateData(null),
    val records: ViewStateData<List<TeamRecord>?> = ViewStateData(null),
    val selectedSeason: Season? = null,
    val selectedRecordStatOption: TeamRecordStatOption = TeamRecordStatOption.PTS,
    val selectedRecordRange: TeamRecordRange = TeamRecordRange.ALL_TIME,
    val winsLosses: Pair<Int, Int>? = null,
    val pointDifferential: Int? = null,
)
