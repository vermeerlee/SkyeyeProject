package com.xmh.skyeyedemo.utils;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mengh on 2016/3/2 002.
 */
public class FileUtil {

    private static final String FILE_NAME_START = "skyeye";
    private static final String FILE_NAME_DEPART = "-";
    private static final String FILE_NAME_END = ".mp4";
    private static final String FILE_ROOT_PATH = "/skyeye/";
    private static final String VIDEO_FILE_PATH = "videofile/";
    private static final String NOMEDIA_FILENAME=".nomedia";

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
        return FILE_NAME_START+FILE_NAME_DEPART+result+FILE_NAME_END;
    }

    public static String getVideoFileFullName() {
        String fullFilename=null;
        try {
            File folder=new File(getVideoFilePath());
            if(!folder.exists()){
                folder.mkdirs();
            }
            fullFilename = getVideoFilePath() + generateFileNameByDateTime();
            File file = new File(fullFilename);
            if (!file.exists()) {
                file.createNewFile();
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

    public static void checkAndCreateNomedia(){
        try {
            File folder = new File(getVideoFilePathOnPhone());
            if (folder.exists()) {
                File file=new File(getVideoFilePathOnPhone()+NOMEDIA_FILENAME);
                if(!file.exists())file.createNewFile();
            }
            folder=new File(getVideoFilePathOnSD());
            if(folder.exists()){
                File file=new File(getVideoFilePathOnSD()+NOMEDIA_FILENAME);
                if(!file.exists())file.createNewFile();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
