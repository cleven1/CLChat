package com.cleven.clchat.fragment.home.bean;

import com.cleven.clchat.home.Bean.CLMessageBean;
import com.cleven.clchat.home.Bean.CLMessageBodyType;
import com.cleven.clchat.home.Bean.CLSendStatus;
import com.cleven.clchat.utils.CLAPPConst;
import com.cleven.clchat.utils.CLUtils;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class CLSessionBean implements RealmModel {

    /**
     * 用户昵称
     */
    private String name;
    /**
     * 别名
     */
    private String aliasName;
    /**
     * 头像
     */
    private String avatarUrl;
    /**
     * 文本内容
     */
    private String content;
    /**
     * 消息的发送状态 CLSendStatus
     */
    private int sendStatus;
    /**
     * 消息的发送时间
     */
    private String sendTime;
    /**
     * 消息未读数
     */
    private int unreadNumber;
    /**
     * 目标会话id
     */
    @PrimaryKey
    private String targetId;
    /**
     * 是否是群聊会话
     */
    private boolean isGroupSession;
    /**
     * 消息类型 CLMessageBodyType
     * CLMessageBodyType
     */
    private int messageType;

    /// 插入或者更新数据
    public static void updateData(CLSessionBean sessionBean){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(sessionBean);
            }
        });
    }

    /// 查询所有消息会话
    public static List<CLSessionBean> getAllSessionData(){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<CLSessionBean> sessionBeans = realm.where(CLSessionBean.class).findAll();
        /// 根据发送时间排序
        sessionBeans = sessionBeans.sort("sendTime");
        return realm.copyFromRealm(sessionBeans);
    }

    /// 根据用户id删除数据
    public static void deleteUserSessionData(String targetId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<CLSessionBean> results = realm.where(CLSessionBean.class).equalTo("targetId", targetId).findAll();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                results.deleteAllFromRealm();
            }
        });
    }

    /// 删除所有
    public static void deleteAllSessionData(){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<CLSessionBean> all = realm.where(CLSessionBean.class).findAll();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                all.deleteAllFromRealm();
            }
        });
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public String getAvatarUrl() {
        return CLAPPConst.QINGCLOUD_AVATAR_URL + avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CLSendStatus getSendStatus() {
        return CLSendStatus.fromTypeName(sendStatus);
    }

    public void setSendStatus(int sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getSendTime() {
        return CLUtils.formatTime(sendTime);
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

    public boolean isGroupSession() {
        return isGroupSession;
    }

    public void setGroupSession(boolean groupSession) {
        isGroupSession = groupSession;
    }

    public CLMessageBodyType getMessageType() {
        return CLMessageBodyType.fromTypeName(messageType);
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public int getUnreadNumber() {
        return CLMessageBean.getAllUnReadMessageData().size();
    }

    public void setUnreadNumber(int unreadNumber) {
        this.unreadNumber = unreadNumber;
    }
}
