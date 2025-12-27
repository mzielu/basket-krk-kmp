package com.mzs.basket_krk.data.di

import com.mzs.basket_krk.data.repository.MatchRepositoryImpl
import com.mzs.basket_krk.data.repository.SeasonRepositoryImpl
import com.mzs.basket_krk.data.service.ApiService
import com.mzs.basket_krk.data.service.HttpClientFactory
import com.mzs.basket_krk.data.service.NetworkMatchService
import com.mzs.basket_krk.data.service.NetworkSeasonService
import com.mzs.basket_krk.domain.repository.MatchRepository
import com.mzs.basket_krk.domain.repository.SeasonRepository
import com.mzs.basket_krk.domain.service.MatchService
import com.mzs.basket_krk.domain.service.SeasonService
import org.koin.dsl.module

val dataModule = module {
    single { ApiService(client = HttpClientFactory().create()) }

    // services
    single<SeasonService> { NetworkSeasonService(get()) }
    single<MatchService> { NetworkMatchService(get()) }

    // repositories
    single<SeasonRepository> { SeasonRepositoryImpl(get()) }
    single<MatchRepository> { MatchRepositoryImpl(get()) }
}