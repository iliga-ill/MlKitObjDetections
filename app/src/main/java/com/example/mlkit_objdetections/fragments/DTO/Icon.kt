package com.example.ejisho.fragments.DTO

import android.graphics.Color
import com.example.mlkit_objdetections.R

data class Icon(
    val id: Int = 0,
    val name: String? = null,
    val iconImageId: Int = 0,
    val enabledIconColorId: Int = Color.valueOf(R.color.black).toArgb(),
    val disabledIconColorId: Int = Color.valueOf(R.color.day_dark_grey).toArgb(),
//    val disabledIconColorId: Int = Color.valueOf(R.color.day_dark_grey),
    val widthDp: Float,
    val heightDp: Float,
)
