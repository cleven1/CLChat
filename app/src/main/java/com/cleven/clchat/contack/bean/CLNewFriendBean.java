package com.cleven.clchat.contack.bean;

import android.text.TextUtils;

import com.cleven.clchat.manager.CLUserManager;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class CLNewFriendBean implements RealmModel{

    /**
     * 用户id
     */
    @PrimaryKey
    private String userId;
    /**
     * 后续扩展跟微信一样的加好友方式使用
     */
    private String payload;
    /**
     * 用户昵称
     */
    private String name;
    /**
     * 性别
     *  0: 女
     *  1: 男
     */
    private int gender;
    /**
     * 生日
     */
    private String birthday;
    /**
     * 别名
     */
    private String aliasName;
    /**
     * 签名
     */
    private String description;
    /**
     * 头像
     */
    private String avatarUrl;
    /**
     * 城市
     */
    private String city;
    /**
     * 消息未读数
     */
    private int unreadNum;
    /**
     * 是否是好友
     */
    private boolean isFriend;
    /**
     * 是否进入过好友通知界面
     */
    private boolean isGotoDetail;
    /**
     * 当前用户id
     */
    private String currentUserId;

    /// 插入数据
    public static void updateData(CLNewFriendBean newFriendBean){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(newFriendBean);
            }
        });
    }

    /// 查询所有
    public static List<CLNewFriendBean> getAllNewFriendData(){
        List<CLNewFriendBean> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<CLNewFriendBean> all = realm.where(CLNewFriendBean.class)
                .equalTo("currentUserId",CLUserManager.getInstence().getUserInfo().getUserId())
                .findAll();
        list.addAll(realm.copyFromRealm(all));
        return list;
    }

    /// 查询所有未进入详情页的条数
    public static int getAllNewFriendUnReadData(){
        List<CLNewFriendBean> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<CLNewFriendBean> all = realm.where(CLNewFriendBean.class)
                .equalTo("currentUserId",CLUserManager.getInstence().getUserInfo().getUserId())
                .equalTo("isGotoDetail",false)
                .findAll();
        list.addAll(realm.copyFromRealm(all));
        return list.size();
    }

    /// 根据用户删除
    public static void deleteUser(String userId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<CLNewFriendBean> beans = realm.where(CLNewFriendBean.class)
                .equalTo("currentUserId",CLUserManager.getInstence().getUserInfo().getUserId())
                .equalTo("userId", userId).findAll();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                beans.deleteAllFromRealm();
            }
        });
    }
    /// 删除所有
    public static void deleteAllUser(){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<CLNewFriendBean> all = realm.where(CLNewFriendBean.class)
                .equalTo("currentUserId",CLUserManager.getInstence().getUserInfo().getUserId())
                .findAll();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                all.deleteAllFromRealm();
            }
        });
    }


    public String getUserId() {
        return TextUtils.isEmpty(userId) ? "" : userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getUnreadNum() {
        return unreadNum;
    }

    public void setUnreadNum(int unreadNum) {
        this.unreadNum = unreadNum;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public boolean isGotoDetail() {
        return isGotoDetail;
    }

    public void setGotoDetail(boolean gotoDetail) {
        isGotoDetail = gotoDetail;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }
}
