package com.cbs.compose.lazytv

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun <T> LazyTvRow(
    modifier: Modifier = Modifier,
    items: List<T>,
    onItemClicked: (item: T) -> Unit,
    onItemSelected: (item: T) -> Unit,
    itemKeyGenerator: (index: Int, item: T) -> Any,

    // <LazyRowParameters>
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    horizontalArrangement: Arrangement.Horizontal =
        if (!reverseLayout) Arrangement.Start else Arrangement.End,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    // </LazyRowParameters>

    content: @Composable (item: T, isSelected: Boolean) -> Unit,
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val selectedItemIndex = remember { mutableStateOf(0) }
    val job = remember { mutableStateOf<Job?>(null) }

    LazyRow(
        state = listState,
        contentPadding = contentPadding,
        reverseLayout = reverseLayout,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled,
        modifier = modifier
            .focusable(true)
            .onHorizontalKeyDownEvents(
                onLeftKeyEvent = {
                    if (selectedItemIndex.value == 0) return@onHorizontalKeyDownEvents false
                    selectedItemIndex.value = selectedItemIndex.value - 1
                    onItemSelected(items[selectedItemIndex.value])

                    job.value?.cancel()
                    job.value = coroutineScope.launch {
                        listState.animateScrollToItem(selectedItemIndex.value)
                    }

                    true
                },
                onRightKeyEvent = {
                    if (selectedItemIndex.value == listState.layoutInfo.totalItemsCount - 1) return@onHorizontalKeyDownEvents false
                    selectedItemIndex.value = selectedItemIndex.value + 1

                    job.value?.cancel()
                    job.value = coroutineScope.launch {
                        listState.animateScrollToItem(selectedItemIndex.value)
                    }
                    onItemSelected(items[selectedItemIndex.value])

                    true
                }
            )
            .clickable {
                onItemClicked(items[selectedItemIndex.value])
            }
    ) {
        itemsIndexed(
            items = items,
            key = itemKeyGenerator,
            itemContent = { index: Int, item: T ->
                content(
                    item = item,
                    isSelected = index == selectedItemIndex.value
                )
            }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
private fun Modifier.onHorizontalKeyDownEvents(
    onLeftKeyEvent: () -> Boolean,
    onRightKeyEvent: () -> Boolean
): Modifier = onKeyEvent { keyEvent: KeyEvent ->
    if (keyEvent.type != KeyEventType.KeyDown) return@onKeyEvent false

    val key = keyEvent.key
    if (key != Key.DirectionLeft && key != Key.DirectionRight) return@onKeyEvent false

    if (key == Key.DirectionLeft) onLeftKeyEvent() else onRightKeyEvent()
}




