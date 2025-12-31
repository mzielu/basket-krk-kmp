package com.mzs.basket_krk.presentation.screens.main.search

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mzs.basket_krk.domain.model.SearchItem
import com.mzs.basket_krk.presentation.base.BasePagingSource
import com.mzs.basket_krk.presentation.screens.main.search.pagination.BaseSearchItemsPagingSourceFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

private const val PAGE_SIZE = 15
private const val INITIAL_FILTER_VALUE = ""

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchViewModel(
    private val searchItemsPagingSourceFactory: BaseSearchItemsPagingSourceFactory
) : ViewModel() {
    private val _viewState: MutableStateFlow<SearchViewState> = MutableStateFlow(SearchViewState())
    val viewState: StateFlow<SearchViewState> = _viewState.asStateFlow()

    private val _effect: MutableSharedFlow<SearchEffect> = MutableSharedFlow()
    val effect: SharedFlow<SearchEffect> = _effect.asSharedFlow()

    private lateinit var pagingSource: BasePagingSource<SearchItem>

    private val textFow: StateFlow<String> by lazy {
        _viewState
            .map { it.currentTextFieldValue }
            .distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = INITIAL_FILTER_VALUE
            )
    }

    val pagingFlow: Flow<PagingData<SearchItem>> by lazy {
        textFow
            .debounce(500)
            .distinctUntilChanged()
            .flatMapLatest { text ->
                Pager(
                    config = PagingConfig(pageSize = PAGE_SIZE),
                    pagingSourceFactory = {
                        searchItemsPagingSourceFactory.create(PAGE_SIZE, text)
                            .also { pagingSource = it }
                    },
                ).flow
            }.cachedIn(viewModelScope)
    }

    fun onRefresh() {
        if (::pagingSource.isInitialized) pagingSource.invalidate()
    }
}

@Immutable
data class SearchViewState(
    val currentTextFieldValue: String = INITIAL_FILTER_VALUE
)

sealed class SearchEffect {
    data class ShowError(val message: String) : SearchEffect()
}
