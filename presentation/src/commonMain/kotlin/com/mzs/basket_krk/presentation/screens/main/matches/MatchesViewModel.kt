package com.mzs.basket_krk.presentation.screens.main.matches

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mzs.basket_krk.domain.base.onSuspendGeneralError
import com.mzs.basket_krk.domain.base.onSuspendSuccess
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.Round
import com.mzs.basket_krk.domain.model.Season
import com.mzs.basket_krk.domain.usecase.GetSeasonsInfo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class MatchesViewModel(
    private val getSeasonsInfo: GetSeasonsInfo,
) : ViewModel() {
    private val _viewState: MutableStateFlow<MatchesViewState> =
        MutableStateFlow(MatchesViewState())
    val viewState: StateFlow<MatchesViewState> = _viewState.asStateFlow()

    private val _effect: MutableSharedFlow<MatchesEffect> = MutableSharedFlow()
    val effect: SharedFlow<MatchesEffect> = _effect.asSharedFlow()

    init {
        fetchData()
    }

    fun onRefresh() {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            _viewState.update { it.copy(fullScreenLoading = true, error = null) }

            getSeasonsInfo()
                .onSuspendSuccess { info ->
                    _viewState.update {
                        it.copy(
                            seasons = info.seasons,
                            rounds = info.rounds,
                            selectedSeason = info.seasons.firstOrNull(),
                            selectedRound = getClosestRound(info.rounds),
                            fullScreenLoading = false
                        )
                    }
                }.onSuspendGeneralError { error ->
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
    val selectedSeason: Season? = null,
    val selectedRound: Round? = null,
    val error: Failure? = null
)

sealed class MatchesEffect {
    data class ShowError(val message: String) : MatchesEffect()
}
