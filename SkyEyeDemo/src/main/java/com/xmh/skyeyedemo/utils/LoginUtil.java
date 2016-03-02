package com.xmh.skyeyedemo.utils;

import android.util.Log;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.xmh.skyeyedemo.application.AppConfig;

/**
 * Created by mengh on 2016/2/24 024.
 */
public class LoginUtil {

    public static final String USERNAME_HEADEND="_head";
    public static final String USERNAME_EYE_DEPART="_";
    /**使用新用户重新登录,如果提示用户名密码错误则先注册再登录*/
    public static void relogin(final String username, final EMCallBack callBack){
        logout();
        EMChatManager.getInstance().login(username, AppConfig.getPassword(), new EMCallBack() {
            @Override
            public void onSuccess() {
                //登录成功后需要调用
                EMGroupManager.getInstance().loadAllGroups();
                EMChatManager.getInstance().loadAllConversations();
                if(callBack!=null) {
                    callBack.onSuccess();
                }
            }

            @Override
            public void onError(int i, String s) {
                if(i==ERROR_EXCEPTION_INVALID_PASSWORD_USERNAME){
                    //注册username
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EMChatManager.getInstance().createAccountOnServer(username,AppConfig.getPassword());//异步调用
                                relogin(username, null);
                            } catch (EaseMobException e) {
                                e.printStackTrace();
                                //注册失败
                                int errorCode = e.getErrorCode();
                                if (errorCode == EMError.NONETWORK_ERROR) {
                                    LogUtil.e("xmh-regist-error", "网络异常，请检查网络！");
                                } else if (errorCode == EMError.USER_ALREADY_EXISTS) {
                                    LogUtil.e("xmh-regist-error", "用户已存在！");
                                } else if (errorCode == EMError.UNAUTHORIZED) {
                                    LogUtil.e("xmh-regist-error", "注册失败，无权限！");
                                } else {
                                    LogUtil.e("xmh-regist-error", "注册失败: " + e.getMessage());
                                }
                            }
                        }
                    }).start();
                }
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    /**退出当前用户*/
    public static void logout(){
        EMChatManager.getInstance().logout();//此方法为同步方法
    }
}
