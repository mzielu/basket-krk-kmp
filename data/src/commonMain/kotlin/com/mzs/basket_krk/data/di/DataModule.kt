package com.mzs.basket_krk.data.di

import com.mzs.basket_krk.data.repository.SeasonRepositoryImpl
import com.mzs.basket_krk.data.service.ApiService
import com.mzs.basket_krk.data.service.HttpClientFactory
import com.mzs.basket_krk.data.service.NetworkSeasonService
import com.mzs.basket_krk.domain.repository.SeasonRepository
import com.mzs.basket_krk.domain.service.SeasonService
import org.koin.dsl.module

val dataModule = module {
    single { ApiService(client = HttpClientFactory().create()) }

    // services
    single<SeasonService> { NetworkSeasonService(get()) }

    // repositories
    single<SeasonRepository> { SeasonRepositoryImpl(get()) }
}