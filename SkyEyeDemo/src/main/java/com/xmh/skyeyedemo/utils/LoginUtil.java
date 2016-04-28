package com.xmh.skyeyedemo.utils;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.xmh.skyeyedemo.application.App;
import com.xmh.skyeyedemo.application.AppConfig;
import com.xmh.skyeyedemo.bean.UserBmobBean;

/**
 * Created by mengh on 2016/2/24 024.
 */
public class LoginUtil {

    public static final String USERNAME_HEADEND="_head";
    public static final String USERNAME_EYE_DEPART="_";

    /**登录*/
    public static void login(final String username, final String password, final EMCallBack callBack) {
        EMChatManager.getInstance().login(username, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                //保存用户名密码
                AppConfig.setUsername(username);
                AppConfig.setPassword(password);
                callBack.onSuccess();
            }

            @Override
            public void onError(int i, String s) {
                callBack.onError(i,s);
            }

            @Override
            public void onProgress(int i, String s) {
                callBack.onProgress(i,s);
            }
        });
    }

    /**使用新用户(eye或head)重新登录,如果提示用户名密码错误则先注册再登录*/
    public static void relogin(final String username, final EMCallBack callBack){
        logout();
        EMChatManager.getInstance().login(username, AppConfig.getPassword(), new EMCallBack() {
            @Override
            public void onSuccess() {
                //保存全用户名（eye或head）
                AppConfig.setFullUsername(username);
                //登录成功后需要调用
                EMGroupManager.getInstance().loadAllGroups();
                EMChatManager.getInstance().loadAllConversations();
                if (callBack != null) {
                    callBack.onSuccess();
                }
            }

            @Override
            public void onError(int i, String s) {
                if (i == ERROR_EXCEPTION_INVALID_PASSWORD_USERNAME) {
                    //注册username
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EMChatManager.getInstance().createAccountOnServer(username, AppConfig.getPassword());//异步调用
                                relogin(username, callBack);
                                //region 保存用户信息到Bmob
                                UserBmobBean userBmobBean = new UserBmobBean();
                                userBmobBean.setFullUsername(username);
                                userBmobBean.setNickName(CommonUtil.getPhoneName());
                                userBmobBean.save(App.getContext());
                                //endregion
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
