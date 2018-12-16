package com.cleven.clchat.clchat.model;

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
     * 头像
     */
    private String avatarUrl;

    public String getUserId() {
        return userId;
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
}
