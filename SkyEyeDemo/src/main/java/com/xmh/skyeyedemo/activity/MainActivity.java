package com.xmh.skyeyedemo.activity;

import android.os.Bundle;
import android.util.Log;

import com.easemob.EMCallBack;
import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.xmh.skyeyedemo.R;
import com.xmh.skyeyedemo.application.AppConfig;
import com.xmh.skyeyedemo.base.BaseActivity;
import com.xmh.skyeyedemo.utils.ContactUtil;
import com.xmh.skyeyedemo.utils.LoginUtil;

import java.util.List;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //退出登录并使用username_head登录
        LoginUtil.relogin(AppConfig.getUsername() + LoginUtil.USERNAME_HEADEND, new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.e("xmh-login","head");
                initAfterLogin();
            }

            @Override
            public void onError(int i, String s) {
                //do nothing
            }

            @Override
            public void onProgress(int i, String s) {
                //do nothing
            }
        });
    }

    /**登录后的初始化操作*/
    private void initAfterLogin() {
        //监听添加好友请求，如果是username_开头则同意
        ContactUtil.initContactListener();
        //TODO 获取用户列表并展示
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> usernames = EMContactManager.getInstance().getContactUserNames();//需异步执行
                    if (usernames.size() > 0) {
                        Log.e("xmh-head-contact", usernames.get(0));
                    }
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        //TODO 注册监听
    }

    @Override
    public void onBackPressed() {
        exitApp();
    }
}
