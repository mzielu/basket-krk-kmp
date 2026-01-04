package com.mzs.basket_krk.presentation.screens.main.statistics.alltimeleaders.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mzs.basket_krk.datautils.SearchFakeData
import com.mzs.basket_krk.domain.model.AllTimeLeader
import com.mzs.basket_krk.domain.model.SearchItem
import com.mzs.basket_krk.presentation.base.ui.AutoSizeText
import com.mzs.basket_krk.presentation.base.ui.BasketKrkColors
import com.mzs.basket_krk.presentation.base.ui.BasketKrkImage
import com.mzs.basket_krk.presentation.base.ui.BasketKrkStyles
import org.jetbrains.compose.ui.tooling.preview.Preview

private val ITEM_HEIGHT = 40.dp
private val ITEM_RADIUS = 12.dp

@Composable
fun LeaderItem(
    leader: AllTimeLeader,
    onOpenPlayerDetails: (SearchItem.Player) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(ITEM_RADIUS)

    Box(
        modifier = modifier
            .clip(shape)
            .border(1.dp, BasketKrkColors.BorderRoundedItem, shape)
            .height(ITEM_HEIGHT)
            .fillMaxWidth()
            .clickable { onOpenPlayerDetails(leader.player) }
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Position
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = leader.position.toString(),
                    style = BasketKrkStyles.competitionItemRow
                )
            }

            // Team logo
            Box(
                modifier = Modifier
                    .width(30.dp)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {

                leader.team.logoPath?.let {
                    BasketKrkImage(
                        logoUrl = it,
                        contentDescription = "${leader.team.name} logo",
                        modifier = Modifier.size(35.dp)
                    )
                }
            }

            // Name
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "${leader.player.firstName} ${leader.player.lastName}",
                    style = BasketKrkStyles.competitionItemRow
                )
            }

            Spacer(Modifier.width(4.dp))

            // Value
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = leader.value.toString(),
                    style = BasketKrkStyles.competitionItemRow
                )
            }

            leader.inf?.let {
                Box(
                    modifier = Modifier
                        .width(50.dp)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    AutoSizeText(
                        text = it,
                        style = BasketKrkStyles.competitionItemRowLight
                    )
                }
            } ?: Spacer(Modifier.width(12.dp))
        }
    }
}

@Composable
@Preview
fun LeaderItemPreview() {
    LeaderItem(
        leader = AllTimeLeader(
            position = 1,
            player = SearchFakeData.searchItemPlayer(),
            team = SearchFakeData.searchItemTeam(),
            value = 1234,
            inf = "+5.6"
        ),
        onOpenPlayerDetails = { },
    )
}
