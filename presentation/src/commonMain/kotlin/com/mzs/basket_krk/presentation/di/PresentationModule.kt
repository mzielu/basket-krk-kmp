package com.mzs.basket_krk.presentation.di

import com.mzs.basket_krk.domain.usecase.GetMatchDetails
import com.mzs.basket_krk.domain.usecase.GetMatchDetailsUseCase
import com.mzs.basket_krk.domain.usecase.GetMatches
import com.mzs.basket_krk.domain.usecase.GetMatchesUseCase
import com.mzs.basket_krk.domain.usecase.GetRoundsForSeason
import com.mzs.basket_krk.domain.usecase.GetRoundsForSeasonUseCase
import com.mzs.basket_krk.domain.usecase.GetSeasonsInfo
import com.mzs.basket_krk.domain.usecase.GetSeasonsInfoUseCase
import com.mzs.basket_krk.presentation.screens.main.MainViewModel
import com.mzs.basket_krk.presentation.screens.main.matches.MatchesViewModel
import com.mzs.basket_krk.presentation.screens.main.matches.pagination.BaseMatchesPagingSourceFactory
import com.mzs.basket_krk.presentation.screens.main.matches.pagination.MatchesPagingSourceFactory
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

    // data source factories
    single<BaseMatchesPagingSourceFactory> { MatchesPagingSourceFactory(get()) }

    // view models
    viewModelOf(::MainViewModel)
    viewModelOf(::MatchesViewModel)
    viewModel { (matchId: Int) -> MatchDetailsViewModel(matchId, get()) }
}