package com.mzs.basket_krk.presentation.di

import com.mzs.basket_krk.domain.usecase.GetSeasonsInfo
import com.mzs.basket_krk.domain.usecase.GetSeasonsInfoUseCase
import com.mzs.basket_krk.presentation.screens.main.MainViewModel
import com.mzs.basket_krk.presentation.screens.main.matches.MatchesViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    // use cases
    single<GetSeasonsInfo> { GetSeasonsInfoUseCase(get()) }

    // view models
    viewModelOf(::MainViewModel)
    viewModelOf(::MatchesViewModel)
}