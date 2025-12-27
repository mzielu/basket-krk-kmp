package com.mzs.basket_krk.presentation.screens.main.matches.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.mzs.basket_krk.domain.model.MatchStatus
import com.mzs.basket_krk.domain.model.MatchTeam
import com.mzs.basket_krk.domain.model.inProgressOrEnded

@Composable
fun MatchItemTeamRow(
    matchTeam: MatchTeam,
    matchStatus: MatchStatus,
    highlight: Boolean,
    modifier: Modifier = Modifier,
) {
    val textStyle = if (highlight) {
        TextStyle.Default
    } else {
        TextStyle.Default.copy(fontSize = 20.sp)
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest
                .Builder(LocalPlatformContext.current)
                .data("https://www.basketkrk.pl/${matchTeam.logoUrl}")
                .crossfade(true)
                .build(),
            contentDescription = "Team logo",
            modifier = Modifier
                .size(25.dp),
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
