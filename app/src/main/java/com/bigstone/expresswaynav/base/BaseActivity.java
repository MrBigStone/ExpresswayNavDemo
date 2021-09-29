package com.bigstone.expresswaynav.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.bigstone.expresswaynav.utils.StatusBarHelper;

public class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarHelper.translucent(this);
        StatusBarHelper.setStatusBarLightMode(this);
    }
}
