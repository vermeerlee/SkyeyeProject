package com.xmh.skyeyedemo.bean;

import android.text.TextUtils;

import cn.bmob.v3.BmobObject;

/**
 * 每个fullNmae（eye）都在Bmob上对应一个User
 */
public class UserBmobBean extends BmobObject{

    /**全名，用于唯一标识*/
    private String fullUsername;
    /**昵称，用于显示给用户*/
    private String nickName;

    public UserBmobBean(){
        //to nothing
    }

    public UserBmobBean(String fullUsername){
        setFullUsername(fullUsername);
    }

    public String getFullUsername() {
        return fullUsername;
    }

    public void setFullUsername(String fullUsername) {
        this.fullUsername = fullUsername;
        //如果nickName未初始化，则用fullUsername初始化
        if(TextUtils.isEmpty(nickName)){
            nickName=new String(fullUsername);
        }
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
