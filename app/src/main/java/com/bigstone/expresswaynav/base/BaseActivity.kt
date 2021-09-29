package com.bigstone.expresswaynav.base

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.bigstone.expresswaynav.utils.StatusBarHelper

abstract class BaseActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarHelper.translucent(this)
        StatusBarHelper.setStatusBarLightMode(this)
    }
}