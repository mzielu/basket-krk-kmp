package com.mzs.basket_krk.presentation.screens.main.matches.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.mzs.basket_krk.datautils.MatchFakeData
import com.mzs.basket_krk.domain.model.MatchStatus
import com.mzs.basket_krk.domain.model.MatchTeam
import com.mzs.basket_krk.domain.model.inProgressOrEnded
import com.mzs.basket_krk.presentation.base.ui.BasketKrkImage
import com.mzs.basket_krk.presentation.base.ui.BasketKrkStyles
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MatchItemTeamRow(
    matchTeam: MatchTeam,
    matchStatus: MatchStatus,
    highlight: Boolean,
    modifier: Modifier = Modifier,
) {
    val textStyle = if (highlight) {
        BasketKrkStyles.listItemMainTextBold;
    } else {
        BasketKrkStyles.listItemMainText;
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasketKrkImage(
            logoUrl = matchTeam.logoUrl,
            contentDescription = "${matchTeam.name} logo",
            modifier = Modifier.size(25.dp),
        )

        Spacer(Modifier.width(4.dp))

        Text(
            text = matchTeam.name,
            style = textStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = ptsValue(matchTeam, matchStatus),
            style = textStyle,
            textAlign = TextAlign.End
        )
    }
}

private fun ptsValue(matchTeam: MatchTeam, matchStatus: MatchStatus): String =
    if (matchStatus.inProgressOrEnded()) matchTeam.points.toString() else ""


@Composable
@Preview
fun MatchItemTeamRowFinishedPreview() {
    MatchItemTeamRow(
        matchTeam = MatchFakeData.matchTeam(),
        matchStatus = MatchStatus.FINISHED,
        highlight = true,
        modifier = Modifier.background(Color.White)
    )
}

@Composable
@Preview
fun MatchItemTeamRowInProgressPreview() {
    MatchItemTeamRow(
        matchTeam = MatchFakeData.matchTeam(),
        matchStatus = MatchStatus.IN_PROGRESS,
        highlight = true,
        modifier = Modifier.background(Color.White)
    )
}

@Composable
@Preview
fun MatchItemTeamRowNotStartedPreview() {
    MatchItemTeamRow(
        matchTeam = MatchFakeData.matchTeam(),
        matchStatus = MatchStatus.NON_STARTED,
        highlight = true,
        modifier = Modifier.background(Color.White)
    )
}