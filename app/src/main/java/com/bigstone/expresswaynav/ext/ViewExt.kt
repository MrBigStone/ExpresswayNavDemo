package com.bigstone.expresswaynav.ext

import android.view.View

/**
 * dp转px
 * @param dpValue dp值
 * @return px值
 */
fun View.dp2px(dpValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}