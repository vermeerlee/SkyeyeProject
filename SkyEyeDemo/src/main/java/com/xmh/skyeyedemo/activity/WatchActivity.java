package com.xmh.skyeyedemo.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.chat.EMCallStateChangeListener;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMVideoCallHelper;
import com.easemob.exceptions.EaseMobException;
import com.xmh.skyeyedemo.R;
import com.xmh.skyeyedemo.application.AppConfig;
import com.xmh.skyeyedemo.base.BaseActivity;
import com.xmh.skyeyedemo.utils.CameraHelper;
import com.xmh.skyeyedemo.utils.CommonUtil;
import com.xmh.skyeyedemo.utils.LoginUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WatchActivity extends BaseActivity {

    @Bind(R.id.surface)SurfaceView surface;

    /**
     * 视频请求监听
     */
    private CallReceiver callReceiver;
    //region 视频需要
    private EMVideoCallHelper callHelper;
    //endregion
    private EMCallStateChangeListener callStateListener;
    private CameraHelper cameraHelper;


    //TODO 监听视频请求如果是username开头则接受

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);
        ButterKnife.bind(this);

        //退出登录并使用username_uuid登录,记着拼接下划线(LoginUtil.USERNAME_EYE_DEPART)
        LoginUtil.relogin(AppConfig.getUsername() + LoginUtil.USERNAME_EYE_DEPART + CommonUtil.getUUID(this), new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.e("xmh-login", "eye");
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

        //设置视频状态监听
        addCallStateListener();
    }

    private void addCallStateListener() {

        callStateListener = new EMCallStateChangeListener() {
            @Override
            public void onCallStateChanged(CallState callState, CallError callError) {
                switch (callState) {
                    case DISCONNNECTED: // 电话断了
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cameraHelper.stopCapture();
                            }
                        });
                        break;
                }
            }
        };
        EMChatManager.getInstance().addVoiceCallStateChangeListener(callStateListener);

    }

    private void initAfterLogin() {
        //初始化视频请求监听
        initCallListener();
        //注册一个监听连接状态的listener,连接成功后接受广播
        EMChatManager.getInstance().addConnectionListener(new EMConnectionListener() {
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
        //获取好友列表，如果没有username_head则添加
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //获取好友列表
                    List<String> usernames = EMContactManager.getInstance().getContactUserNames();//需异步执行
                    if (usernames == null || usernames.isEmpty()) {
                        //添加head好友
                        EMContactManager.getInstance().addContact(AppConfig.getUsername() + LoginUtil.USERNAME_HEADEND, AppConfig.getUsername());//需异步处理
                        Log.e("xmh-eye-contact", "request head");
                    }
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 初始化视频请求监听
     */
    private void initCallListener() {
        IntentFilter callFilter = new IntentFilter(EMChatManager.getInstance().getIncomingCallBroadcastAction());
        if (callReceiver == null) {
            callReceiver = new CallReceiver();
        }
        //注册通话广播接收者
        this.registerReceiver(callReceiver, callFilter);
    }

    @Override
    public void onBackPressed() {
        exitApp();
    }

    public class CallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //拨打方username
            String fromUsername = intent.getStringExtra("from");
            //call type
            String type = intent.getStringExtra("type");
            if ("video".equals(type)) { //视频通话
                //如果不是head的请求，则忽略
                if (!fromUsername.equals(AppConfig.getUsername() + LoginUtil.USERNAME_HEADEND))
                    return;
                //接听
                answerCall();
            }
        }

    }

    /**
     * 接听呼叫
     */
    private void answerCall() {
        Log.e("xmh-call", "receive");
        callHelper = EMVideoCallHelper.getInstance();
        cameraHelper = new CameraHelper(this,callHelper,surface.getHolder());
        surface.getHolder().addCallback(new LocalCallback());
        cameraHelper.setStartFlag(true);
        try {
            EMChatManager.getInstance().answerCall();
//            cameraHelper.setStartFlag(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 本地SurfaceHolder callback
     *
     */
    class LocalCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            cameraHelper.startCapture();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    }

}
