package com.bigstone.expresswaynav.ext

import android.os.Build
import android.view.View
import com.bigstone.expresswaynav.utils.SizeUtils

/**
 * dp转px
 * @param dpValue dp值
 * @return px值
 */
fun View.dp2px(dpValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}


/**
 * 根据状态栏的高度设置view的高度或者隐藏显示状态
 */
fun View.setStatusBarState() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        visibility = View.VISIBLE
        setLPHeight(SizeUtils.getStatusBarHeight(context))
    } else {
        visibility = View.GONE
    }
}

/**
 * 设置View的高度
 */
fun View.setLPHeight(height: Int): View {
    val params = layoutParams
    if (params != null) {
        params.height = height
        layoutParams = params
    } else {
        throw NullPointerException("LayoutParams is null")
    }
    return this
}

/**
 * 设置View的高度
 */
fun View.setLPWidth(width: Int): View {
    val params = layoutParams
    if (params != null) {
        params.width = width
        layoutParams = params
    } else {
        throw NullPointerException("LayoutParams is null")
    }
    return this
}


/**
 * view 的防连击处理
 */
fun View.setOnClickListenerEx(period: Long = 150, l: ((View) -> Unit)) {
    this.setOnClickListener(OnClickListenerWrapper(period = period, onClick = l))
}

private class OnClickListenerWrapper(
    private val period: Long,
    private val onClick: (view: View) -> Unit
) : View.OnClickListener {
    private var lastClickTime = 0L
    override fun onClick(view: View) {
        if (System.currentTimeMillis() - lastClickTime < period) return
        lastClickTime = System.currentTimeMillis()
        onClick.invoke(view)
    }
}