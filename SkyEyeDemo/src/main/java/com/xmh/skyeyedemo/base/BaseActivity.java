package com.xmh.skyeyedemo.base;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import butterknife.ButterKnife;

/**
 * Created by mengh on 2016/2/22 022.
 */
public class BaseActivity extends AppCompatActivity {

    /**第一次点击返回的时间*/
    private long clickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //隐藏actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //region 隐藏魅族手机导航栏
        if(Build.BRAND.equals("Meizu")){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
        //endregion
    }

    /**
     * 双击返回键退出应用（在onBackPressed中调用即可）
     */
    public void exitApp() {
        if ((System.currentTimeMillis() - clickTime) > 2000) {
            Snackbar.make(getWindow().getDecorView(), "再按一次后退键退出程序", Snackbar.LENGTH_SHORT).show();
            clickTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }
}
