package com.cleven.clchat.model;

import android.text.TextUtils;

import com.cleven.clchat.utils.CLAPPConst;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by cleven on 2018/12/14.
 */
@RealmClass
public class CLUserBean implements RealmModel {

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


    public String getUserId() {
        return TextUtils.isEmpty(userId) ? " " : userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return TextUtils.isEmpty(name) ? " " : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        if (avatarUrl.startsWith("http")){
            return avatarUrl;
        }
        return CLAPPConst.QINGCLOUD_AVATAR_URL + avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return TextUtils.isEmpty(birthday) ? "" : birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAliasName() {
        return TextUtils.isEmpty(aliasName) ? "" : aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public String getDescription() {
        return TextUtils.isEmpty(description) ? "" : description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCity() {
        return TextUtils.isEmpty(city) ? "" : city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    @Override
    public String toString() {
        return "CLUserBean{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", gender=" + gender +
                ", birthday='" + birthday + '\'' +
                ", aliasName='" + aliasName + '\'' +
                ", description='" + description + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
