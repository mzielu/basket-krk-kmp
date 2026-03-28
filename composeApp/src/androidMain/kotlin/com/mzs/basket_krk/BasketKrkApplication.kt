package com.mzs.basket_krk

import android.app.Application
import com.avsystem.cemmobile.sdk.CemSdk
import com.avsystem.cemmobile.sdk.CemSdkConfig
import com.mzs.basket_krk.shared.di.initKoin
import org.koin.android.ext.koin.androidContext

class BasketKrkApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@BasketKrkApplication)
        }

        val config = CemSdkConfig.Builder()
            .baseApiUrl("https://example.com")
            .authToken("test-token")
            .build()

        CemSdk.initialize(this, config)
    }
}
