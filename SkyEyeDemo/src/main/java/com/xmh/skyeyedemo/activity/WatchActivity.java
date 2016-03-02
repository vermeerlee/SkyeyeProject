package com.xmh.skyeyedemo.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

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
import com.xmh.skyeyedemo.receiver.NewMessageBroadcastReceiver;
import com.xmh.skyeyedemo.utils.CameraHelper;
import com.xmh.skyeyedemo.utils.CommendUtil;
import com.xmh.skyeyedemo.utils.CommonUtil;
import com.xmh.skyeyedemo.utils.LogUtil;
import com.xmh.skyeyedemo.utils.LoginUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WatchActivity extends BaseActivity {

    @Bind(R.id.surface)
    SurfaceView surface;

    /**
     * 视频请求监听
     */
    private CallReceiver callReceiver;
    private EMVideoCallHelper callHelper;
    private EMCallStateChangeListener callStateListener;
    private EMConnectionListener connectionListener;
    private NewMessageBroadcastReceiver msgReceiver;
    private CameraHelper cameraHelper;
    private AudioManager audioManager;
    private boolean isGoing = false;


    //TODO 监听视频请求如果是username开头则接受

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);
        ButterKnife.bind(this);

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON//保持屏幕常亮
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD//关闭键盘
        );

        //退出登录并使用username_uuid登录,记着拼接下划线(LoginUtil.USERNAME_EYE_DEPART)
        LoginUtil.relogin(AppConfig.getUsername() + LoginUtil.USERNAME_EYE_DEPART + CommonUtil.getUUID(this), new EMCallBack() {
            @Override
            public void onSuccess() {
                LogUtil.e("xmh-login", "eye");
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

//        surface.setZOrderMediaOverlay(true);
//        surface.setZOrderOnTop(true);
        callHelper = EMVideoCallHelper.getInstance();
        cameraHelper = new CameraHelper(this, callHelper, surface.getHolder());

        callHelper.setSurfaceView(null);
        surface.getHolder().addCallback(new LocalCallback());

        //设置视频状态监听
        setCallStateListener();
        setConnectionListener();
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_RINGTONE);
        audioManager.setSpeakerphoneOn(true);

        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(isGoing){
                    //结束通话
                    EMChatManager.getInstance().endCall();
                    isGoing=false;
                }
            }
        },new IntentFilter(CommendUtil.ACTION_END_CALL));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraHelper.stopCapture();
        cameraHelper = null;
        callHelper.setSurfaceView(null);
        if (isGoing) {
            isGoing=false;
            EMChatManager.getInstance().endCall();
        }
        if(callReceiver!=null){
            unregisterReceiver(callReceiver);
        }
    }

    private void setCallStateListener() {

        callStateListener = new EMCallStateChangeListener() {
            @Override
            public void onCallStateChanged(CallState callState, CallError callError) {
                switch (callState) {
                    case DISCONNNECTED: // 电话断了
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                cameraHelper.stopCapture();
                                isGoing = false;
                            }
                        });
                        break;
                }
            }
        };
        EMChatManager.getInstance().addVoiceCallStateChangeListener(callStateListener);

    }

    private void setConnectionListener() {
        connectionListener = new EMConnectionListener() {

            @Override
            public void onConnected() {

            }

            @Override
            public void onDisconnected(int i) {
                if (isGoing) {
                    isGoing = false;
                    EMChatManager.getInstance().endCall();
                }
            }
        };
        EMChatManager.getInstance().addConnectionListener(connectionListener);

    }

    private void initAfterLogin() {
        //初始化视频请求监听
        initCallListener();
        //初始化消息监听
//        initMessageReceiver();
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
        registerReceiver(callReceiver, callFilter);
    }

    /**注册监听*/
    private void initMessageReceiver() {
        msgReceiver = new NewMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
        intentFilter.setPriority(3);
        registerReceiver(msgReceiver, intentFilter);
    }

    @Override
    public void onBackPressed() {
        if (isGoing) {
            Snackbar.make(getWindow().getDecorView(), R.string.node_using, Snackbar.LENGTH_SHORT).show();
            return;
        }
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
        try {
            EMChatManager.getInstance().answerCall();
            cameraHelper.setStartFlag(true);
            openSpeakerOn();
            isGoing = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 本地SurfaceHolder callback
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

    // 打开扬声器
    protected void openSpeakerOn() {
        try {
            if (!audioManager.isSpeakerphoneOn())
                audioManager.setSpeakerphoneOn(true);
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
