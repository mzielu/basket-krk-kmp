package com.mzs.basket_krk.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mzs.basket_krk.presentation.base.ui.BasketKrkColors
import com.mzs.basket_krk.presentation.navigation.Screen
import com.mzs.basket_krk.presentation.screens.main.MainScreen
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun App() {
    MaterialTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(BasketKrkColors.Main),
            contentAlignment = Alignment.Center,
        ) {
            val navController: NavHostController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = Screen.Main,
            ) {
                // Onboarding Graph
                composable<Screen.Main> {
                    MainScreen()
                }
            }
        }
    }
}