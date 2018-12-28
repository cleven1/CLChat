package com.cleven.clchat.home.Bean;

import com.cleven.clchat.model.CLUserBean;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmResults;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class CLMessageBean implements RealmModel {

    /**
     * 消息id
     */
    @PrimaryKey
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
     * CLUploadStatus
     */
    private int uploadStatus;
    /**
     * 消息的发送时间
     */
    private String sendTime;
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
     * 收到消息的状态
     * CLReceivedStatus
     */
    private int receivedStatus;
    /**
     * 消息中的@提醒信息
     */
    @Ignore
    private CLMentionedInfo mentionedInfo;


    /// 插入或者更新数据
    public static void insertMessageData(CLMessageBean messageBean){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(messageBean);
            }
        });
    }

    /// 更新状态
    public static void updateMessageStatus(List<CLMessageBean> messageBeans){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (CLMessageBean messageBean : messageBeans){
                    messageBean.setReceivedStatus(CLReceivedStatus.ReceivedStatus_READ.getTypeName());
                    realm.copyToRealmOrUpdate(messageBean);
                }
            }
        });
    }

    /// 根据用户查询消息
    public static List<CLMessageBean> getUserMessageData(String targetId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<CLMessageBean> sessionBeans = realm.where(CLMessageBean.class)
                .equalTo("targetId",targetId)
                .findAll();
        /// 根据发送时间排序
        sessionBeans = sessionBeans.sort("sendTime");
        return realm.copyFromRealm(sessionBeans);
    }

    /// 查询所有未读的消息
    public static List<CLMessageBean> getAllUnReadMessageData(){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<CLMessageBean> sessionBeans = realm.where(CLMessageBean.class)
                .equalTo("receivedStatus", CLReceivedStatus.ReceivedStatus_UNREAD.getTypeName())
                .findAll();
        return realm.copyFromRealm(sessionBeans);
    }

    /// 查询用户未读的消息
    public static List<CLMessageBean> getUserUnReadMessageData(String targetId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<CLMessageBean> sessionBeans = realm.where(CLMessageBean.class)
                .equalTo("targetId",targetId)
                .equalTo("receivedStatus", CLReceivedStatus.ReceivedStatus_UNREAD.getTypeName())
                .findAll();
        List<CLMessageBean> beanList = realm.copyFromRealm(sessionBeans);
        return beanList;
    }

    /// 根据用户删除数据
    public static void deleteUserMessageData(String targetId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<CLMessageBean> results = realm.where(CLMessageBean.class).equalTo("targetId", targetId).findAll();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                results.deleteAllFromRealm();
            }
        });
    }

    /// 删除所有
    public static void deleteAllMessageData(){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<CLMessageBean> all = realm.where(CLMessageBean.class).findAll();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                all.deleteAllFromRealm();
            }
        });
    }

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

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
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

    public int getReceivedStatus() {
        return receivedStatus;
    }

    public void setReceivedStatus(int receivedStatus) {
        this.receivedStatus = receivedStatus;
    }

    public int getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(int uploadStatus) {
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
                ", sendTime='" + sendTime + '\'' +
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
