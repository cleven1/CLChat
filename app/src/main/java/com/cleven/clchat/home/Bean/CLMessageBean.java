package com.cleven.clchat.home.Bean;

import com.cleven.clchat.manager.CLUserManager;
import com.cleven.clchat.model.CLUserBean;

public class CLMessageBean {

    /**
     * 消息id
     */
    private String messageId;
    /**
     * 文本内容
     */
    private String content;
    /**
     * 消息的方向
     */
    private CLMessageDirection messageDirection;
    /**
     * 消息的发送状态 CLSentStatus
     */
    private int sentStatus;
    /**
     * 消息的发送时间
     */
    private String sentTime;
    /**
     * 目标会话id
     */
    private String targetId;
    /**
     * 消息类型 CLMessageBodyType
     */
    private int messageType;

    /**
     * 发送者的用户信息
     */
    private CLUserBean userInfo;

    /**
     * 消息中的@提醒信息
     */
    private CLMentionedInfo mentionedInfo;


    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CLMessageDirection getMessageDirection() {
        if (getUserInfo().getUserId().equals(CLUserManager.getInstence().getUserInfo().getUserId())){
            return CLMessageDirection.MessageDirection_SEND;
        }
        return CLMessageDirection.MessageDirection_RECEIVE;
    }

    public int getSentStatus() {
        return sentStatus;
    }

    public void setSentStatus(int sentStatus) {
        this.sentStatus = sentStatus;
    }

    public String getSentTime() {
        return sentTime;
    }

    public void setSentTime(String sentTime) {
        this.sentTime = sentTime;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public CLUserBean getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(CLUserBean userInfo) {
        this.userInfo = userInfo;
    }

    public CLMentionedInfo getMentionedInfo() {
        return mentionedInfo;
    }

    public void setMentionedInfo(CLMentionedInfo mentionedInfo) {
        this.mentionedInfo = mentionedInfo;
    }
}
