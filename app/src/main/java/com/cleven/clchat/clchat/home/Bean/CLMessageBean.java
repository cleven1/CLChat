package com.cleven.clchat.clchat.home.Bean;

import com.cleven.clchat.clchat.model.CLUserBean;

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
     * 消息的发送状态
     */
    private CLSentStatus sentStatus;
    /**
     * 消息的发送时间
     */
    private String sentTime;
    /**
     * 消息的类型名
     */
    private String objectName;
    /**
     * 目标会话id
     */
    private String targetId;
    /**
     * 消息发送者id
     */
    private String senderUserId;
    /**
     * 消息类型
     */
    private CLMessageBodyType messageType;

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
        return messageDirection;
    }

    public void setMessageDirection(CLMessageDirection messageDirection) {
        this.messageDirection = messageDirection;
    }

    public CLSentStatus getSentStatus() {
        return sentStatus;
    }

    public void setSentStatus(CLSentStatus sentStatus) {
        this.sentStatus = sentStatus;
    }

    public String getSentTime() {
        return sentTime;
    }

    public void setSentTime(String sentTime) {
        this.sentTime = sentTime;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(String senderUserId) {
        this.senderUserId = senderUserId;
    }

    public CLMessageBodyType getMessageType() {
        return messageType;
    }

    public void setMessageType(CLMessageBodyType messageType) {
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
