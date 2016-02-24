package com.xmh.skyeyedemo.application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;

import java.util.Iterator;
import java.util.List;

/**
 * Created by mengh on 2016/2/23 023.
 */
public class App extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        //region 初始化环信SDK前的准备
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // 如果app启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回

        if (processAppName == null ||!processAppName.equalsIgnoreCase("com.xmh.skyeyedemo")) {
            Log.e("SkyEyeDemo", "enter the service process!");
            //"com.easemob.chatuidemo"为demo的包名，换到自己项目中要改成自己包名

            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }
        //endregion
        //关闭环信自动登录
        EMChat.getInstance().setAutoLogin(false);
        //初始化环信SDK
        EMChat.getInstance().init(this);
        //设置使用环信的好友体系
        EMChatManager.getInstance().getChatOptions().setUseRoster(true);

        /**
         * debugMode == true 时为打开，sdk 会在log里输入调试信息
         * @param debugMode
         * 在做代码混淆的时候需要设置成false
         */
        EMChat.getInstance().setDebugMode(true);//在做打包混淆时，要关闭debug模式，避免消耗不必要的资源
    }

    /**初始化环信SDK前按需调用*/
    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    // Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
                    // info.processName +"  Label: "+c.toString());
                    // processName = c.toString();
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }


}
