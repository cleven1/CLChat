package com.cleven.clchat.contack.bean;

import com.cleven.clchat.model.CLUserBean;

public class CLFriendBean {

    public CLUserBean userInfo;

    public boolean isFriend;

    public CLUserBean getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(CLUserBean userInfo) {
        this.userInfo = userInfo;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }
}
