package com.reader.app.utils

import androidx.compose.foundation.gestures.detectGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.combinedClickable(
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
): Modifier {
    return this.pointerInput(onLongClick != null) {
        detectGestures(
            onPress = {
                if (onLongClick != null) {
                    val longPressTimeout = 500L
                    val wasLongPressed = kotlinx.coroutines.delay(longPressTimeout)
                    if (wasLongPressed) {
                        onLongClick()
                    } else {
                        onClick()
                    }
                } else {
                    onClick()
                }
            }
        )
    }
}