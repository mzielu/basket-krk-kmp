package com.mzs.basket_krk.presentation.base.ui

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BasketKrkPullToRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    state: PullToRefreshState = rememberPullToRefreshState(),
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier,
        state = state,
        contentAlignment = contentAlignment,
        indicator = {
            PullToRefreshDefaults.Indicator(
                state = state,
                containerColor = Color.White,
                color = BasketKrkColors.Main,
                isRefreshing = isRefreshing,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        },
        content = content
    )
}

@Preview
@Composable
private fun BasketKrkPullToRefreshPreview() {
    BasketKrkPullToRefresh(
        isRefreshing = true,
        onRefresh = {}
    ) {
        LazyColumn {
            items(10) { item ->
                ListItem(headlineContent = { Text("Item $item") })
            }
        }
    }
}
