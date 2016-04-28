package com.xmh.skyeyedemo.utils;

import android.os.Environment;

import com.xmh.skyeyedemo.application.AppConfig;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private static final String DOWNLOAD_FILE_PATH = "/download/";
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

    /**生成完整文件名*/
    public static String generateVideoFileFullName() {
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

    /**创建nomedia文件*/
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

    /**获取log保存路径*/
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

    /**根据文件名解析日期时间*/
    public static String parseDateFromFilename(String str){
        str=str.substring(AppConfig.getUsername().length() + FileUtil.FILE_NAME_START.length() + FileUtil.FILE_NAME_DEPART.length() * 2, str.length() - FileUtil.FILE_NAME_END.length());
        String[] strings = str.split(FileUtil.FILE_NAME_DEPART);
        str=new StringBuilder().append(strings[0]).append("-").append(strings[1]).append("-").append(strings[2]).append(" ").append(strings[3]).append(":").append(strings[4]).append(":").append(strings[5]).toString();
        return str;
    }

    /**获取下载文件保存路径*/
    public static String getDownloadPath(){
        if (checkSDCardAvailable()) {
            return Environment.getExternalStorageDirectory() + DOWNLOAD_FILE_PATH ;
        }
        return Environment.getDataDirectory() + DOWNLOAD_FILE_PATH;

    }

    public static void copyFile(String srcPath,String dstPath){
        try {
            File oldfile = new File(srcPath);
            if (oldfile.exists()) { //文件存在时
                File dir=new File(new File(dstPath).getParent());
                if(!dir.exists()){//创建目标路径
                    dir.mkdir();
                }
                File file=new File(dstPath);
                if(!file.exists()){//创建目标文件
                    file.createNewFile();
                }
                int bytesum = 0;
                int byteread = 0;
                InputStream inStream = new FileInputStream(srcPath); //读入原文件
                FileOutputStream outStream = new FileOutputStream(file);
                byte[] buffer = new byte[1444];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    outStream.write(buffer, 0, byteread);
                }
                outStream.flush();
                outStream.close();
                inStream.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(String fullFilename){
        File file = new File(fullFilename);
        if(file.exists()){
            file.delete();
        }
    }

    /**video文件过滤规则*/
    static class VideoFileFilter implements FileFilter {
        @Override
        public boolean accept(File file) {
            //如果目录则pass
            if (file.isDirectory())
                return false;
            String fileName = file.getName();
            //如果文件正在使用则pass
            if(fileName.equals(CameraHelper.getCurrentVideoFileName()))
                return false;
            //如果文件名长度不符则pass
            if (fileName.length() != FileUtil.generateFileNameByDateTime().length())
                return false;
            //如果不以规定开头则pass
            if (!fileName.startsWith(FileUtil.FILE_NAME_START))
                return false;
            //如果不以规定结尾则pass
            if (!fileName.endsWith(FileUtil.FILE_NAME_END))
                return false;
            //TODO 用正则表达式过滤
            return true;
        }
    }

    /**扫描未上传文件*/
    public static String[] scanFilePath() {
        LogUtil.e("xmh-file","scan");
        List<String> pathList=new ArrayList<>();
        File sdFolder = new File(getVideoFilePathOnSD());
        File[] list1 = sdFolder.listFiles(new VideoFileFilter());
        if(list1!=null&&list1.length>0) {
            for (File file : list1) {
                pathList.add(file.getAbsolutePath());
            }
        }
        File phoneFolder = new File(getVideoFilePathOnPhone());
        File[] list2 = phoneFolder.listFiles(new VideoFileFilter());
        if(list2!=null&&list2.length>0) {
            for (File file : list2) {
                pathList.add(file.getAbsolutePath());
            }
        }
        return pathList.toArray(new String[pathList.size()]);
    }

}
