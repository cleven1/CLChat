package com.cleven.clchat.home.Bean;

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
     * 消息的发送状态 CLSendStatus
     */
    private int sendStatus;
    /**
     * 文件上传状态
     */
    private CLUploadStatus uploadStatus;
    /**
     * 消息的发送时间
     */
    private String sentTime;
    /**
     * 目标会话id
     */
    private String targetId;
    /**
     * 是否是群聊会话
     */
    private boolean isGroupSession;
    /**
     * 消息类型 CLMessageBodyType
     */
    private int messageType;
    /**
     * 多媒体时长
     */
    private int duration;
    /**
     * 多媒体url
     */
    private String mediaUrl;
    /**
     * 本地录音路径
     */
    private String localUrl;
    /**
     * 视频缩略图
     */
    private String videoThumbnail;
    /**
     * 图片的宽
     */
    private int witdh;
    /**
     * 图片的高
     */
    private int height;

    /**
     * 发送者的用户信息
     */
    private CLUserBean userInfo;

    /**
     * 消息中的@提醒信息
     */
    private CLMentionedInfo mentionedInfo;
    /**
     * 收到消息的状态
     */
    private CLReceivedStatus receivedStatus;


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

    public int getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(int sendStatus) {
        this.sendStatus = sendStatus;
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

    public String getVideoThumbnail() {
        return videoThumbnail;
    }

    public void setVideoThumbnail(String videoThumbnail) {
        this.videoThumbnail = videoThumbnail;
    }

    public CLReceivedStatus getReceivedStatus() {
        return receivedStatus;
    }

    public void setReceivedStatus(CLReceivedStatus receivedStatus) {
        this.receivedStatus = receivedStatus;
    }

    public CLUploadStatus getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(CLUploadStatus uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public int getWitdh() {
        return witdh;
    }

    public void setWitdh(int witdh) {
        this.witdh = witdh;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getDuration() {
        return duration > 0 ? duration : 0;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public void setMentionedInfo(CLMentionedInfo mentionedInfo) {
        this.mentionedInfo = mentionedInfo;
    }

    public boolean isGroupSession() {
        return isGroupSession;
    }

    public void setGroupSession(boolean groupSession) {
        isGroupSession = groupSession;
    }

    @Override
    public String toString() {
        return "CLMessageBean{" +
                "messageId='" + messageId + '\'' +
                ", content='" + content + '\'' +
                ", sendStatus=" + sendStatus +
                ", sentTime='" + sentTime + '\'' +
                ", targetId='" + targetId + '\'' +
                ", isGroupSession=" + isGroupSession +
                ", messageType=" + messageType +
                ", duration=" + duration +
                ", mediaUrl='" + mediaUrl + '\'' +
                ", videoThumbnail='" + videoThumbnail + '\'' +
                ", witdh=" + witdh +
                ", height=" + height +
                ", userInfo=" + userInfo +
                ", mentionedInfo=" + mentionedInfo +
                '}';
    }
}
