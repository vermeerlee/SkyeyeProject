package com.xmh.skyeye.activity;

import android.os.Bundle;

import com.xmh.skyeye.R;
import com.xmh.skyeye.base.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onBackPressed() {
        exitApp();
    }
}
