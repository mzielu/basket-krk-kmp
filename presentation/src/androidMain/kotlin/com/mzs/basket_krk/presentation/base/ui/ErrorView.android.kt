package com.mzs.basket_krk.presentation.base.ui

private const val ANDROID_MARKET_URL = "market://details?id=com.mzs.basket_krk"

actual fun storeUrl(): String {
    return ANDROID_MARKET_URL
}