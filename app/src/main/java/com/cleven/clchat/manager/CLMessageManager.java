package com.cleven.clchat.manager;

import com.alibaba.fastjson.JSON;
import com.cleven.clchat.home.Bean.CLMessageBean;
import com.cleven.clchat.home.Bean.CLMessageBodyType;
import com.cleven.clchat.home.Bean.CLSentStatus;
import com.cleven.clchat.utils.CLUtils;

import dev.utils.LogPrintUtils;

/**
 * Created by cleven on 2018/12/16.
 */

public class CLMessageManager {

    private static CLMessageManager instance = new CLMessageManager();
    private CLMessageManager(){}
    public static CLMessageManager getInstance(){
        return instance;
    }

    /**
     * 发送文本消息
     * @param text 内容
     */
    public void sendMessage(String text){
        CLMessageBean message = new CLMessageBean();
        /// 消息内容
        message.setContent(text);
        /// 发送者信息
        message.setUserInfo(CLUserManager.getInstence().getUserInfo());
        /// 消息类型
        message.setMessageType(CLMessageBodyType.MessageBodyType_Text.getTypeName());
        int timeStamp = CLUtils.timeStamp;
        /// 消息id
        message.setMessageId("" + timeStamp);
        /// 发送状态
        message.setSentStatus(CLSentStatus.SentStatus_SENT.getTypeName());
        /// 发送时间
        message.setSentTime("" + timeStamp);
        /// 目标id
        message.setTargetId(CLUserManager.getInstence().getUserInfo().getUserId());

        String jsonString = JSON.toJSONString(message);
        /// 发送
        CLMQTTManager.getInstance().sendMessage(jsonString);

        LogPrintUtils.d(jsonString);
    }



}
