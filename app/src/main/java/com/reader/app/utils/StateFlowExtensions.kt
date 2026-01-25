package com.reader.app.utils

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first

fun <T> StateFlow<T>.asStateFlow(): StateFlow<T> = this
