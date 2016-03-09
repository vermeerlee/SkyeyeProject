package com.xmh.skyeyedemo.utils;

import android.os.Environment;

import com.xmh.skyeyedemo.application.AppConfig;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mengh on 2016/3/2 002.
 */
public class FileUtil {

    public static final String FILE_NAME_START = "skyeye";
    public static final String FILE_NAME_DEPART = "-";
    public static final String FILE_NAME_END = ".mp4";
    private static final String FILE_ROOT_PATH = "/skyeye/";
    private static final String VIDEO_FILE_PATH = "videofile/";
    private static final String LOG_FILE_PATH = "logfile/";
    private static final String NOMEDIA_FILENAME = ".nomedia";

    public static boolean checkSDCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取可用video存储路径
     */
    public static String getVideoFilePath() {
        if (checkSDCardAvailable()) {
            return getVideoFilePathOnSD();
        }
        return getVideoFilePathOnPhone();
    }

    /**
     * 获取SD卡上的video存储路径
     */
    public static String getVideoFilePathOnSD() {
        return Environment.getExternalStorageDirectory() + FILE_ROOT_PATH + VIDEO_FILE_PATH;
    }

    /**
     * 获取手机上的video存储路径
     */
    public static String getVideoFilePathOnPhone() {
        return Environment.getDataDirectory() + FILE_ROOT_PATH + VIDEO_FILE_PATH;
    }

    /**
     * 根据时间生成video文件名
     */
    public static String generateFileNameByDateTime() {
        StringBuilder stringBuilder = new StringBuilder()
                .append("yyyy").append(FILE_NAME_DEPART)
                .append("MM").append(FILE_NAME_DEPART)
                .append("dd").append(FILE_NAME_DEPART)
                .append("HH").append(FILE_NAME_DEPART)
                .append("mm").append(FILE_NAME_DEPART)
                .append("ss");
        String result = new SimpleDateFormat(stringBuilder.toString()).format(new Date());
        stringBuilder = new StringBuilder(FILE_NAME_START).append(FILE_NAME_DEPART)
                .append(AppConfig.getUsername()).append(FILE_NAME_DEPART)
                .append(result).append(FILE_NAME_END);
        //skyeye-username-yyyy-MM-dd-HH-mm-ss.mp4
        return stringBuilder.toString();
    }

    public static String getVideoFileFullName() {
        String fullFilename = null;
        try {
            File folder = new File(getVideoFilePath());
            if (!folder.exists()) {
                folder.mkdirs();
            }
            fullFilename = getVideoFilePath() + generateFileNameByDateTime();
            File file = new File(fullFilename);
            if (!file.exists()) {
                file.createNewFile();
                LogUtil.e("xmh-record", "create file",true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        checkAndCreateNomedia();
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fullFilename;
    }

    public static void checkAndCreateNomedia() {
        try {
            File folder = new File(getVideoFilePathOnPhone());
            if (folder.exists()) {
                File file = new File(getVideoFilePathOnPhone() + NOMEDIA_FILENAME);
                if (!file.exists()) file.createNewFile();
            }
            folder = new File(getVideoFilePathOnSD());
            if (folder.exists()) {
                File file = new File(getVideoFilePathOnSD() + NOMEDIA_FILENAME);
                if (!file.exists()) file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getLogFilePath() {
        String path=null;
        if (checkSDCardAvailable()) {
            path=Environment.getExternalStorageDirectory() + FILE_ROOT_PATH + LOG_FILE_PATH;
        }else{
            path=Environment.getDataDirectory() + FILE_ROOT_PATH + LOG_FILE_PATH;
        }
        File folder = new File(path);
        if(!folder.exists()){
            folder.mkdirs();
        }
        return path;
    }

    public static String parseDateFromFilename(String str){
        str=str.substring(AppConfig.getUsername().length() + FileUtil.FILE_NAME_START.length() + FileUtil.FILE_NAME_DEPART.length() * 2, str.length() - FileUtil.FILE_NAME_END.length());
        String[] strings = str.split(FileUtil.FILE_NAME_DEPART);
        str=new StringBuilder().append(strings[0]).append("-").append(strings[1]).append("-").append(strings[2]).append(" ").append(strings[3]).append(":").append(strings[4]).append(":").append(strings[5]).toString();
        return str;
    }
}
