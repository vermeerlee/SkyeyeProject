package com.xmh.skyeyedemo.utils;

import android.content.Context;

import com.xmh.skyeyedemo.bean.FileBmobBean;

import java.io.File;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UploadBatchListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by mengh on 2016/3/4 004.
 */
public class UploadUtil {

    /**上传多个文件*/
    public static void uploadFiles(final Context context,final String[] pathArray){
        BmobFile.uploadBatch(context, pathArray, new UploadBatchListener() {
            @Override
            public void onSuccess(List<BmobFile> files, List<String> urls) {
                LogUtil.e("xmh-upload-array","success");
                for(BmobFile file:files) {
                    FileBmobBean fileBmobBean = new FileBmobBean();
                    fileBmobBean.setVideoFile(file);
                    fileBmobBean.save(context);
                }
                //上传完成后删除文件
                for(String path:pathArray) {
                    FileUtil.deleteFile(path);
                }
            }

            @Override
            public void onProgress(int i, int i1, int i2, int i3) {

            }

            @Override
            public void onError(int i, String s) {
                LogUtil.e("xmh-upload-array","error:"+s);
            }
        });
    }

    /**上传单个文件*/
    public static void uploadFile(final Context context, final String filePath){
        //不足1k文件直接删除
        File file = new File(filePath);
        if(file.length()<1024){
            file.delete();
            return;
        }
        LogUtil.e("xmh-record", "upload file start",true);
        final BmobFile bmobFile=new BmobFile(file);
        bmobFile.uploadblock(context, new UploadFileListener() {
            @Override
            public void onSuccess() {
                //上传完成后保存到bmob数据库
                FileBmobBean fileBmobBean = new FileBmobBean();
                fileBmobBean.setVideoFile(bmobFile);
                fileBmobBean.save(context);
                LogUtil.e("xmh-record", "upload file success", true);
                //上传完成后删除文件
                FileUtil.deleteFile(filePath);
            }

            @Override
            public void onFailure(int i, String s) {
                //do nothing
                LogUtil.e("xmh-record", "upload file fail:"+s,true);
            }
        });
    }

    /**检查是否有未上传文件*/
    public static void checkAndUploadVideoFile(Context context){
        uploadFiles(context,FileUtil.scanFilePath());
    }
}
