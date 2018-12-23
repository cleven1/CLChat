package com.cleven.clchat.model;

import android.text.TextUtils;

/**
 * Created by cleven on 2018/12/14.
 */

public class CLUserBean {

    /**
     * 用户id
     */
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

    public String getAvatarUrl() {
        return avatarUrl;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
