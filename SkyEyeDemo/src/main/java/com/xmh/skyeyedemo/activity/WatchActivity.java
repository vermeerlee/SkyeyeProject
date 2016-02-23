package com.xmh.skyeyedemo.activity;

import android.os.Bundle;

import com.xmh.skyeyedemo.R;
import com.xmh.skyeyedemo.base.BaseActivity;

public class WatchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);

        //TODO 退出登录并使用username_uuid登录,如果登录失败则注册username_uuid并登录
        //TODO 获取好友列表，如果没有username_head则添加
    }

    @Override
    public void onBackPressed() {
        exitApp();
    }
}
