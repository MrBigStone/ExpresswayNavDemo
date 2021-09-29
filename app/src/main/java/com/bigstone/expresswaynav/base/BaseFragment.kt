package com.bigstone.expresswaynav.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

/**
 * @Description: Fragment基础类，实现ViewBinding
 * @Author: yangxinlei
 * @CreateDate:  2021/9/29 3:50 下午
 * @UpdateUser:
 * @UpdateDate:
 * @UpdateRemark:
 */
abstract class BaseFragment<T : ViewBinding>(layoutId: Int) : Fragment(layoutId) {

    private var _binding: T? = null
    val vb get() = _binding!!

    var isSupportVisible = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = initBinding(view)
        initView()
    }

    /**
     * 初始化[_binding]
     * */
    abstract fun initBinding(view: View): T

    /**
     * 视图初始化
     * */
    abstract fun initView()

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    // region visible/invisible 切换
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden && isResumed) {
            //可见操作
            onSupportVisible()
        } else {
            //不可见操作
            onSupportInVisible()
        }
    }

    override fun onResume() {
        super.onResume()
        //可见操作
        onSupportVisible()
    }

    override fun onPause() {
        super.onPause()
        //不可见操作
        onSupportInVisible()
    }

    open fun onSupportVisible() {
        isSupportVisible = true
    }

    open fun onSupportInVisible() {
        isSupportVisible = false
    }
    // endregion
}