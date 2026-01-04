package com.mzs.basket_krk.presentation.screens.main.statistics.alltimeleaders

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mzs.basket_krk.domain.model.AllTimeLeader
import com.mzs.basket_krk.domain.model.AllTimeStatLeaderOption
import com.mzs.basket_krk.presentation.base.BasePagingSource
import com.mzs.basket_krk.presentation.screens.main.statistics.alltimeleaders.pagination.BaseAllTimeLeadersPagingSourceFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

private const val PAGE_SIZE = 20
private val INITIAL_FILTER_VALUE = AllTimeStatLeaderOption.PTS

@OptIn(ExperimentalCoroutinesApi::class)
class AllTimeLeadersViewModel(
    private val allTimeLeadersPagingSourceFactory: BaseAllTimeLeadersPagingSourceFactory
) : ViewModel() {
    private val _viewState: MutableStateFlow<AllTimeLeadersViewState> =
        MutableStateFlow(AllTimeLeadersViewState())
    val viewState: StateFlow<AllTimeLeadersViewState> = _viewState.asStateFlow()

    private lateinit var pagingSource: BasePagingSource<AllTimeLeader>

    private val statOptionFow: StateFlow<AllTimeStatLeaderOption> by lazy {
        _viewState
            .map { it.selectedStatOption }
            .distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = INITIAL_FILTER_VALUE
            )
    }

    val pagingFlow: Flow<PagingData<AllTimeLeader>> by lazy {
        statOptionFow
            .flatMapLatest { statOption ->
                Pager(
                    config = PagingConfig(pageSize = PAGE_SIZE),
                    pagingSourceFactory = {
                        allTimeLeadersPagingSourceFactory.create(PAGE_SIZE, statOption)
                            .also { pagingSource = it }
                    },
                ).flow
            }.cachedIn(viewModelScope)
    }

    fun onRefresh() {
        if (::pagingSource.isInitialized) pagingSource.invalidate()
    }

    fun onStatOptionChanged(newValue: AllTimeStatLeaderOption) {
        _viewState.value = _viewState.value.copy(
            selectedStatOption = newValue
        )
    }
}

@Immutable
data class AllTimeLeadersViewState(
    val selectedStatOption: AllTimeStatLeaderOption = INITIAL_FILTER_VALUE,
)