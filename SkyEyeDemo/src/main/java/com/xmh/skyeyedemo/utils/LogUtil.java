package com.xmh.skyeyedemo.utils;

import android.util.Log;

import com.xmh.skyeyedemo.application.AppConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class LogUtil {

    public static void d(String tag, String msg) {
        if (AppConfig.DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (AppConfig.DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (AppConfig.DEBUG) {
            Log.w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (AppConfig.DEBUG) {
            Log.w(tag, msg, tr);
        }
    }

    public static void e(String tag, String msg, boolean toFile) {
        e(tag, msg);
        if(!toFile) {
            return;
        }
        String logFilePath = FileUtil.getLogFilePath();
        String logFileName = logFilePath + tag + ".txt";
        File logFile = new File(logFileName);
            try {
                if(!logFile.exists()) {
                    logFile.createNewFile();
                }
                FileWriter fileWriter = new FileWriter(logFileName, true);
                fileWriter.write(new Date().toString()+"::"+tag+"::"+msg+"\n");
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static void e(String tag, String msg) {
        if (AppConfig.DEBUG) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (AppConfig.DEBUG) {
            Log.e(tag, msg, tr);
        }
    }

    public static void printException(Throwable e) {
        if (AppConfig.DEBUG) {
            e.printStackTrace();
            return;
        }
    }


}
