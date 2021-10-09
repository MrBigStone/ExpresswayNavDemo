package com.bigstone.expresswaynav.modules

import android.Manifest
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import com.amap.api.maps.AMap
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.MyLocationStyle
import com.bigstone.expresswaynav.R
import com.bigstone.expresswaynav.base.BaseActivity
import com.bigstone.expresswaynav.databinding.ActivityMainBinding
import com.bigstone.expresswaynav.ext.binding
import com.bigstone.expresswaynav.map.LocationManager
import com.bigstone.expresswaynav.utils.SizeUtils
import com.bigstone.expresswaynav.utils.ViewUtils
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks

/**
 * 主页
 */
class MainActivity : BaseActivity(), PermissionCallbacks {
    // ViewBinding
    private val binding: ActivityMainBinding by binding(ActivityMainBinding::inflate)
    private var mAMap: AMap? = null
    private var isFirst = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ViewBinding初始化
        binding.mapView.onCreate(savedInstanceState)
        initMapView()
        initView()
    }

    /**
     * 地图初始化
     */
    private fun initMapView() {
        mAMap = binding.mapView.map
        // 解决拖动地图后定位小蓝点总是返回到屏幕中心位置的问题  https://lbs.amap.com/api/android-sdk/guide/create-map/mylocation
        val myLocationStyle = MyLocationStyle().apply {
            myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER)
            showMyLocation(true)
            myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.img_add_my))
            anchor(0.5f, 0.5f) //设置定位图标的偏移量，这里代表坐标与图标中心重合
        }
        mAMap?.myLocationStyle = myLocationStyle
        // 显示当前定位图层
        mAMap?.isMyLocationEnabled = true
        val uiSettings = mAMap?.uiSettings
        uiSettings?.apply {
            isZoomControlsEnabled = false //设置缩放按钮是否可见。
            isRotateGesturesEnabled = false //设置旋转手势是否可用
            isCompassEnabled = false //设置指南针是否可见
        }
    }

    /**
     * 视图初始化
     */
    private fun initView() {
        binding.menuFl.setOnClickListener { binding.drawerLayout.openDrawer(binding.sideBarView) }
        initDrawerLayout()
    }

    /**
     * 初始化侧边栏
     */
    private fun initDrawerLayout() {
        ViewUtils.setLPWidth(binding.sideBarView, (SizeUtils.getScreenWidth(this) * 0.8).toInt())
        ViewUtils.setStatusBarState(binding.placeHolderView)
        binding.drawerLayout.scrollBarFadeDuration = 200 //动画时间
        binding.drawerLayout.setScrimColor(Color.parseColor("#66000000"))
        binding.drawerLayout.addDrawerListener(object : DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {}
            override fun onDrawerStateChanged(newState: Int) {}
        })
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
        if (isFirst) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Android6.0及以上先获取权限再定位
                requestPermission()
            } else {
                // Android6.0以下指定定位
                LocationManager.registerLocationListener(this)
            }
        }
        isFirst = false
    }

    override fun onPause() {
        binding.mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        binding.mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        binding.mapView.onLowMemory()
        super.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    /**
     * 动态请求权限
     */
    @AfterPermissionGranted(REQUEST_PERMISSIONS)
    private fun requestPermission() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (EasyPermissions.hasPermissions(this, *permissions)) {
            //true 有权限 开始定位
            LocationManager.registerLocationListener(this)
        } else {
            //false 无权限
            EasyPermissions.requestPermissions(this, "需要权限", REQUEST_PERMISSIONS, *permissions)
        }
    }

    /**
     * 请求权限结果
     *
     * @param requestCode  请求code
     * @param permissions  权限列表
     * @param grantResults 授予结果
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //设置权限请求结果
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    //region EasyPermissions.PermissionCallbacks 结果回调
    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        if (requestCode != REQUEST_PERMISSIONS) return
        LocationManager.registerLocationListener(this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (requestCode != REQUEST_PERMISSIONS) return
        Toast.makeText(this, "应用的正常运行需要【定位】【存储】相关权限", Toast.LENGTH_LONG).show()
    } //endregion

    companion object {
        private const val REQUEST_PERMISSIONS = 0x1001
    }
}