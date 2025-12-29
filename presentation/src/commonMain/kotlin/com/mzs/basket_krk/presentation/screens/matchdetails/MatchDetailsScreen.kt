package com.mzs.basket_krk.presentation.screens.matchdetails

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import basket_krk.presentation.generated.resources.Res
import basket_krk.presentation.generated.resources.label_match_about_start
import basket_krk.presentation.generated.resources.label_match_no_stats
import basket_krk.presentation.generated.resources.label_match_not_played_yet
import basket_krk.presentation.generated.resources.label_match_wo_in_favor_of
import basket_krk.presentation.generated.resources.label_match_wo_two_way
import basket_krk.presentation.generated.resources.label_playoffs
import basket_krk.presentation.generated.resources.label_reg_season
import basket_krk.presentation.generated.resources.league_label
import com.mzs.basket_krk.datautils.MatchFakeData
import com.mzs.basket_krk.domain.model.MatchDetails
import com.mzs.basket_krk.domain.model.MatchStatus
import com.mzs.basket_krk.domain.model.MatchType
import com.mzs.basket_krk.domain.model.TournamentType
import com.mzs.basket_krk.presentation.base.ViewStateData
import com.mzs.basket_krk.presentation.base.getMatchDateTime
import com.mzs.basket_krk.presentation.base.ui.ActionBar
import com.mzs.basket_krk.presentation.base.ui.BasketKrkColors
import com.mzs.basket_krk.presentation.base.ui.BasketKrkImage
import com.mzs.basket_krk.presentation.base.ui.BasketKrkStyles
import com.mzs.basket_krk.presentation.base.ui.ErrorView
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MatchDetailsScreen(
    viewModel: MatchDetailsViewModel,
    onNavigateBack: () -> Unit,
) {

    val viewState by viewModel.viewState.collectAsState()

    MatchDetailsContent(
        viewState = viewState,
        onRetry = viewModel::retry,
        onNavigateBack = onNavigateBack,
    )
}

@Composable
fun MatchDetailsContent(
    viewState: MatchDetailsViewState,
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
        topBar = {
            ActionBar(
                titleText = "Match Details",
                showBackButton = true,
                onBackButtonClick = onNavigateBack,
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {


            if (viewState.matchDetails.isLoading) {

                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (viewState.matchDetails.isError) {

                ErrorView(error = viewState.matchDetails.error, retryAction = onRetry)
            } else {
                viewState.matchDetails.data?.let { matchDetails ->
                    if (matchDetails.statsEmpty) {
                        ViewWithoutTable(
                            matchDetails = matchDetails,
                            onOpenTeamDetails = {},
                            middleText = matchDetails.resolveMiddleText()
                        )
                    } else {
                        ViewWithoutTable(
                            matchDetails = matchDetails,
                            onOpenTeamDetails = {},
                            middleText = matchDetails.resolveMiddleText()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ViewWithoutTable(
    matchDetails: MatchDetails, onOpenTeamDetails: (Int) -> Unit, middleText: String
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopView(matchDetails = matchDetails, onOpenTeamDetails = onOpenTeamDetails)

        if (matchDetails.qtrs.isNotEmpty()) {
            Text(
                text = matchDetails.qtrs.joinToString(" | "),
                style = BasketKrkStyles.matchDetailsDateScore,
                modifier = Modifier.padding(bottom = 4.dp).align(Alignment.CenterHorizontally)
            )
        }

        HorizontalDivider(color = BasketKrkColors.Main)

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = middleText, textAlign = TextAlign.Center, modifier = Modifier.padding(32.dp)
            )
        }
    }
}


@Composable
private fun MiddleTopView(matchDetails: MatchDetails) {
    Column(
        modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = matchDetails.resolveMatchLabel(),
            style = BasketKrkStyles.matchDetailsDescription,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "${matchDetails.t1.points} - ${matchDetails.t2.points}",
            style = BasketKrkStyles.matchDetailsMainScore,
            textAlign = TextAlign.Center
        )

        val showOpenInWeb =
            (matchDetails.status == MatchStatus.IN_PROGRESS || matchDetails.status == MatchStatus.FINISHED) && matchDetails.tournament != TournamentType.KNBA

        if (showOpenInWeb) {
            Spacer(Modifier.height(6.dp))
            //TODO OpenInWebButton(onOpenInWeb = { /* TODO */ } )
        }

        Text(
            text = getMatchDateTime(
                status = matchDetails.status, date = matchDetails.date, time = matchDetails.time
            ), style = BasketKrkStyles.matchDetailsDateScore, textAlign = TextAlign.Center
        )

        matchDetails.arena?.let { arena ->
            Text(
                text = arena,
                style = BasketKrkStyles.matchDetailsDateScore,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TopView(matchDetails: MatchDetails, onOpenTeamDetails: (Int) -> Unit = {}) {
    return Row(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.weight(6f).padding(8.dp).clickable {
                //TODO onOpenTeamDetails(matchDetails.t1.id)
            }, contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                BasketKrkImage(
                    logoUrl = matchDetails.t1.logo,
                    contentDescription = "${matchDetails.t1.name} logo",
                    modifier = Modifier.size(80.dp)
                )
                Text(
                    text = matchDetails.t1.name,
                    textAlign = TextAlign.Center,
                    style = BasketKrkStyles.matchDetailsTeamName
                )
            }
        }

        // Middle
        Box(
            modifier = Modifier.weight(7f), contentAlignment = Alignment.Center
        ) {
            MiddleTopView(matchDetails = matchDetails)
        }

        // Right team
        Box(
            modifier = Modifier.weight(6f).padding(8.dp).clickable {
                //TODO onOpenTeamDetails(matchDetails.t2.id)
            }, contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                BasketKrkImage(
                    logoUrl = matchDetails.t2.logo,
                    contentDescription = "${matchDetails.t2.name} logo",
                    modifier = Modifier.size(80.dp)
                )
                Text(
                    text = matchDetails.t2.name,
                    textAlign = TextAlign.Center,
                    style = BasketKrkStyles.matchDetailsTeamName
                )
            }
        }
    }
}

@Composable
private fun MatchDetails.resolveMatchLabel(): String {
    val description = this.description
    if (description?.isEmpty() == false) return description

    val league = this.league ?: return when (type) {
        MatchType.REGULAR_SEASON -> stringResource(Res.string.label_reg_season)
        MatchType.PLAYOFFS -> stringResource(Res.string.label_playoffs)
    }

    val leagueLabel = stringResource(Res.string.league_label)
    val leagueText = if (league.name.length < 5) "${league.name} $leagueLabel" else league.name

    return when (type) {
        MatchType.REGULAR_SEASON -> leagueText
        MatchType.PLAYOFFS -> "${stringResource(Res.string.label_playoffs)}: $leagueText"
    }
}

@Composable
private fun MatchDetails.resolveMiddleText(): String {
    return when (status) {
        MatchStatus.NON_STARTED -> stringResource(Res.string.label_match_not_played_yet)
        MatchStatus.IN_PROGRESS -> stringResource(Res.string.label_match_about_start)

        MatchStatus.WALKOVER -> {
            val favorOf = stringResource(Res.string.label_match_wo_in_favor_of)
            when {
                t1.points > 0 -> "$favorOf ${t1.name}"
                t2.points > 0 -> "$favorOf ${t2.name}"
                else -> stringResource(Res.string.label_match_wo_two_way)
            }
        }

        MatchStatus.FINISHED -> stringResource(Res.string.label_match_no_stats)
    }
}

@Composable
@Preview
fun MatchDetailsContentPreview() {
    MatchDetailsContent(
        viewState = MatchDetailsViewState(matchDetails = ViewStateData(MatchFakeData.matchDetails())),
        onRetry = {},
        onNavigateBack = {},
    )
}
