package com.xmh.skyeyedemo.bean;

import com.xmh.skyeyedemo.application.AppConfig;

import java.io.File;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by mengh on 2016/3/4 004.
 */
public class FileBean extends BmobObject{

    /**使用用户名初始化，不允许修改*/
    private String username= AppConfig.getUsername();

    /**文件信息*/
    private BmobFile videoFile;

    //region constructor
    public FileBean(BmobFile videoFile) {
        this.videoFile = videoFile;
    }

    public FileBean(String filePath) {
        this.videoFile=new BmobFile(new File(filePath));
    }
    //endregion

    //region get&set
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BmobFile getVideoFile() {
        return videoFile;
    }

    public void setVideoFile(BmobFile videoFile) {
        this.videoFile = videoFile;
    }
    //endregion
}
