package com.bigstone.expresswaynav.modules.home

import android.view.View
import android.widget.ImageView
import androidx.fragment.app.FragmentManager
import com.bigstone.expresswaynav.R
import com.bigstone.expresswaynav.base.BaseFragment
import com.bigstone.expresswaynav.databinding.FragmentHomeBinding
import com.bigstone.expresswaynav.ext.code.CodeUtil.encodeBarCodeAsBitmap
import com.bigstone.expresswaynav.ext.code.CodeUtil.encodeQRCodeAsBitmap
import com.bigstone.expresswaynav.utils.SizeUtils.*

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    companion object {
        fun start(
            fm: FragmentManager,
            containerId: Int
        ) {
            fm.beginTransaction()
                .add(containerId, HomeFragment(), HomeFragment::class.simpleName)
                .addToBackStack(HomeFragment::class.simpleName)
                .commitAllowingStateLoss()
        }
    }

    override fun initBinding(view: View): FragmentHomeBinding = FragmentHomeBinding.bind(view)

    override fun initView() {
        val context = context ?: return
        val barBitmap = encodeBarCodeAsBitmap(
            contents = "628879691923",
            desiredHeight = dp2px(context, 200f),
            desiredWidth = getScreenWidth(context)
        )
        barBitmap?.let {
            vb.barCodeIv.scaleType = ImageView.ScaleType.FIT_XY
            vb.barCodeIv.setImageBitmap(it)
        }

        val qrBitmap = encodeQRCodeAsBitmap(
            contents = "这是一个二维码，试试可以放多少东西",
            context = context,
            size = dp2px(context, 200f)
        )
        qrBitmap?.let {
            vb.qrCodeIv.scaleType = ImageView.ScaleType.FIT_XY
            vb.qrCodeIv.setImageBitmap(it)
        }
        vb.root.setPadding(0, getStatusBarHeight(context), 0, 0)
        vb.root.clipToPadding = true
    }

}