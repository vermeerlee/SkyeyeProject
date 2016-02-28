package com.xmh.skyeyedemo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.MessageBody;
import com.xmh.skyeyedemo.application.AppConfig;
import com.xmh.skyeyedemo.utils.CommendUtil;

/**
 * Created by xmh19 on 2016/2/28 028.
 */
public class NewMessageBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 注销广播
        abortBroadcast();

        // 消息id（每条消息都会生成唯一的一个id，目前是SDK生成）
        String msgId = intent.getStringExtra("msgid");
        //发送方
        String usernameFrom = intent.getStringExtra("from");
        // 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
        EMMessage message = EMChatManager.getInstance().getMessage(msgId);
        EMConversation conversation = EMChatManager.getInstance().getConversation(usernameFrom);

        if(!usernameFrom.startsWith(AppConfig.getUsername())){
            //不是该用户名开头的消息不予理会
            return;
        }

        //如果消息包含终止通话命令，发送终止广播
        MessageBody body = message.getBody();
        if(body.toString().contains(CommendUtil.COMMEND_END_CALL)){
            Intent cmdIntent = new Intent();
            cmdIntent.setAction(CommendUtil.ACTION_END_CALL);
            LocalBroadcastManager.getInstance(context).sendBroadcast(cmdIntent);
        }

    }
}
