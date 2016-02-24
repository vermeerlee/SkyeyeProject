package com.xmh.skyeyedemo.activity;

import android.os.Bundle;
import android.util.Log;

import com.easemob.EMCallBack;
import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.xmh.skyeyedemo.R;
import com.xmh.skyeyedemo.application.AppConfig;
import com.xmh.skyeyedemo.base.BaseActivity;
import com.xmh.skyeyedemo.utils.CommonUtil;
import com.xmh.skyeyedemo.utils.LoginUtil;

import java.util.List;

public class WatchActivity extends BaseActivity {

    //TODO 监听视频请求如果是username开头则接受

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);

        //退出登录并使用username_uuid登录,记着拼接下划线(LoginUtil.USERNAME_EYE_DEPART)
        LoginUtil.relogin(AppConfig.getUsername() + LoginUtil.USERNAME_EYE_DEPART + CommonUtil.getUUID(this), new EMCallBack() {
            @Override
            public void onSuccess() {
                //获取好友列表，如果没有username_head则添加
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //获取好友列表
                            List<String> usernames = EMContactManager.getInstance().getContactUserNames();//需异步执行
                            if (usernames == null || usernames.isEmpty()) {
                                Log.e("xmh-eye-contact", "request head");
                                //添加head好友
                                EMContactManager.getInstance().addContact(AppConfig.getUsername() + LoginUtil.USERNAME_HEADEND, AppConfig.getUsername());//需异步处理
                            }
                        } catch (EaseMobException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
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

    @Override
    public void onBackPressed() {
        exitApp();
    }
}
