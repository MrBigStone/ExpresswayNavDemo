package com.bigstone.expresswaynav.modules.home.view

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import com.bigstone.expresswaynav.databinding.ViewHomeSiderBarBinding
import com.bigstone.expresswaynav.ext.dp2px
import com.bigstone.expresswaynav.ext.mergeBinding
import com.bigstone.expresswaynav.modules.home.HomeFragment
import com.bigstone.expresswaynav.utils.SizeUtils.getStatusBarHeight

/**
 * @Description: 主页侧边栏
 * @Author: yangxinlei
 * @CreateDate:  2021/9/29 3:51 下午
 * @UpdateUser:
 * @UpdateDate:
 * @UpdateRemark:
 */
class HomeSideBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val vb by mergeBinding(ViewHomeSiderBarBinding::inflate)

    init {
        val paddingTop = getStatusBarHeight(context) + dp2px(30f)
        vb.userInfoCl.setPadding(dp2px(10f), paddingTop, dp2px(10f), dp2px(30f))
        initView()
    }

    /**
     * 视图初始化
     * */
    private fun initView() {
        vb.codeMenu.setClickListener {
            val fm =
                (context as? FragmentActivity)?.supportFragmentManager ?: return@setClickListener
            HomeFragment.start(fm, android.R.id.content)
        }
    }

}