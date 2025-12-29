package com.mzs.basket_krk.presentation.base.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object BasketKrkStyles {

    val actionBarTitle = TextStyle(
        color = BasketKrkColors.AppBarText,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    )

    // LIST
    val listItemMainText = TextStyle(
        color = BasketKrkColors.TextHighlighted2,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    )
    val listItemMainTextBold = TextStyle(
        color = BasketKrkColors.TextHighlighted,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    )
    val listItemSecondaryText = TextStyle(
        color = BasketKrkColors.TextSecondary,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp
    )
    val listItemTopText = TextStyle(
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp
    )

    // EDIT-TEXT
    val editTextFloating = TextStyle(
        color = BasketKrkColors.TextLight,
        fontSize = 12.sp
    )
    val editTextDefault = TextStyle(
        color = BasketKrkColors.TextNormal,
        fontSize = 14.sp
    )

    // BOTTOM-NAV-BAR
    val bottomNavBarSelected = TextStyle(
        color = BasketKrkColors.TextLight,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
    )
    val bottomNavBarUnselected = TextStyle(
        color = BasketKrkColors.TextSecondary,
        fontSize = 10.sp
    )

    // TEXT-FIELD / DROPDOWN
    val dropdownMenuItemUnselected = TextStyle(
        color = BasketKrkColors.TextDropdownItemUnselected,
        fontSize = 16.sp
    )
    val dropdownMenuItemSelected = TextStyle(
        color = BasketKrkColors.TextDropdownItemSelected,
        fontSize = 16.sp
    )

    // TABLE
    val fixedRowText = TextStyle(
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
    )
    val fixedColumnText = TextStyle(
        color = Color.Black,
        fontSize = 13.sp
    )
    val fixedColumnTextBold = TextStyle(
        color = Color.Black,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
    )
    val numericTableText = TextStyle(
        color = Color.Black,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    val numericTableTextBold = TextStyle(
        color = Color.Black,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
    )
    val numericTableTextSmall = TextStyle(
        color = Color.Black,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp
    )
    val numericTableTextSmallBold = TextStyle(
        color = Color.Black,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp
    )

    // MATCH DETAILS
    val matchDetailsTeamName = TextStyle(
        color = BasketKrkColors.TextHighlighted,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    )
    val matchDetailsMainScore = TextStyle(
        color = BasketKrkColors.Main,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    )
    val matchDetailsDateScore = TextStyle(
        color = BasketKrkColors.TextNormal,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp
    )
    val matchDetailsDescription = TextStyle(
        color = BasketKrkColors.Main,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
    )
    val matchDetailsOpenInWeb = TextStyle(
        color = BasketKrkColors.Main,
        fontSize = 11.sp
    )

    // PLAYER & TEAM DETAILS
    val itemName = TextStyle(
        color = BasketKrkColors.Main,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    )
    val itemAdditionalInfo = TextStyle(
        color = BasketKrkColors.TextSecondary,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp
    )

    val recordValue = TextStyle(
        color = BasketKrkColors.Main,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp
    )
    val recordStatSign = TextStyle(
        color = BasketKrkColors.Main,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
    )
    val recordDescription = TextStyle(
        color = BasketKrkColors.TextHighlighted,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp
    )

    val gameLogsSignResult = TextStyle(
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp
    )
    val gameLogsResult = TextStyle(
        color = BasketKrkColors.TextHighlighted,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
    )
    val gameLogsVsTeam = TextStyle(
        color = BasketKrkColors.TextSecondary,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp
    )
    val gameLogsDate = TextStyle(
        color = BasketKrkColors.TextSecondary,
        fontSize = 9.sp
    )

    val teamResult = TextStyle(
        color = BasketKrkColors.TextHighlighted,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    )
    val teamResultDate = TextStyle(
        color = BasketKrkColors.TextSecondary,
        fontSize = 10.sp
    )
    val teamResultSignResult = TextStyle(
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
    )
    val teamResultRoundedTop = TextStyle(
        color = BasketKrkColors.TextHighlighted,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp
    )
    val teamResultRoundedBottom = TextStyle(
        color = BasketKrkColors.TextHighlighted,
        fontSize = 13.sp
    )

    // LEAGUE
    val competitionTitle = TextStyle(
        color = BasketKrkColors.Main,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    )
    val competitionTitleRow = TextStyle(
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
    )
    val competitionItemRow = TextStyle(
        color = BasketKrkColors.TextHighlighted,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
    )
    val competitionItemRowLight = TextStyle(
        color = BasketKrkColors.TextHighlighted,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp
    )

    // TEAM RECORDS
    val recordsMedalPosition = TextStyle(
        fontSize = 20.sp
    )
    val recordsNormalPosition = TextStyle(
        color = BasketKrkColors.Main,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    )
    val recordsValue = TextStyle(
        color = BasketKrkColors.TextHighlighted,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
    )
    val recordsAdditional = TextStyle(
        color = BasketKrkColors.TextHighlighted,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp
    )

    // MORE
    val moreItem = TextStyle(
        color = BasketKrkColors.TextHighlighted,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp
    )

    // STATS
    val categoryTitle = TextStyle(
        color = BasketKrkColors.DarkText,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    )

    // PAYMENTS
    val paymentItemLabel = TextStyle(
        color = BasketKrkColors.TextHighlighted,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    )
    val paymentItemDescription = TextStyle(
        color = BasketKrkColors.TextHighlighted2,
        fontSize = 14.sp
    )
    val paymentsMainLabel = TextStyle(
        color = BasketKrkColors.TextHighlighted,
        fontSize = 20.sp
    )
}
