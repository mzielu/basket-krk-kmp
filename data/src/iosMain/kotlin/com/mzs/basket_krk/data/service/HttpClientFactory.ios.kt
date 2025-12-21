package com.mzs.basket_krk.data.service

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

actual fun platformEngine(): HttpClientEngine = Darwin.create()