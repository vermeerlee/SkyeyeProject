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
    public static void uploadVideoFile(final Context context, String filePath){
        LogUtil.e("xmh-record", "upload file start",true);
        BmobProFile.getInstance(context).upload(filePath, new UploadListener() {
            @Override
            public void onSuccess(String s, String s1, BmobFile bmobFile) {
                //上传完成后保存到数据库
                new FileBmobBean(bmobFile).save(context);
                LogUtil.e("xmh-record", "upload file success",true);
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
