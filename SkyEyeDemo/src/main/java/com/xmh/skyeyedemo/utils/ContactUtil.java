package com.xmh.skyeyedemo.utils;

import android.text.TextUtils;
import android.util.Log;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.xmh.skyeyedemo.application.AppConfig;

import java.util.List;

/**
 * Created by mengh on 2016/2/24 024.
 */
public class ContactUtil {

    /**初始化好友状态监听,如果是username_开头则同意*/
    public static void initContactListener(){
        EMContactManager.getInstance().setContactListener(new EMContactListener() {

            @Override
            public void onContactAgreed(String username) {
                //好友请求被同意
            }

            @Override
            public void onContactRefused(String username) {
                //好友请求被拒绝
            }

            @Override
            public void onContactInvited(final String username, String reason) {
                Log.e("xmh-head-contact","rec-"+username);
                //收到好友邀请
                if(TextUtils.isEmpty(reason)){
                    return;
                }
                //如果前缀不一致
                if(!AppConfig.getUsername().equals(reason)){
                    return;
                }
                if(!username.startsWith(AppConfig.getUsername())){
                    return;
                }
                //满足条件则同意添加好友
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            EMChatManager.getInstance().acceptInvitation(username);//需异步处理
                        } catch (EaseMobException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void onContactDeleted(List<String> usernameList) {
                //被删除时回调此方法
            }


            @Override
            public void onContactAdded(List<String> usernameList) {
                //增加了联系人时回调此方法
            }
        });
    }
}
