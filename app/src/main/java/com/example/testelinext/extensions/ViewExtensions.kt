package com.example.testelinext.extensions

import android.graphics.Outline
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider

fun View.setCornerRadius(radiusPx: Float) {
    outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            outline.setRoundRect(
                0,
                0,
                view.width,
                view.height,
                radiusPx
            )
        }
    }
    clipToOutline = true
}

fun View.dpToPx(dp: Float) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    dp,
    resources.displayMetrics
)