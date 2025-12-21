package com.mzs.basket_krk.data.service

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

actual fun platformEngine(): HttpClientEngine = OkHttp.create()