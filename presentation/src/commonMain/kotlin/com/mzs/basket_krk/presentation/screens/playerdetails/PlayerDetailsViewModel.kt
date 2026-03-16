package com.mzs.basket_krk.presentation.screens.playerdetails

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.mzs.basket_krk.domain.base.onSuspendGeneralError
import com.mzs.basket_krk.domain.base.onSuspendSuccess
import com.mzs.basket_krk.domain.model.PlayerDetails
import com.mzs.basket_krk.domain.model.PlayerLogList
import com.mzs.basket_krk.domain.model.PlayerRecord
import com.mzs.basket_krk.domain.model.PlayerStat
import com.mzs.basket_krk.domain.model.Season
import com.mzs.basket_krk.domain.usecase.GetPlayerDetails
import com.mzs.basket_krk.domain.usecase.GetPlayerDetailsUseCase
import com.mzs.basket_krk.domain.usecase.GetPlayerGameLogs
import com.mzs.basket_krk.domain.usecase.GetPlayerGameLogsUseCase
import com.mzs.basket_krk.domain.usecase.GetPlayerRecords
import com.mzs.basket_krk.domain.usecase.GetPlayerRecordsUseCase
import com.mzs.basket_krk.domain.usecase.GetPlayerStats
import com.mzs.basket_krk.domain.usecase.GetPlayerStatsUseCase
import com.mzs.basket_krk.presentation.base.ViewStateData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlayerDetailsViewModel(
    private val playerId: Int,
    private val getPlayerDetails: GetPlayerDetails,
    private val getPlayerGameLogs: GetPlayerGameLogs,
    private val getPlayerStats: GetPlayerStats,
    private val getPlayerRecords: GetPlayerRecords
) : ViewModel() {

    private val _viewState: MutableStateFlow<PlayerDetailsViewState> =
        MutableStateFlow(PlayerDetailsViewState())
    val viewState: StateFlow<PlayerDetailsViewState> = _viewState.asStateFlow()

    init {
        fetchPlayerDetails()
    }

    private fun fetchPlayerDetails() {
        viewModelScope.launch {
            _viewState.update { it.copy(playerDetails = it.playerDetails.loading()) }
            getPlayerDetails(input = GetPlayerDetailsUseCase.Input(playerId = playerId))
                .onSuspendSuccess { details ->
                    _viewState.update {
                        it.copy(
                            playerDetails = it.playerDetails.data(details),
                            selectedSeason = details.seasons.firstOrNull()
                        )
                    }
                    // Auto-fetch first tab (Game Logs) after player details load
                    details.seasons.firstOrNull()?.let { season ->
                        fetchGameLogsIfNeeded(season.id)
                    }
                }.onSuspendGeneralError { error ->
                    Logger.e("Error when fetching player details", error)
                    _viewState.update { it.copy(playerDetails = it.playerDetails.error(error)) }
                }
        }
    }

    fun onTabSelected(index: Int) {
        val season = _viewState.value.selectedSeason ?: return
        when (index) {
            0 -> fetchGameLogsIfNeeded(season.id)
            1 -> fetchStatsIfNeeded()
            2 -> fetchRecordsIfNeeded()
        }
    }

    fun retry() {
        fetchPlayerDetails()
    }

    private fun fetchGameLogsIfNeeded(seasonId: Int) {
        val current = _viewState.value.gameLogs
        if (current.data != null && !current.isError) return
        viewModelScope.launch {
            _viewState.update { it.copy(gameLogs = it.gameLogs.loading()) }
            getPlayerGameLogs(input = GetPlayerGameLogsUseCase.Input(playerId = playerId, seasonId = seasonId))
                .onSuspendSuccess { logs ->
                    _viewState.update { it.copy(gameLogs = it.gameLogs.data(logs)) }
                }.onSuspendGeneralError { error ->
                    Logger.e("Error when fetching player game logs", error)
                    _viewState.update { it.copy(gameLogs = it.gameLogs.error(error)) }
                }
        }
    }

    private fun fetchStatsIfNeeded() {
        val current = _viewState.value.stats
        if (current.data != null && !current.isError) return
        viewModelScope.launch {
            _viewState.update { it.copy(stats = it.stats.loading()) }
            getPlayerStats(input = GetPlayerStatsUseCase.Input(playerId = playerId))
                .onSuspendSuccess { stats ->
                    _viewState.update { it.copy(stats = it.stats.data(stats)) }
                }.onSuspendGeneralError { error ->
                    Logger.e("Error when fetching player stats", error)
                    _viewState.update { it.copy(stats = it.stats.error(error)) }
                }
        }
    }

    private fun fetchRecordsIfNeeded() {
        val current = _viewState.value.records
        if (current.data != null && !current.isError) return
        viewModelScope.launch {
            _viewState.update { it.copy(records = it.records.loading()) }
            getPlayerRecords(input = GetPlayerRecordsUseCase.Input(playerId = playerId))
                .onSuspendSuccess { records ->
                    _viewState.update { it.copy(records = it.records.data(records)) }
                }.onSuspendGeneralError { error ->
                    Logger.e("Error when fetching player records", error)
                    _viewState.update { it.copy(records = it.records.error(error)) }
                }
        }
    }
}

@Immutable
data class PlayerDetailsViewState(
    val playerDetails: ViewStateData<PlayerDetails?> = ViewStateData(null),
    val gameLogs: ViewStateData<PlayerLogList?> = ViewStateData(null),
    val stats: ViewStateData<List<PlayerStat>?> = ViewStateData(null),
    val records: ViewStateData<List<PlayerRecord>?> = ViewStateData(null),
    val selectedSeason: Season? = null
)
