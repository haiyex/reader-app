package com.reader.app.presentation.theme

import androidx.compose.ui.graphics.Color

sealed class ReaderTheme(
    val name: String,
    val id: String,
    val backgroundColor: Color,
    val textColor: Color,
    val secondaryTextColor: Color
) {
    object Default : ReaderTheme(
        name = "默认白色",
        id = "default",
        backgroundColor = Color.White,
        textColor = Color.Black,
        secondaryTextColor = Color.Gray
    )

    object EyeCare : ReaderTheme(
        name = "护眼米色",
        id = "eyecare",
        backgroundColor = Color(0xFFF5E6D3),
        textColor = Color.Black,
        secondaryTextColor = Color.DarkGray
    )

    object Night : ReaderTheme(
        name = "夜间黑底",
        id = "night",
        backgroundColor = Color.Black,
        textColor = Color.White,
        secondaryTextColor = Color.LightGray
    )

    companion object {
        fun fromId(id: String): ReaderTheme {
            return when (id) {
                "eyecare" -> EyeCare
                "night" -> Night
                else -> Default
            }
        }

        fun getAllThemes(): List<ReaderTheme> {
            return listOf(Default, EyeCare, Night)
        }
    }
}
