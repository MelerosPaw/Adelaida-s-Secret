package com.example.composetest.ui.compose.modifiers

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput

/**
 *
 * @param onClicked This modifier is not compatible with [clickable]. Therefore, If you need both
 * at the same time, use this lambda as you would use [clickable]. It will use the default ripple
 * animation produced by [LocalIndication].
 * @param onBeingLongPressed When a long click is detected, you will receive `true` in this lambda
 * so you can react to it to produce any visual transformations. You'll receive `false` once the
 * user releases their finger only if the action was a long click.
 * @param onLongClicked This will be executed once the user releases their finger if the action was
 * a long click.
 */
@Composable
fun Modifier.onLongClick(
    onClicked: (() -> Unit)? = null,
    onBeingLongPressed: (isLongClicked: Boolean) -> Unit = {},
    enabled: Boolean = true,
    onLongClicked: () -> Unit,
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    var isLongClicked = remember { mutableStateOf(false) }

    Modifier
        .indication(interactionSource, LocalIndication.current)
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = { offset ->
                    val press = PressInteraction.Press(offset)
                    interactionSource.emit(press)
                    tryAwaitRelease()
                    interactionSource.emit(PressInteraction.Release(press))

                    if (isLongClicked.value) {
                        isLongClicked.value = false
                        onBeingLongPressed(false)
                    }
                },
                onLongPress = {
                    if (enabled) {
                        isLongClicked.value = true
                        onBeingLongPressed(true)
                        onLongClicked()
                    }
                },
                onTap = { onClicked?.invoke() }
            )
        }

}