package com.xmh.skyeyedemo.application;

import android.app.Application;

import com.easemob.chat.EMChat;

/**
 * Created by mengh on 2016/2/23 023.
 */
public class App extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        EMChat.getInstance().init(this);

        /**
         * debugMode == true 时为打开，sdk 会在log里输入调试信息
         * @param debugMode
         * 在做代码混淆的时候需要设置成false
         */
        EMChat.getInstance().setDebugMode(true);//在做打包混淆时，要关闭debug模式，避免消耗不必要的资源
    }

}
