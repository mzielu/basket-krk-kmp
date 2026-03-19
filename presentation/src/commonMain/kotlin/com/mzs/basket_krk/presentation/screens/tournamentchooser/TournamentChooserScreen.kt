package com.mzs.basket_krk.presentation.screens.tournamentchooser

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import basket_krk.presentation.generated.resources.Res
import basket_krk.presentation.generated.resources.choose_trnmnt
import basket_krk.presentation.generated.resources.trnmnt_knba
import basket_krk.presentation.generated.resources.trnmnt_mba
import basket_krk.presentation.generated.resources.trnmnt_mbaw
import com.mzs.basket_krk.domain.model.TournamentType
import com.mzs.basket_krk.presentation.base.ui.ActionBar
import com.mzs.basket_krk.presentation.base.ui.BasketKrkColors
import org.jetbrains.compose.resources.stringResource

@Composable
fun TournamentChooserScreen(
    viewModel: TournamentChooserViewModel,
    onSwitchAndRestart: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val viewState by viewModel.viewState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                TournamentChooserEffect.RestartApp -> onSwitchAndRestart()
            }
        }
    }

    TournamentChooserContent(
        viewState = viewState,
        onTournamentSelected = viewModel::onTournamentSelected,
        onNavigateBack = onNavigateBack,
    )
}

@Composable
private fun TournamentChooserContent(
    viewState: TournamentChooserViewState,
    onTournamentSelected: (TournamentType) -> Unit,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
        topBar = {
            ActionBar(
                titleText = stringResource(Res.string.choose_trnmnt),
                showBackButton = true,
                onBackButtonClick = onNavigateBack,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BasketKrkColors.DefaultBackground),
        ) {
            viewState.tournaments.forEach { tournament ->
                TournamentRadioItem(
                    title = tournament.toDisplayName(),
                    selected = tournament == viewState.currentTournament,
                    onClick = { onTournamentSelected(tournament) },
                )
            }
        }
    }
}

@Composable
private fun TournamentRadioItem(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(Modifier.width(8.dp))
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun TournamentType.toDisplayName(): String = when (this) {
    TournamentType.MBA -> stringResource(Res.string.trnmnt_mba)
    TournamentType.WMBA -> stringResource(Res.string.trnmnt_mbaw)
    TournamentType.KNBA -> stringResource(Res.string.trnmnt_knba)
}
