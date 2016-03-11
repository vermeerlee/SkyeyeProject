package com.xmh.skyeyedemo.bean;

import com.xmh.skyeyedemo.application.AppConfig;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**每个文件都保存在bean中
 */
public class FileBmobBean extends BmobObject{

    /**使用用户名初始化，不允许修改*/
    private String username= AppConfig.getFullUsername();

    /**文件信息*/
    private BmobFile videoFile;

    /**上传成功后返回的用于下载文件的文件名（bmob后台生成的文件唯一标识）*/
    private String filenameForDownload;

    //region constructor 注意创建对象时会初始化username，因此只允许在eye模式创建对象
    public FileBmobBean() {
    }
    public FileBmobBean(BmobFile videoFile) {
        this.videoFile = videoFile;
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

    public String getFilenameForDownload() {
        return filenameForDownload;
    }

    public void setFilenameForDownload(String filenameForDownload) {
        this.filenameForDownload = filenameForDownload;
    }
    //endregion
}
