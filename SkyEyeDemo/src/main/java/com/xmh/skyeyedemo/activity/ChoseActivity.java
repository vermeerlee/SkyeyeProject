package com.xmh.skyeyedemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.xmh.skyeyedemo.R;
import com.xmh.skyeyedemo.application.AppConfig;
import com.xmh.skyeyedemo.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChoseActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chose);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_head)
    void onHeadClick(View view){
        AppConfig.setUserMode(AppConfig.USER_MODE_HEAD);
        Snackbar.make(getWindow().getDecorView(),R.string.head_mode,Snackbar.LENGTH_SHORT).show();
        startActivity(new Intent(ChoseActivity.this, MainActivity.class));
        finish();
    }

    @OnClick(R.id.btn_eye)
    void onEyeClick(View eye){
        AppConfig.setUserMode(AppConfig.USER_MODE_EYE);
        Snackbar.make(getWindow().getDecorView(),"TODOing",Snackbar.LENGTH_SHORT).show();
        startActivity(new Intent(ChoseActivity.this, WatchActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        exitApp();
    }
}
