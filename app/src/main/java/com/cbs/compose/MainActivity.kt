package com.cbs.compose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.cbs.compose.lazytv.LazyTvRow

private val channels = (0..50).map { "Channel $it" }
private val firstItemPadding = 60.dp
private val spaceBetweenItems = 16.dp
private val cardSize = DpSize(140.dp, 90.dp)
private val borderWidth = 2.dp
private val selectedBorderColor = Color.Red
private val unselectedBorderColor = Color.White

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LazyTvRow(
                items = channels,
                itemKeyGenerator = { _: Int, item: String -> item },
                onItemClicked = { item ->
                    Log.e("TAG", "onItemClicked: $item")
                },
                onItemSelected = { item ->
                    Log.e("TAG", "onItemSelected: $item")
                },
                contentPadding = PaddingValues(start = firstItemPadding),
                horizontalArrangement = Arrangement.spacedBy(spaceBetweenItems)
            ) { item, isSelected ->
                Item(item, isSelected)
            }
        }
    }

    @Composable
    private fun Item(
        item: String,
        isSelected: Boolean
    ) {
        val borderColor = if (isSelected) selectedBorderColor else unselectedBorderColor
        val scale = if (isSelected) 1f else 0.95f
        Card(
            modifier = Modifier
                .size(cardSize)
                .scale(scale),
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(borderWidth, borderColor),
        ) {
            Box(Modifier.wrapContentSize(Alignment.Center)) {
                Text(text = item)
            }
        }
    }
}