package com.xmh.skyeyedemo.application;

/**
 * Created by mengh on 2016/2/23 023.
 */
public class AppConfig {
    //region user mode
    private static int UserMode=-1;
    public static final int USER_MODE_HEAD=1;
    public static final int USER_MODE_EYE=2;
    public static boolean DEBUG=true;

    public static void setUserMode(int mode){
        UserMode=mode;
    }
    public static int getUserMode(){
        return UserMode;
    }
    //endregion

    //region user info
    /**此username永远没有后缀，即没有head或uuid后缀*/
    private static String username=null;
    private static String password=null;
    public static String getUsername() {
        return username;
    }
    public static void setUsername(String username) {
        AppConfig.username = username;
    }
    public static String getPassword() {
        return password;
    }
    public static void setPassword(String password) {
        AppConfig.password = password;
    }
    //endregion

}
