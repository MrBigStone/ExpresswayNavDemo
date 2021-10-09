package com.bigstone.expresswaynav.base

import android.view.View
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import com.bigstone.expresswaynav.R
import com.bigstone.expresswaynav.databinding.FragmentBaseTitleBinding
import com.bigstone.expresswaynav.ext.setStatusBarState
import org.jetbrains.annotations.NotNull

class BaseTitleFragment<T : ViewBinding> : BaseFragment<FragmentBaseTitleBinding>(R.layout.fragment_base_title) {

    private var _containerBinding: T? = null
    val containerVb get() = _containerBinding!!

    override fun initBinding(view: View): FragmentBaseTitleBinding =
        FragmentBaseTitleBinding.bind(view)

    override fun initView() {
       // initTitleView()
    }
//
//    private fun initTitleView(){
//        vb.statusBarPlaceHolder.setStatusBarState()
//        vb.titleView.setOnClickListener {
//            // 弹出
//        }
//    }
//
//    /**
//     * 只能在 [.onViewCreated()]中调用
//     */
//    protected fun setContentView(@LayoutRes resId: Int) {
//        vb.containerFl.removeAllViews()
//        mContentView = layoutInflater.inflate(resId, container, true)
//    }
//
//    /**
//     * 只能在 [.onViewCreated()]中调用
//     */
//    protected fun setContentView(@NotNull contentView: View) {
//        contentView.parent?.let {
//            throw IllegalArgumentException("contentView 已经拥有父控件")
//        }
//        container?.let {
//            container.removeAllViews()
//            container.addView(contentView)
//        }
//        mContentView = contentView
//    }
}