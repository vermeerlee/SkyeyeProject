package com.xmh.skyeyedemo.base;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.util.NetUtils;
import com.xmh.skyeyedemo.R;
import com.xmh.skyeyedemo.activity.LoginActivity;
import com.xmh.skyeyedemo.utils.LoginUtil;

/**
 * Created by mengh on 2016/2/22 022.
 */
public class BaseActivity extends AppCompatActivity {

    /**第一次点击返回的时间*/
    private long clickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ButterKnife.bind(this);

        //透明状态栏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
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
        //注册一个监听连接状态的listener
        EMChatManager.getInstance().addConnectionListener(new MyConnectionListener());
    }

    /**
     * 双击返回键退出应用（在onBackPressed中调用即可）
     */
    public void exitApp() {
        if ((System.currentTimeMillis() - clickTime) > 2000) {
            Snackbar.make(getWindow().getDecorView(), "再按一次后退键退出程序", Snackbar.LENGTH_SHORT).show();
            clickTime = System.currentTimeMillis();
        } else {
            LoginUtil.logout();
            super.onBackPressed();
        }
    }

    //实现ConnectionListener接口
    private class MyConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
            //已连接到服务器
            Log.e("xmh-connected","connected");
        }
        @Override
        public void onDisconnected(final int error) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if(error == EMError.USER_REMOVED){
                        // 显示帐号已经被移除
                        Toast.makeText(BaseActivity.this, R.string.user_removed,Toast.LENGTH_LONG).show();
                        startActivity(new Intent(BaseActivity.this, LoginActivity.class));
                        BaseActivity.this.finish();
                    }else if (error == EMError.CONNECTION_CONFLICT) {
                        // 显示帐号在其他设备登陆
                        Toast.makeText(BaseActivity.this, R.string.relogined,Toast.LENGTH_LONG).show();
                        startActivity(new Intent(BaseActivity.this, LoginActivity.class));
                        BaseActivity.this.finish();
                    } else {
                        if (NetUtils.hasNetwork(BaseActivity.this)){
                        //连接不到聊天服务器
                        }
                        else{
                        //当前网络不可用，请检查网络设置
                        }
                    }
                }
            });
        }
    }
}
