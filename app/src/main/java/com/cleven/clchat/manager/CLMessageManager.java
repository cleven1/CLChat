package com.cleven.clchat.manager;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.cleven.clchat.home.Bean.CLMessageBean;
import com.cleven.clchat.home.Bean.CLMessageBodyType;
import com.cleven.clchat.utils.CLUtils;

import dev.utils.LogPrintUtils;

import static com.cleven.clchat.home.Bean.CLSendStatus.SendStatus_FAILED;
import static com.cleven.clchat.home.Bean.CLSendStatus.SendStatus_SEND;
import static com.cleven.clchat.home.Bean.CLSendStatus.SendStatus_SENDING;

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
     * 定义发送消息回调接口
     */
    public interface CLSendMessageStatusOnListener {
        void onSuccess(CLMessageBean message);
        void onFailure(CLMessageBean message);
    }
    private CLSendMessageStatusOnListener messageStatusOnListener;
    public void setMessageStatusOnListener(CLSendMessageStatusOnListener messageStatusOnListener) {
        this.messageStatusOnListener = messageStatusOnListener;
    }

    /**
     * 定义收到消息的接口
     */
    public interface CLReceiveMessageOnListener{
        void onMessage(CLMessageBean message);
    }
    private CLReceiveMessageOnListener receiveMessageOnListener;
    public void setReceiveMessageOnListener(CLReceiveMessageOnListener receiveMessageOnListener) {
        this.receiveMessageOnListener = receiveMessageOnListener;
    }

    /**
     * 发送文本消息
     * @param text 内容
     */
    public CLMessageBean sendMessage(String text,String userId,CLMessageBodyType messageBodyType,int duration,String mediaUrl){
        CLMessageBean message = new CLMessageBean();
        /// 是否是群聊会话
        message.setGroupSession(false);
        /// 消息内容
        message.setContent(text);
        /// 发送者信息
        message.setUserInfo(CLUserManager.getInstence().getUserInfo());
        /// 消息类型
        message.setMessageType(messageBodyType.getTypeName());
        /// 时长
        message.setDuration(duration);
        /// 音视频路径
        message.setMediaUrl(mediaUrl);

        long timeStamp = CLUtils.timeStamp;
        /// 消息id
        message.setMessageId("" + timeStamp);
        /// 发送状态
        if (CLMQTTManager.getInstance().getCurrentStatus() != CLMQTTManager.CLMQTTStatus.connect_succss){
            message.setSendStatus(SendStatus_FAILED.getTypeName());
        }else {
            message.setSendStatus(SendStatus_SENDING.getTypeName());
        }
        /// 发送时间
        message.setSentTime("" + timeStamp);
        /// 目标id
        message.setTargetId(userId);

        String jsonString = JSON.toJSONString(message);

        /// 发送
        if (CLMQTTManager.getInstance().getCurrentStatus() == CLMQTTManager.CLMQTTStatus.connect_succss){
            CLMQTTManager.getInstance().sendSingleMessage(jsonString,userId);
        }

        LogPrintUtils.d(jsonString);

        CLMQTTManager.getInstance().setMessageStatusOnListener(new CLMQTTManager.CLSendMessageStatusOnListener() {
            @Override
            public void onSuccess(String message) {
                if (messageStatusOnListener != null){
                    Log.e("tag","message = " + message);
                    CLMessageBean messageBean = JSON.parseObject(message, CLMessageBean.class);
                    // 发送成功  CLSendStatus
                    messageBean.setSendStatus(SendStatus_SEND.getTypeName());
                    messageStatusOnListener.onSuccess(messageBean);
                }
            }

            @Override
            public void onFailure(String message) {
                if (messageStatusOnListener != null){
                    CLMessageBean messageBean = JSON.parseObject(message, CLMessageBean.class);
                    /// 发送失败 CLSendStatus
                    messageBean.setSendStatus(SendStatus_FAILED.getTypeName());
                    messageStatusOnListener.onFailure(messageBean);
                }
            }
        });

        return message;
    }


    public void receiveMessageHandler(String msg){
        CLMessageBean messageBean = JSON.parseObject(msg, CLMessageBean.class);
        if (receiveMessageOnListener != null){
            receiveMessageOnListener.onMessage(messageBean);
        }
    }


}
