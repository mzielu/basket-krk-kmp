package com.mzs.basket_krk.presentation.base.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import basket_krk.presentation.generated.resources.Res
import basket_krk.presentation.generated.resources.error_description
import basket_krk.presentation.generated.resources.error_try_again
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun PaginationErrorItem(
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
    messageRes: StringResource = Res.string.error_description
) {
    PaginationErrorItem(
        modifier = modifier,
        message = stringResource(messageRes),
        onRetryClick = onRetryClick
    )
}

@Composable
fun PaginationErrorItem(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Spacer(Modifier.height(16.dp))

        Text(
            text = message,
            style = BasketKrkStyles.errorDescription,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onRetryClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = BasketKrkColors.Main,
                contentColor = BasketKrkColors.DefaultButtonText
            )
        ) {
            Text(text = stringResource(Res.string.error_try_again), fontSize = 14.sp)
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Preview
@Composable
private fun PaginationErrorItemPreview() {
    PaginationErrorItem(onRetryClick = {})
}
