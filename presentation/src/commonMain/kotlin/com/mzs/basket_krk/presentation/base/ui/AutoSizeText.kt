package com.mzs.basket_krk.presentation.base.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AutoSizeText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle,
    maxLines: Int = 1,
    minFontSize: TextUnit = 8.sp
) {
    var textStyle by remember { mutableStateOf(style) }
    var readyToDraw by remember { mutableStateOf(false) }

    Text(
        text = text,
        modifier = modifier,
        maxLines = maxLines,
        softWrap = false,
        overflow = TextOverflow.Clip,
        style = textStyle,
        onTextLayout = { result ->
            if (result.hasVisualOverflow && textStyle.fontSize > minFontSize) {
                textStyle = textStyle.copy(
                    fontSize = textStyle.fontSize * 0.95f
                )
            } else {
                readyToDraw = true
            }
        }
    )
}

@Composable
@Preview
fun AutoSizeTextPreview() {
    AutoSizeText(
        text = "This is a very long text that should auto size to fit in the given space",
        style = BasketKrkStyles.fixedColumnText,
        maxLines = 1
    )
}