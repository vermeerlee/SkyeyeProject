package com.xmh.skyeyedemo.activity;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.easemob.chat.EMCallStateChangeListener;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMVideoCallHelper;
import com.easemob.exceptions.EMServiceNotReadyException;
import com.xmh.skyeyedemo.R;
import com.xmh.skyeyedemo.base.BaseActivity;
import com.xmh.skyeyedemo.bean.UserBmobBean;
import com.xmh.skyeyedemo.utils.CommendUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CallActivity extends BaseActivity {

    public static final String EXTRA_TAG_EYE_BEAN="eyeBean";

    @Bind(R.id.tv_connect_status)TextView tvConnectStatus;
    @Bind(R.id.surface_video)SurfaceView eyeSurface;
    @Bind(R.id.btn_speak)Button btnSpeak;
    @Bind(R.id.btn_over)Button btnOver;
    @Bind(R.id.cl_snackbar)CoordinatorLayout snackbarContainer;
    @Bind(R.id.tv_title)TextView tvTitle;

    private UserBmobBean eyeBean;
    private EMVideoCallHelper callHelper;
    private boolean isStarted = false;
    protected EMCallStateChangeListener callStateListener;
    protected AudioManager audioManager;
    private boolean isSpeaking = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        ButterKnife.bind(this);

        //先话筒静音
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMicrophoneMute(true);


        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON//保持屏幕常亮
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD//关闭键盘
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED//锁屏时显示该window：当按下待机键锁屏后，再次按待机键显示该window而不是解锁手机界面。
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);//当该window要被显示时，如果处于锁屏等状态，则唤醒屏幕

        initView();
        //显示eye图像
        callHelper = EMVideoCallHelper.getInstance();
        callHelper.setVideoOrientation(EMVideoCallHelper.EMVideoOrientation.EMPortrait);//此处注意与ManiFest屏幕方向一致
        callHelper.setSurfaceView(eyeSurface);
        eyeSurface.getHolder().addCallback(new EyeCallBack());

        //设置连接状态监听
        setCallStateListener();

        tvConnectStatus.setText(R.string.connecting);
    }

    private void initView() {
        //显示eye名称
        eyeBean = (UserBmobBean) getIntent().getSerializableExtra(EXTRA_TAG_EYE_BEAN);
        tvTitle.setText(eyeBean.getNickName());
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setMicrophoneMute(false);
        if (callStateListener != null) {
            EMChatManager.getInstance().removeCallStateChangeListener(callStateListener);
        }
        callHelper.setSurfaceView(null);
        eyeSurface = null;
    }

    @OnClick(R.id.btn_speak)
    void onSpeakClick(View view) {
        if (isSpeaking) {
            //静音
            audioManager.setMicrophoneMute(true);
            isSpeaking = false;
            btnSpeak.setText(R.string.speaker_off);
        } else {
            //说话
            audioManager.setMicrophoneMute(false);
            isSpeaking = true;
            btnSpeak.setText(R.string.speaker_on);
        }
    }

    @OnClick(R.id.btn_over)
    void onOverClick(View view) {
        btnOver.setEnabled(false);
        EMChatManager.getInstance().endCall();
        finish();
    }

    /**
     * 设置状态监听
     */
    private void setCallStateListener() {
        callStateListener = new EMCallStateChangeListener() {
            @Override
            public void onCallStateChanged(CallState callState, final CallError callError) {
                switch (callState) {
                    case CONNECTING:
                        //正在连接对方
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvConnectStatus.setText(R.string.connecting);
                            }
                        });
                        break;
                    case CONNECTED:
                        //已建立连接，等待对方响应
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvConnectStatus.setText(R.string.waiting_for_eye);
                            }
                        });
                        break;
                    case ACCEPTED:
                        //已建立连接
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //默认静音
                                openSpeakerOn();
                                audioManager.setMicrophoneMute(true);
                                isSpeaking = false;
                                tvConnectStatus.setVisibility(View.INVISIBLE);
                            }
                        });
                        break;
                    case DISCONNNECTED:
                        //连接结束
                        final CallError error = callError;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (error) {
                                    case REJECTED:
                                        tvConnectStatus.setText(R.string.refused);
                                        break;
                                    case ERROR_TRANSPORT:
                                        tvConnectStatus.setText(R.string.connect_fail);
                                        break;
                                    case ERROR_INAVAILABLE:
                                        tvConnectStatus.setText(R.string.peer_offline);
                                        break;
                                    case ERROR_BUSY:
                                        tvConnectStatus.setText(R.string.busy);
                                        //发送指令让终端结束当前通话
                                        CommendUtil.sendCommendEndCall(eyeBean.getFullUsername(), CommendUtil.COMMEND_END_CALL);
                                        //延迟1秒钟重新连接
                                        tvConnectStatus.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    EMChatManager.getInstance().makeVideoCall(eyeBean.getFullUsername());
                                                } catch (EMServiceNotReadyException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, 1*1000);

                                        break;
                                    case ERROR_NORESPONSE:
                                        tvConnectStatus.setText(R.string.not_answer);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        });
                        break;
                }
            }
        };
        EMChatManager.getInstance().addVoiceCallStateChangeListener(callStateListener);
    }

    /**
     * 打开扬声器
     */
    protected void openSpeakerOn() {
        try {
            if (!audioManager.isSpeakerphoneOn())
                audioManager.setSpeakerphoneOn(true);
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class EyeCallBack implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            holder.setType(SurfaceHolder.SURFACE_TYPE_GPU);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            callHelper.onWindowResize(width, height, format);
            if (!isStarted) {
                try {
                    // 拨打视频通话
                    EMChatManager.getInstance().makeVideoCall(eyeBean.getFullUsername());
                    // 通知cameraHelper可以写入数据
                    isStarted = true;
                } catch (EMServiceNotReadyException e) {
                    Snackbar.make(snackbarContainer, R.string.connect_fail, Snackbar.LENGTH_LONG).show();
                }
            } else {
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    }
}
