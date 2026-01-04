package com.mzs.basket_krk.presentation.screens.main.more

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
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
    onOpenTournamentChooser: () -> Unit,
    onOpenPayments: () -> Unit,
) {
    val viewState by viewModel.viewState.collectAsState()

    MoreContent(
        viewState = viewState,
        onOpenTournamentChooser = onOpenTournamentChooser,
        onOpenPayments = onOpenPayments,
    )
}

@Composable
fun MoreContent(
    viewState: MoreViewState,
    onOpenTournamentChooser: () -> Unit,
    onOpenPayments: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    val itemPadding = 8.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BasketKrkColors.DefaultBackground)
            .verticalScroll(rememberScrollState())
            .padding(itemPadding)
    ) {
        NavigationItem(
            title = stringResource(Res.string.more_change_trnmt),
            imageVector = Icons.Outlined.ChangeCircle,
            onClick = {
                onOpenTournamentChooser()
            }
        )

        Spacer(modifier = Modifier.height(itemPadding))

        NavigationItem(
            title = stringResource(Res.string.more_about_us),
            imageVector = Icons.Outlined.SmartToy,
            onClick = {
                // TODO: Implement about us action
            }
        )

        Spacer(modifier = Modifier.height(itemPadding))

        NavigationItem(
            title = stringResource(Res.string.more_donate),
            imageVector = Icons.Outlined.Savings,
            onClick = {
                // TODO: Implement about us action
            }
        )

        Spacer(modifier = Modifier.height(itemPadding))

        NavigationItem(
            title = stringResource(Res.string.more_buy_premium),
            imageVector = Icons.Outlined.WorkspacePremium,
            onClick = {
                onOpenPayments()
            }
        )

        Spacer(modifier = Modifier.height(itemPadding))

        NavigationItem(
            title = stringResource(Res.string.more_terms_of_use),
            imageVector = Icons.Outlined.DocumentScanner,
            onClick = {
                // TODO: Implement about us action
            }
        )

        Spacer(modifier = Modifier.height(itemPadding))

        NavigationItem(
            title = stringResource(Res.string.more_privacy_policy),
            imageVector = Icons.Outlined.Policy,
            onClick = {
                // TODO: Implement about us action
            }
        )

        Spacer(modifier = Modifier.height(itemPadding))

        NavigationItem(
            title = stringResource(Res.string.more_write_to_us),
            imageVector = Icons.Outlined.Mail,
            onClick = {
                // TODO: Implement about us action
                val mailto =
                    "mailto:kontakt@basketkrk.pl?subject=[${viewState.platform}] Mail from the app"

                uriHandler.openUri(mailto)
            }
        )

        Spacer(modifier = Modifier.height(itemPadding))

        NavigationItem(
            title = stringResource(Res.string.more_check_fb),
            imageVector = Icons.Outlined.Facebook,
            onClick = {
                // TODO: Implement about us action
            }
        )

        Spacer(modifier = Modifier.height(itemPadding))

        NavigationItem(
            title = stringResource(Res.string.more_check_ig),
            imageVector = Icons.Outlined.PhotoCamera,
            onClick = {
                // TODO: Implement about us action
            }
        )
    }
}

@Composable
@Preview
fun MoreContentPreview() {
    MoreContent(
        viewState = MoreViewState(platform = Platform.ANDROID),
        onOpenTournamentChooser = {},
        onOpenPayments = {},
    )
}