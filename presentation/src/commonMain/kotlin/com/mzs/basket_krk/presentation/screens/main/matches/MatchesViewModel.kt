package com.mzs.basket_krk.presentation.screens.main.matches

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import co.touchlab.kermit.Logger
import com.mzs.basket_krk.domain.base.onSuspendGeneralError
import com.mzs.basket_krk.domain.base.onSuspendSuccess
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.Match
import com.mzs.basket_krk.domain.model.Round
import com.mzs.basket_krk.domain.model.Season
import com.mzs.basket_krk.domain.usecase.GetRoundsForSeason
import com.mzs.basket_krk.domain.usecase.GetSeasonsInfo
import com.mzs.basket_krk.presentation.base.BasePagingSource
import com.mzs.basket_krk.presentation.screens.main.matches.pagination.BaseMatchesPagingSourceFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalCoroutinesApi::class)
class MatchesViewModel(
    private val getSeasonsInfo: GetSeasonsInfo,
    private val getRoundsForSeason: GetRoundsForSeason,
    private val matchesPagingSourceFactory: BaseMatchesPagingSourceFactory
) : ViewModel() {
    private val _viewState: MutableStateFlow<MatchesViewState> =
        MutableStateFlow(MatchesViewState())
    val viewState: StateFlow<MatchesViewState> = _viewState.asStateFlow()

    private val _effect: MutableSharedFlow<MatchesEffect> = MutableSharedFlow()
    val effect: SharedFlow<MatchesEffect> = _effect.asSharedFlow()


    private lateinit var pagingSource: BasePagingSource<Match>

    private val roundFlow: StateFlow<Round?> by lazy {
        _viewState
            .map { it.selectedRound }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = null
            )
    }

    val pagingFlow: Flow<PagingData<Match>> by lazy {
        roundFlow
            .filterNotNull()
            .flatMapLatest { round ->
                Pager(
                    config = PagingConfig(pageSize = 10),
                    pagingSourceFactory = {
                        matchesPagingSourceFactory.create(10, round.id)
                            .also { pagingSource = it }
                    },
                ).flow
            }.cachedIn(viewModelScope)
    }

    init {
        fetchInitData()
    }

    fun onRefresh() {
        val vs = _viewState.value

        when {
            vs.selectedSeason == null -> fetchInitData()
            vs.selectedRound == null -> fetchRoundsData(seasonId = vs.selectedSeason.id)
            else -> refreshMatches()
        }
    }

    private fun refreshMatches() {
        if (this::pagingSource.isInitialized) pagingSource.invalidate()
    }

    fun onRoundSelected(newRound: Round) {
        _viewState.update { it.copy(selectedRound = newRound) }
    }

    fun onSeasonSelected(newSeason: Season) {
        if (newSeason != _viewState.value.selectedSeason) {
            _viewState.update { it.copy(selectedSeason = newSeason, selectedRound = null) }
            fetchRoundsData(seasonId = newSeason.id)
        }
    }

    private fun fetchInitData() {
        viewModelScope.launch {
            _viewState.update { it.copy(fullScreenLoading = true, error = null) }

            getSeasonsInfo()
                .onSuspendSuccess { info ->

                    val sortedSeasons = info.seasons.sortedByDescending { it.num }
                    val sortedRounds = info.rounds.sortedByDescending { it.date }
                    val selectedRound = getClosestRound(info.rounds)

                    _viewState.update {
                        it.copy(
                            seasons = sortedSeasons,
                            rounds = sortedRounds,
                            selectedSeason = sortedSeasons.firstOrNull(),
                            selectedRound = selectedRound,
                            fullScreenLoading = false
                        )
                    }
                }.onSuspendGeneralError { error ->
                    Logger.e("Error when fetching data", error)
                    _viewState.update { it.copy(error = error, fullScreenLoading = false) }
                }
        }
    }

    private fun fetchRoundsData(seasonId: Int) {
        viewModelScope.launch {
            _viewState.update { it.copy(fullScreenLoading = true, error = null) }

            getRoundsForSeason(input = seasonId)
                .onSuspendSuccess { rounds ->

                    val sortedRounds = rounds.sortedByDescending { it.date }
                    val selectedRound = getClosestRound(rounds)

                    _viewState.update {
                        it.copy(
                            rounds = sortedRounds,
                            selectedRound = selectedRound,
                            fullScreenLoading = false
                        )
                    }
                }.onSuspendGeneralError { error ->
                    Logger.e("Error when fetching rounds data", error)
                    _viewState.update { it.copy(error = error, fullScreenLoading = false) }
                }
        }
    }

    @OptIn(ExperimentalTime::class)
    fun getClosestRound(roundsList: List<Round>): Round? {
        val now = Clock.System.now()
        val today = now.toLocalDateTime(TimeZone.currentSystemDefault())

        return roundsList.minByOrNull { round ->
            val diffDays = today.date.daysUntil(round.date)
            abs(diffDays)
        }
    }
}

@Immutable
data class MatchesViewState(
    val fullScreenLoading: Boolean = false,
    val seasons: List<Season> = emptyList(),
    val rounds: List<Round> = emptyList(),
    val matches: List<Match> = emptyList(),
    val selectedSeason: Season? = null,
    val selectedRound: Round? = null,
    val error: Failure? = null
)

sealed class MatchesEffect {
    data class ShowError(val message: String) : MatchesEffect()
}
