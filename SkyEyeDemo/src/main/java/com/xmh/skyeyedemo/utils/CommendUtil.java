package com.xmh.skyeyedemo.utils;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;

/**
 * Created by xmh19 on 2016/2/28 028.
 */
public class CommendUtil {
    public static final String COMMEND_END_CALL="cmd_end_call";
    public static final String ACTION_END_CALL="ActionEndCall";

    /**
     * 发送终止终端通话指令
     */
    public static void sendCommendEndCall(String eyeName ,String cmd){
        //获取到与聊天人的会话对象。参数username为聊天人的userid或者groupid，后文中的username皆是如此
        EMConversation conversation = EMChatManager.getInstance().getConversation(eyeName);
        //创建一条文本消息
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        //设置消息body
        TextMessageBody txtBody = new TextMessageBody(cmd);
        message.addBody(txtBody);
        //设置接收人
        message.setReceipt(eyeName);
        //把消息加入到此会话对象中
        conversation.addMessage(message);
        //发送消息
        EMChatManager.getInstance().sendMessage(message, null);
    }
}
