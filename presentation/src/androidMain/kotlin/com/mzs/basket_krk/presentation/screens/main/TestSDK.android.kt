package com.mzs.basket_krk.presentation.screens.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.avsystem.cemmobile.sdk.CemSpeedTestView
import com.avsystem.cemmobile.sdk.CemView

@Composable
actual fun TestSDK() {
    val context = LocalContext.current

    AndroidView(
        factory = { CemSpeedTestView(it) },
        modifier = Modifier.fillMaxSize()
    )
}