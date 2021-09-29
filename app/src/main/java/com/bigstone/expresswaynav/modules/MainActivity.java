package com.bigstone.expresswaynav.modules;

import android.Manifest;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;

import com.amap.api.maps.AMap;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MyLocationStyle;
import com.bigstone.expresswaynav.R;
import com.bigstone.expresswaynav.base.BaseActivity;
import com.bigstone.expresswaynav.databinding.ActivityMainBinding;
import com.bigstone.expresswaynav.map.LocationManager;
import com.bigstone.expresswaynav.utils.SizeUtils;
import com.bigstone.expresswaynav.utils.ViewUtils;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 主页
 */
public class MainActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    private static final int REQUEST_PERMISSIONS = 0x1001;

    // ViewBinding
    private ActivityMainBinding binding = null;

    private AMap mAMap = null;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ViewBinding初始化
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.mapView.onCreate(savedInstanceState);
        initMapView();
        initView();
    }

    /**
     * 地图初始化
     */
    private void initMapView() {
        mAMap = binding.mapView.getMap();
        if (mAMap == null) {
            return;
        }
        // 解决拖动地图后定位小蓝点总是返回到屏幕中心位置的问题  https://lbs.amap.com/api/android-sdk/guide/create-map/mylocation
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        myLocationStyle.showMyLocation(true);
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.img_add_my));
        myLocationStyle.anchor(0.5f, 0.5f);  //设置定位图标的偏移量，这里代表坐标与图标中心重合
        mAMap.setMyLocationStyle(myLocationStyle);
        // 显示当前定位图层
        mAMap.setMyLocationEnabled(true);
        UiSettings uiSettings = mAMap.getUiSettings();
        if (uiSettings != null) {
            uiSettings.setZoomControlsEnabled(false);  //设置缩放按钮是否可见。
            uiSettings.setRotateGesturesEnabled(false); //设置旋转手势是否可用
            uiSettings.setCompassEnabled(false);  //设置指南针是否可见
        }
    }

    /**
     * 视图初始化
     */
    private void initView() {
        binding.menuFl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drawerLayout.openDrawer(binding.sideBarView);
            }
        });

        initDrawerLayout();
    }

    /**
     * 初始化侧边栏
     */
    private void initDrawerLayout() {
        ViewUtils.setLPWidth(binding.sideBarView, (int) (SizeUtils.getScreenWidth(this) * 0.8));
        ViewUtils.setStatusBarState(binding.placeHolderView);
        binding.drawerLayout.setScrollBarFadeDuration(200);  //动画时间
        binding.drawerLayout.setScrimColor(Color.parseColor("#66000000"));
        binding.drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.mapView.onResume();
        if (isFirst) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Android6.0及以上先获取权限再定位
                requestPermission();
            } else {
                // Android6.0以下指定定位
                LocationManager.registerLocationListener(this);
            }
        }
        isFirst = false;
    }

    @Override
    protected void onPause() {
        binding.mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        binding.mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        binding.mapView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        binding.mapView.onSaveInstanceState(outState);
    }


    /**
     * 动态请求权限
     */
    @AfterPermissionGranted(REQUEST_PERMISSIONS)
    private void requestPermission() {
        String[] permissions = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        if (EasyPermissions.hasPermissions(this, permissions)) {
            //true 有权限 开始定位
            LocationManager.registerLocationListener(this);
        } else {
            //false 无权限
            EasyPermissions.requestPermissions(this, "需要权限", REQUEST_PERMISSIONS, permissions);
        }
    }

    /**
     * 请求权限结果
     *
     * @param requestCode  请求code
     * @param permissions  权限列表
     * @param grantResults 授予结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //设置权限请求结果
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    //region EasyPermissions.PermissionCallbacks 结果回调
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode != REQUEST_PERMISSIONS) return;
        LocationManager.registerLocationListener(this);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (requestCode != REQUEST_PERMISSIONS) return;
        Toast.makeText(this, "应用的正常运行需要【定位】【存储】相关权限", Toast.LENGTH_LONG).show();
    }
    //endregion
}