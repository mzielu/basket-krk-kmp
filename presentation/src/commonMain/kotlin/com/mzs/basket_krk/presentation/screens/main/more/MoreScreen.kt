package com.mzs.basket_krk.presentation.screens.main.more

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChangeCircle
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.Facebook
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Policy
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import basket_krk.presentation.generated.resources.Res
import basket_krk.presentation.generated.resources.more_about_us
import basket_krk.presentation.generated.resources.more_buy_premium
import basket_krk.presentation.generated.resources.more_change_trnmt
import basket_krk.presentation.generated.resources.more_check_fb
import basket_krk.presentation.generated.resources.more_check_ig
import basket_krk.presentation.generated.resources.more_donate
import basket_krk.presentation.generated.resources.more_privacy_policy
import basket_krk.presentation.generated.resources.more_terms_of_use
import basket_krk.presentation.generated.resources.more_write_to_us
import com.mzs.basket_krk.domain.model.Platform
import com.mzs.basket_krk.presentation.base.ui.BasketKrkColors
import com.mzs.basket_krk.presentation.base.ui.NavigationItem
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MoreScreen(
    viewModel: MoreViewModel = koinViewModel(),
    logEvent: (name: String, params: Map<String, Any?>?) -> Unit,
    onOpenTournamentChooser: () -> Unit,
    onOpenPayments: () -> Unit,
) {
    val viewState by viewModel.viewState.collectAsState()

    MoreContent(
        viewState = viewState,
        logEvent = logEvent,
        onOpenTournamentChooser = onOpenTournamentChooser,
        onOpenPayments = onOpenPayments,
    )
}

@Composable
fun MoreContent(
    viewState: MoreViewState,
    logEvent: (name: String, params: Map<String, Any?>?) -> Unit,
    onOpenTournamentChooser: () -> Unit,
    onOpenPayments: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BasketKrkColors.DefaultBackground)
            .verticalScroll(rememberScrollState())
    ) {
        NavigationItem(
            title = stringResource(Res.string.more_change_trnmt),
            imageVector = Icons.Outlined.ChangeCircle,
            onClick = {
                onOpenTournamentChooser()
                logEvent("MORE_TOURNAMENT_CHOOSER_CLICKED", null)
            }
        )

        NavigationItem(
            title = stringResource(Res.string.more_about_us),
            imageVector = Icons.Outlined.SmartToy,
            onClick = {
                logEvent("MORE_ABOUT_ME_CLICKED", null)
            }
        )

        NavigationItem(
            title = stringResource(Res.string.more_donate),
            imageVector = Icons.Outlined.Savings,
            onClick = {
                logEvent("MORE_DONATE_CLICKED", null)
            }
        )

        NavigationItem(
            title = stringResource(Res.string.more_buy_premium),
            imageVector = Icons.Outlined.WorkspacePremium,
            onClick = {
                onOpenPayments()
            }
        )

        NavigationItem(
            title = stringResource(Res.string.more_terms_of_use),
            imageVector = Icons.Outlined.DocumentScanner,
            onClick = {
                logEvent("MORE_TERMS_OF_USE_CLICKED", null)
            }
        )

        NavigationItem(
            title = stringResource(Res.string.more_privacy_policy),
            imageVector = Icons.Outlined.Policy,
            onClick = {
                logEvent("MORE_PRIVACY_POLICY_CLICKED", null)
            }
        )

        NavigationItem(
            title = stringResource(Res.string.more_write_to_us),
            imageVector = Icons.Outlined.Mail,
            onClick = {
                logEvent("MORE_WRITE_TO_US_CLICKED", null)
                val mailto =
                    "mailto:kontakt@basketkrk.pl?subject=[${viewState.platform}] Mail from the app"

                uriHandler.openUri(mailto)
            }
        )

        NavigationItem(
            title = stringResource(Res.string.more_check_fb),
            imageVector = Icons.Outlined.Facebook,
            onClick = {
                logEvent("MORE_FACEBOOK_CLICKED", null)
            }
        )

        NavigationItem(
            title = stringResource(Res.string.more_check_ig),
            imageVector = Icons.Outlined.PhotoCamera,
            onClick = {
                logEvent("MORE_INSTAGRAM_CLICKED", null)
            }
        )
    }
}

@Composable
@Preview
fun MoreContentPreview() {
    MoreContent(
        viewState = MoreViewState(platform = Platform.ANDROID),
        logEvent = { _, _ -> },
        onOpenTournamentChooser = {},
        onOpenPayments = {},
    )
}