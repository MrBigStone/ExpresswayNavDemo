package com.bigstone.expresswaynav.modules.home.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.bigstone.expresswaynav.R
import com.bigstone.expresswaynav.databinding.ViewSideMenuLayoutBinding
import com.bigstone.expresswaynav.ext.mergeBinding
import com.bigstone.expresswaynav.ext.setOnClickListenerEx

/**
 * @Description: 侧边栏中的菜单栏
 * @Author: yangxinlei
 * @CreateDate:  2021/10/9 10:34 上午
 * @UpdateUser:
 * @UpdateDate:
 * @UpdateRemark:
 */
class SideMenuView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val vb by mergeBinding(ViewSideMenuLayoutBinding::inflate)

    init {
        initAttrs(attrs, defStyleAttr)
    }

    private fun initAttrs(attrs: AttributeSet?, defStyleAttr: Int) {
        attrs ?: return
        val typeArray =
            context.obtainStyledAttributes(attrs, R.styleable.SideMenuView, defStyleAttr, 0)
        vb.titleTv.text = typeArray.getString(R.styleable.SideMenuView_menu_title)
        typeArray.recycle()
    }


    /**
     * 设置标题
     */
    fun setTitle(title: String) {
        vb.titleTv.text = title
    }

    /**
     * 点击回调
     * */
    fun setClickListener(l: ((View) -> Unit)) {
        vb.root.setOnClickListenerEx(l = l)
    }

}