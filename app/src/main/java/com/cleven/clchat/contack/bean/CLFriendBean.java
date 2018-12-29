package com.cleven.clchat.contack.bean;

import android.text.TextUtils;

import com.cleven.clchat.manager.CLUserManager;
import com.nanchen.wavesidebar.FirstLetterUtil;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class CLFriendBean implements RealmModel {

    /**
     * 用户id
     */
    @PrimaryKey
    private String userId;
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
     * 昵称首字母
     */
    private String index;
    /**
     * 是否是好友
     */
    private boolean isFriend;
    /**
     * 10000:表示headView
     * 其它:表示内容
     */
    private int itemType;
    /**
     * 当前用户id
     */
    private String currentUserId;
    /// 插入数据
    public static void updateData(CLFriendBean friendBean){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(friendBean);
            }
        });

    }

    /// 获取好友信息
    public static CLFriendBean getFriendUserInfo(String userId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<CLFriendBean> all = realm.where(CLFriendBean.class)
                .equalTo("currentUserId",CLUserManager.getInstence().getUserInfo().getUserId())
                .equalTo("userId",userId)
                .findAll();
        if (all.size() > 0){
            return realm.copyFromRealm(all.first());
        }
        return null;
    }

    /// 查询所有
    public static List<CLFriendBean> getAllFriendData(){
        List<CLFriendBean> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<CLFriendBean> all = realm.where(CLFriendBean.class)
                .equalTo("currentUserId",CLUserManager.getInstence().getUserInfo().getUserId())
                .findAll();
        list.addAll(realm.copyFromRealm(all));
        return list;
    }

    /// 根据用户删除
    public static void deleteUser(String userId){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<CLFriendBean> results = realm.where(CLFriendBean.class)
                .equalTo("currentUserId",CLUserManager.getInstence().getUserInfo().getUserId())
                .equalTo("userId", userId).findAll();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                results.deleteAllFromRealm();
            }
        });
    }
    /// 删除所有
    public static void deleteAllUser(){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<CLFriendBean> all = realm.where(CLFriendBean.class)
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

    public String getIndex() {
        return TextUtils.isEmpty(name) ? "" : FirstLetterUtil.getFirstLetter(name);
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }
}
