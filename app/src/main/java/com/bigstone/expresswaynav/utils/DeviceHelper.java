package com.bigstone.expresswaynav.utils;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeviceHelper {
    private final static String TAG = "DeviceHelper";
    private final static String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_FLYME_VERSION_NAME = "ro.build.display.id";
    private static final String KEY_EMUI_VERSION_NAME = "ro.build.version.emui";
    private static final String KEY_OPPO_VERSION_NAME = "ro.build.version.opporom";
    private static final String KEY_VIVO_VERSION_NAME = "ro.vivo.os.version";
    private static final String KEY_SMARTISAN_VERSION_NAME = "ro.smartisan.version";
    private final static String FLYME = "flyme";
    private final static String ZTEC2016 = "zte c2016";
    private final static String ZUKZ1 = "zuk z1";
    private final static String MEIZUBOARD[] = {"m9", "M9", "mx", "MX"};
    private static String sMiuiVersionName;
    private static String sFlymeVersionName;
    private static String sEmuiVersionName;
    private static String sOppoVersionName;
    private static String sVivoVersionName;
    private static String sSmartisanVersionName;

    private static boolean sIsTabletChecked = false;
    private static boolean sIsTabletValue = false;

    static {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(new File(Environment.getRootDirectory(), "build.prop"));
            Properties properties = new Properties();
            properties.load(fileInputStream);
            Class<?> clzSystemProperties = Class.forName("android.os.SystemProperties");
            Method getMethod = clzSystemProperties.getDeclaredMethod("get", String.class);
            // miui
            sMiuiVersionName = getLowerCaseName(properties, getMethod, KEY_MIUI_VERSION_NAME);
            //flyme
            sFlymeVersionName = getLowerCaseName(properties, getMethod, KEY_FLYME_VERSION_NAME);
            //EMUI
            sEmuiVersionName = getLowerCaseName(properties, getMethod, KEY_EMUI_VERSION_NAME);
            //oppo
            sOppoVersionName = getLowerCaseName(properties, getMethod, KEY_OPPO_VERSION_NAME);
            //vivo
            sVivoVersionName = getLowerCaseName(properties, getMethod, KEY_VIVO_VERSION_NAME);
            //smartisan
            sSmartisanVersionName = getLowerCaseName(properties, getMethod, KEY_SMARTISAN_VERSION_NAME);
        } catch (Exception e) {

        } finally {
            close(fileInputStream);
        }
    }

    private static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean _isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >=
                Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * 判断是否为平板设备
     */
    public static boolean isTablet(Context context) {
        if (sIsTabletChecked) {
            return sIsTabletValue;
        }
        sIsTabletValue = _isTablet(context);
        sIsTabletChecked = true;
        return sIsTabletValue;
    }

    /**
     * 判断是否是flyme系统
     */
    public static boolean isFlyme() {
        return !TextUtils.isEmpty(sFlymeVersionName) && sFlymeVersionName.contains(FLYME);
    }

    /**
     * 判断是否是emui系统
     */
    public static boolean isEMUI() {
        return !TextUtils.isEmpty(sEmuiVersionName);
    }

    /**
     * 判断是否是oppo系统
     */
    public static boolean isOppo() {
        return !TextUtils.isEmpty(sOppoVersionName);
    }

    /**
     * 判断是否是vivo系统
     */
    public static boolean isVivo() {
        return !TextUtils.isEmpty(sVivoVersionName);
    }

    /**
     * 判断是否是smartisan系统
     */
    public static boolean isSmartisan() {
        return !TextUtils.isEmpty(sSmartisanVersionName);
    }

    /**
     * 判断是否是MIUI系统
     */
    public static boolean isMIUI() {
        return !TextUtils.isEmpty(sMiuiVersionName);
    }

    public static boolean isMIUIV5() {
        return "v5".equals(sMiuiVersionName);
    }

    public static boolean isMIUIV6() {
        return "v6".equals(sMiuiVersionName);
    }

    public static boolean isMIUIV7() {
        return "v7".equals(sMiuiVersionName);
    }

    public static boolean isMIUIV8() {
        return "v8".equals(sMiuiVersionName);
    }

    public static boolean isMIUIV9() {
        return "v9".equals(sMiuiVersionName);
    }

    public static boolean isFlymeVersionHigher5_2_4() {
        //查不到默认高于5.2.4
        boolean isHigher = true;
        if (sFlymeVersionName != null && !sFlymeVersionName.equals("")) {
            Pattern pattern = Pattern.compile("(\\d+\\.){2}\\d");
            Matcher matcher = pattern.matcher(sFlymeVersionName);
            if (matcher.find()) {
                String versionString = matcher.group();
                if (versionString != null && !versionString.equals("")) {
                    String[] version = versionString.split("\\.");
                    if (version.length == 3) {
                        if (Integer.valueOf(version[0]) < 5) {
                            isHigher = false;
                        } else if (Integer.valueOf(version[0]) > 5) {
                            isHigher = true;
                        } else {
                            if (Integer.valueOf(version[1]) < 2) {
                                isHigher = false;
                            } else if (Integer.valueOf(version[1]) > 2) {
                                isHigher = true;
                            } else {
                                if (Integer.valueOf(version[2]) < 4) {
                                    isHigher = false;
                                } else if (Integer.valueOf(version[2]) >= 5) {
                                    isHigher = true;
                                }
                            }
                        }
                    }

                }
            }
        }
        return isMeizu() && isHigher;
    }

    /**
     * 判断是否为魅族
     */
    public static boolean isMeizu() {
        return isPhone(MEIZUBOARD) || isFlyme();
    }

    /**
     * 判断是否为小米
     */
    public static boolean isXiaomi() {
        return Build.BRAND.toLowerCase().contains("xiaomi");
    }


    /**
     * 判断是否为 ZUK Z1 和 ZTK C2016。
     * 两台设备的系统虽然为 android 6.0，但不支持状态栏icon颜色改变，因此经常需要对它们进行额外判断。
     */
    public static boolean isZUKZ1() {
        final String board = Build.MODEL;
        return board != null && board.toLowerCase().contains(ZUKZ1);
    }

    public static boolean isZTKC2016() {
        final String board = Build.MODEL;
        return board != null && board.toLowerCase().contains(ZTEC2016);
    }

    public static boolean is360() {
        final String board = Build.MANUFACTURER;
        return board != null && (board.toLowerCase().contains("QIKU")
                || board.toLowerCase().contains("360"));
    }

    private static boolean isPhone(String[] boards) {
        final String board = Build.BOARD;
        if (board == null) {
            return false;
        }
        for (String board1 : boards) {
            if (board.equals(board1)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断悬浮窗权限（目前主要用户魅族与小米的检测）。
     */
    public static boolean isFloatWindowOpAllowed(Context context) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            return checkOp(context, 24);  // 24 是AppOpsManager.OP_SYSTEM_ALERT_WINDOW 的值，该值无法直接访问
        } else {
            try {
                return (context.getApplicationInfo().flags & 1 << 27) == 1 << 27;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    @TargetApi(19)
    private static boolean checkOp(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= Build.VERSION_CODES.KITKAT) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Method method = manager.getClass().getDeclaredMethod("checkOp", int.class, int.class, String.class);
                int property = (Integer) method.invoke(manager, op,
                        Binder.getCallingUid(), context.getPackageName());
                return AppOpsManager.MODE_ALLOWED == property;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Nullable
    private static String getLowerCaseName(Properties p, Method get, String key) {
        String name = p.getProperty(key);
        if (name == null) {
            try {
                name = (String) get.invoke(null, key);
            } catch (Exception ignored) {
            }
        }
        if (name != null) name = name.toLowerCase();
        return name;
    }
}
