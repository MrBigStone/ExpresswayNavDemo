package com.bigstone.expresswaynav.utils;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

/**
 * View相关
 */
public class ViewUtils {

    /**
     * 根据状态栏的高度设置view的高度或者隐藏显示状态
     */
    public static void setStatusBarState(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view.setVisibility(View.VISIBLE);
            setLPHeight(view, SizeUtils.getStatusBarHeight(view.getContext()));
        } else {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * 设置View的高度
     */
    public static View setLPHeight(View view, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params != null) {
            params.height = height;
            view.setLayoutParams(params);
        } else {
            throw new NullPointerException("LayoutParams is null");
        }
        return view;
    }

    /**
     * 设置View的高度
     */
    public static View setLPWidth(View view, int width) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params != null) {
            params.width = width;
            view.setLayoutParams(params);
        } else {
            throw new NullPointerException("LayoutParams is null");
        }
        return view;
    }
}
