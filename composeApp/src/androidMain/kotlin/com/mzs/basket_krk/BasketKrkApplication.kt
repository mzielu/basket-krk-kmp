package com.mzs.basket_krk

import android.app.Application
import com.mzs.basket_krk.shared.di.initKoin
import org.koin.android.ext.koin.androidContext

class BasketKrkApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@BasketKrkApplication)
        }
    }
}
