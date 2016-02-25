package com.xmh.skyeyedemo.activity;

import android.os.Bundle;
import android.util.Log;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.xmh.skyeyedemo.R;
import com.xmh.skyeyedemo.application.AppConfig;
import com.xmh.skyeyedemo.base.BaseActivity;
import com.xmh.skyeyedemo.utils.ContactUtil;
import com.xmh.skyeyedemo.utils.LoginUtil;

import java.util.List;

public class MainActivity extends BaseActivity implements EMEventListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //退出登录并使用username_head登录
        LoginUtil.relogin(AppConfig.getUsername() + LoginUtil.USERNAME_HEADEND, new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.e("xmh-login", "head");
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
        //TODO 注册好友列表改变广播监听，在接收到时更新UI
    }

    /**登录后的初始化操作*/
    private void initAfterLogin() {
        //注册一个监听连接状态的listener,连接成功后获取添加好友请求与好友列表
        EMChatManager.getInstance().addConnectionListener(new EMConnectionListener(){
            @Override
            public void onConnected() {
                //登录且EMConnectionListener.onConnected时调用
                // 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
                EMChat.getInstance().setAppInited();
            }

            @Override
            public void onDisconnected(int i) {
                //do nothing
            }
        });
        //监听添加好友请求，如果是username_开头则同意
        ContactUtil.initContactListener();
        //获取用户列表并展示
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> usernames = EMContactManager.getInstance().getContactUserNames();//需异步执行
                    //TODO 将列表保存到本地维护的好友列表
                    //TODO 将列表显示到UI
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        exitApp();
    }

    @Override
    public void onEvent(EMNotifierEvent emNotifierEvent) {
        Log.e("xmh","event");
    }


}
