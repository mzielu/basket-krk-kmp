package com.mzs.basket_krk.presentation.screens.premium

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import basket_krk.presentation.generated.resources.Res
import basket_krk.presentation.generated.resources.manage_subscription_button
import basket_krk.presentation.generated.resources.premium_account
import basket_krk.presentation.generated.resources.premium_active_label
import basket_krk.presentation.generated.resources.premium_app_store_legal_info
import basket_krk.presentation.generated.resources.premium_app_store_page
import basket_krk.presentation.generated.resources.premium_description
import basket_krk.presentation.generated.resources.premium_google_play_legal_info
import basket_krk.presentation.generated.resources.premium_google_play_page
import basket_krk.presentation.generated.resources.subscribe_active_button
import basket_krk.presentation.generated.resources.subscribe_button
import com.mzs.basket_krk.domain.model.Platform
import com.mzs.basket_krk.domain.model.PremiumProduct
import com.mzs.basket_krk.presentation.base.ui.ActionBar
import com.mzs.basket_krk.presentation.base.ui.BasketKrkColors
import com.mzs.basket_krk.presentation.base.ui.ErrorView
import com.mzs.basket_krk.presentation.base.ui.FullScreenLoader
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PremiumScreen(
    viewModel: PremiumViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
) {
    val viewState by viewModel.viewState.collectAsState()

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
        ) {
            when {
                viewState.isLoading -> FullScreenLoader()
                viewState.error != null -> ErrorView(
                    error = viewState.error,
                    retryAction = viewModel::onRetry,
                )
                else -> {
                    PremiumContent(
                        product = viewState.product,
                        isPremiumActive = viewState.isPremiumActive,
                        platform = viewState.platform,
                        subscriptionManagementUrl = viewState.subscriptionManagementUrl,
                        onSubscribeClick = viewModel::onBuyClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun PremiumContent(
    product: PremiumProduct?,
    isPremiumActive: Boolean,
    platform: Platform,
    subscriptionManagementUrl: String,
    onSubscribeClick: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        SubscriptionCard(
            isPremiumActive = isPremiumActive,
            product = product,
            onSubscribeClick = onSubscribeClick,
            onManageClick = { uriHandler.openUri(subscriptionManagementUrl) },
        )

        Spacer(modifier = Modifier.height(8.dp))
        Spacer(modifier = Modifier.weight(1f))

        LegalText(
            platform = platform,
            onLinkClick = { url -> uriHandler.openUri(url) },
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun SubscriptionCard(
    isPremiumActive: Boolean,
    product: PremiumProduct?,
    onSubscribeClick: () -> Unit,
    onManageClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 32.dp, horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (!isPremiumActive) {
                // Subscribe state
                Text(
                    text = stringResource(Res.string.premium_account),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(Res.string.premium_description),
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                )
                if (product != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = product.formattedPriceAndPeriod,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                ElevatedButton(
                    onClick = onSubscribeClick,
                    enabled = product != null,
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = Color.White,
                        contentColor = BasketKrkColors.MainDark,
                    ),
                ) {
                    Text(text = stringResource(Res.string.subscribe_button))
                }
            } else {
                // Active state
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(28.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(Res.string.subscribe_active_button),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF388E3C),
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(Res.string.premium_active_label),
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                )
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedButton(
                    onClick = onManageClick,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = BasketKrkColors.MainDark,
                    ),
                ) {
                    Text(text = stringResource(Res.string.manage_subscription_button))
                }
            }
        }
    }
}

@Composable
private fun LegalText(
    platform: Platform,
    onLinkClick: (String) -> Unit,
) {
    val fullText: String
    val linkText: String
    val linkUrl: String

    if (platform == Platform.ANDROID) {
        fullText = stringResource(Res.string.premium_google_play_legal_info)
        linkText = stringResource(Res.string.premium_google_play_page)
        linkUrl = "https://play.google.com/store/account/subscriptions"
    } else {
        fullText = stringResource(Res.string.premium_app_store_legal_info)
        linkText = stringResource(Res.string.premium_app_store_page)
        linkUrl = "https://apps.apple.com/account/subscriptions"
    }

    val startIndex = fullText.indexOf(linkText)
    if (startIndex == -1 || linkText.isEmpty()) {
        Text(
            text = fullText,
            modifier = Modifier.padding(16.dp),
        )
        return
    }

    val annotatedString = buildAnnotatedString {
        append(fullText.substring(0, startIndex))
        pushStringAnnotation(tag = "URL", annotation = linkUrl)
        withStyle(SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline)) {
            append(linkText)
        }
        pop()
        append(fullText.substring(startIndex + linkText.length))
    }

    ClickableText(
        text = annotatedString,
        modifier = Modifier.padding(16.dp),
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    onLinkClick(annotation.item)
                }
        },
    )
}
