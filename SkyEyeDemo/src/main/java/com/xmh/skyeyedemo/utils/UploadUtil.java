package com.xmh.skyeyedemo.utils;

import android.content.Context;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.xmh.skyeyedemo.bean.FileBmobBean;

import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by mengh on 2016/3/4 004.
 */
public class UploadUtil {

    /**上传video文件*/
    public static void uploadVideoFile(final Context context, final String filePath){
        LogUtil.e("xmh-record", "upload file start",true);
        BmobProFile.getInstance(context).upload(filePath, new UploadListener() {
            @Override
            public void onSuccess(String filenameForDownload, String oldUrl, BmobFile bmobFile) {
                //上传完成后保存到bmob数据库
                FileBmobBean fileBmobBean = new FileBmobBean();
                fileBmobBean.setVideoFile(bmobFile);
                fileBmobBean.setFilenameForDownload(filenameForDownload);
                fileBmobBean.save(context);
                LogUtil.e("xmh-record", "upload file success",true);
                //上传完成后删除文件
                FileUtil.deleteFile(filePath);
            }

            @Override
            public void onProgress(int i) {
                //do nothing
            }

            @Override
            public void onError(int i, String s) {
                //do nothing
                LogUtil.e("xmh-record", "upload file fail:"+s,true);
            }
        });
    }

}
