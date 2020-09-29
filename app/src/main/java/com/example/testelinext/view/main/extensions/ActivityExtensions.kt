package com.example.testelinext.view.main.extensions

import android.app.Activity
import android.util.TypedValue

fun Activity.dpToPx(dp: Float) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    dp,
    resources.displayMetrics
)