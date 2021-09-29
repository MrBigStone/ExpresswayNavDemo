package com.bigstone.expresswaynav.modules.home

import android.view.View
import com.bigstone.expresswaynav.R
import com.bigstone.expresswaynav.base.BaseFragment
import com.bigstone.expresswaynav.databinding.FragmentHomeBinding

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    override fun initBinding(view: View): FragmentHomeBinding = FragmentHomeBinding.bind(view)

    override fun initView() {

    }

}