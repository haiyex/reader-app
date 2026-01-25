package com.reader.app.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

suspend fun <T> Flow<T>.first(): T {
    return kotlinx.coroutines.flow.first(this)
}

fun Int.coAtLeast(minimum: Int): Int {
    return maxOf(this, minimum)
}
