package com.xmh.skyeyedemo.bean;

import com.xmh.skyeyedemo.application.AppConfig;

import java.io.File;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**每个文件都保存在bean中
 */
public class FileBmobBean extends BmobObject{

    /**使用用户名初始化，不允许修改*/
    private String username= AppConfig.getFullUsername();

    /**文件信息*/
    private BmobFile videoFile;

    //region constructor
    public FileBmobBean(BmobFile videoFile) {
        this.videoFile = videoFile;
    }

    public FileBmobBean(String filePath) {
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
