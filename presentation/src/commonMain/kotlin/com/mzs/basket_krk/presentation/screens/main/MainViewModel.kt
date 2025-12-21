package com.mzs.basket_krk.presentation.screens.main

import androidx.lifecycle.ViewModel
import com.mzs.basket_krk.presentation.navigation.MainTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    private val _selectedTab = MutableStateFlow(MainTab.MATCHES)
    val selectedTab = _selectedTab.asStateFlow()

    fun selectTab(tab: MainTab) {
        _selectedTab.value = tab
    }
}
