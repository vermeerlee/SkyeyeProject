package com.xmh.skyeyedemo.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.xmh.skyeyedemo.R;
import com.xmh.skyeyedemo.adapter.EyeListAdapter;
import com.xmh.skyeyedemo.application.AppConfig;
import com.xmh.skyeyedemo.base.BaseActivity;
import com.xmh.skyeyedemo.utils.ContactUtil;
import com.xmh.skyeyedemo.utils.LogUtil;
import com.xmh.skyeyedemo.utils.LoginUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements EMEventListener {

    @Bind(R.id.rv_eye_list)RecyclerView rvEyeList;
    private EyeListAdapter mEyeListAdapter;
    private ContactChangeReceiver receiver=new ContactChangeReceiver();
    private ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        loginWithHead();
        initView();
        initListener();

    }

    private void loginWithHead() {
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setMessage(getString(R.string.loading_device_list));
        loadingDialog.show();
        //退出登录并使用username_head登录
        LoginUtil.relogin(AppConfig.getUsername() + LoginUtil.USERNAME_HEADEND, new EMCallBack() {
            @Override
            public void onSuccess() {
                LogUtil.e("xmh-login", "head");
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

    private void initListener() {
        //注册好友列表改变广播监听
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(ContactUtil.ACTION_CONTACT_CHANGED));
    }

    private void initView() {
        rvEyeList.setLayoutManager(new LinearLayoutManager(this));
        mEyeListAdapter = new EyeListAdapter(this);
        rvEyeList.setAdapter(mEyeListAdapter);
    }

    /**登录后的初始化操作*/
    private void initAfterLogin() {
        //注册一个监听连接状态的listener,连接成功后获取添加好友请求与好友列表
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
        //监听添加好友请求
        ContactUtil.initContactListener(this);
        //获取用户列表并展示
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<String> usernames = EMContactManager.getInstance().getContactUserNames();//需异步执行
                    //将列表保存到本地维护的好友列表
                    ContactUtil.setContacts(usernames);
                    //将列表显示到UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mEyeListAdapter.setEyeList(usernames);
                            loadingDialog.dismiss();

                        }
                    });
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
        LogUtil.e("xmh-event", "event:"+emNotifierEvent.getEvent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver!=null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        }
    }

    /**好友列表改变监听*/
    class ContactChangeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //更新UI
            final List<String> contacts = ContactUtil.getContacts();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mEyeListAdapter.setEyeList(contacts);
                }
            });
        }
    }

}
