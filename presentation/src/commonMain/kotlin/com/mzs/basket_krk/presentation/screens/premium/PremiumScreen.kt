package com.mzs.basket_krk.presentation.screens.premium

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import basket_krk.presentation.generated.resources.Res
import basket_krk.presentation.generated.resources.premium_account
import com.mzs.basket_krk.presentation.base.ui.ActionBar
import com.mzs.basket_krk.presentation.base.ui.BasketKrkColors
import org.jetbrains.compose.resources.stringResource

@Composable
fun PremiumScreen(
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
        topBar = {
            ActionBar(
                titleText = stringResource(Res.string.premium_account),
                showBackButton = true,
                onBackButtonClick = onNavigateBack,
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BasketKrkColors.DefaultBackground),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "Coming soon")
        }
    }
}
