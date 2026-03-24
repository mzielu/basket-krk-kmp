package com.mzs.basket_krk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.mzs.basket_krk.domain.service.InAppPurchaseService
import com.mzs.basket_krk.presentation.App
import org.koin.android.ext.android.get

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }

        val iapService = get<InAppPurchaseService>()
        iapService.setActivity(this)
    }

    override fun onDestroy() {
        val iapService = get<InAppPurchaseService>()
        iapService.setActivity(null)
        super.onDestroy()
    }
}