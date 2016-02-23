package com.xmh.skyeyedemo.application;

/**
 * Created by mengh on 2016/2/23 023.
 */
public class AppConfig {
    private static int UserMode=-1;
    public static final int USER_MODE_HEAD=1;
    public static final int USER_MODE_EYE=2;

    public static void setUserMode(int mode){
        UserMode=mode;
    }
    public static int getUserMode(){
        return UserMode;
    }
}
