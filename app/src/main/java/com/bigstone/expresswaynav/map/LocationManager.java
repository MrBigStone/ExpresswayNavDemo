package com.bigstone.expresswaynav.map;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * 定位管理
 */
public class LocationManager {

    public static AMapLocation lsatLocation = null;
    //声明AMapLocationClient类对象
    public static AMapLocationClient mLocationClient = null;

    public static void registerLocationListener(Context context) {
        mLocationClient = new AMapLocationClient(context);
        // 配置相关参数
        AMapLocationClientOption option = new AMapLocationClientOption();
        // 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
        option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Transport);
        // 设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        option.setInterval(3000);
        // 设置是否返回地址信息（默认返回地址信息）
        option.setNeedAddress(true);
        // 启动定位
        if (mLocationClient != null) {
            // 设置定位相关参数
            mLocationClient.setLocationOption(option);
            // 设置回调
            mLocationClient.setLocationListener(new AMapLocationListener() {
                @Override
                public void onLocationChanged(AMapLocation aMapLocation) {
                    if (aMapLocation != null) {
                        Log.e("location", "errorCode:" + aMapLocation.getErrorCode() + ",errorInfo:" + aMapLocation.getErrorInfo());
                        Log.e("location", "lat:" + aMapLocation.getLatitude() + ",lng:" + aMapLocation.getLongitude());
                        LocationManager.lsatLocation = aMapLocation;
                    }
                }
            });
            mLocationClient.startLocation();
        }
    }
}
