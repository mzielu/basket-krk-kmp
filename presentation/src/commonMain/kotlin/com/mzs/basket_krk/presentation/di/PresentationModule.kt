package com.mzs.basket_krk.presentation.di

import com.mzs.basket_krk.domain.usecase.GetMatchDetails
import com.mzs.basket_krk.domain.usecase.GetMatchDetailsUseCase
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
import com.mzs.basket_krk.presentation.screens.matchdetails.MatchDetailsViewModel
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

    // data source factories
    single<BaseMatchesPagingSourceFactory> { MatchesPagingSourceFactory(get()) }
    single<BaseSearchItemsPagingSourceFactory> { SearchItemsPagingSourceFactory(get()) }

    // view models
    viewModelOf(::MainViewModel)
    viewModelOf(::MatchesViewModel)
    viewModel { (matchId: Int) -> MatchDetailsViewModel(matchId, get()) }
    viewModelOf(::SearchViewModel)
    viewModelOf(::MoreViewModel)
}