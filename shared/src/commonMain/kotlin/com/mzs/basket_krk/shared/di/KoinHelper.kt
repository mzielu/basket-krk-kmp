package com.mzs.basket_krk.shared.di

import com.mzs.basket_krk.data.di.dataModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(
    appDeclaration: KoinAppDeclaration = {},
) = startKoin {
    appDeclaration()

    modules(dataModule)
}
