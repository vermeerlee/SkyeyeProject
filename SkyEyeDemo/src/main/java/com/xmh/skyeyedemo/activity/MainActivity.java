package com.xmh.skyeyedemo.activity;

import android.os.Bundle;

import com.xmh.skyeyedemo.R;
import com.xmh.skyeyedemo.base.BaseActivity;

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
