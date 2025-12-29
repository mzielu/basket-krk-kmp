package com.mzs.basket_krk.presentation.base.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SentimentDissatisfied
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import basket_krk.presentation.generated.resources.Res
import basket_krk.presentation.generated.resources.error_description
import basket_krk.presentation.generated.resources.error_network_description
import basket_krk.presentation.generated.resources.error_old_version_description
import basket_krk.presentation.generated.resources.error_try_again
import basket_krk.presentation.generated.resources.error_update
import com.mzs.basket_krk.domain.model.Failure
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ErrorView(
    error: Failure? = null,
    retryAction: (() -> Unit)?
) {
    val uriHandler = LocalUriHandler.current

    val errorDescription = when (error) {
        is Failure.OldVersionError -> stringResource(Res.string.error_old_version_description)
        is Failure.NetworkConnectionError -> stringResource(Res.string.error_network_description)
        else -> stringResource(Res.string.error_description)
    }

    val buttonLabel = when (error) {
        is Failure.OldVersionError -> stringResource(Res.string.error_update)
        else -> stringResource(Res.string.error_try_again)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BasketKrkColors.DefaultBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.SentimentDissatisfied,
                contentDescription = null,
                tint = BasketKrkColors.TextSecondary,
                modifier = Modifier.size(250.dp)
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = errorDescription,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(30.dp))

            Button(
                onClick = {
                    if (error is Failure.OldVersionError) {
                        uriHandler.openUri(storeUrl())
                    } else {
                        retryAction?.invoke()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = BasketKrkColors.Main,
                    contentColor = BasketKrkColors.DefaultButtonText
                )
            ) {
                Text(text = buttonLabel, fontSize = 14.sp)
            }
        }
    }
}

expect fun storeUrl(): String

@Composable
@Preview
fun ErrorViewNetworkConnectionPreview() {
    ErrorView(
        error = Failure.NetworkConnectionError,
        retryAction = {},
    )
}

@Composable
@Preview
fun ErrorViewOldVersionPreview() {
    ErrorView(
        error = Failure.OldVersionError,
        retryAction = {},
    )
}