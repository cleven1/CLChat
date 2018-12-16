package com.cleven.clchat.manager;

import com.cleven.clchat.model.CLUserBean;

/**
 * Created by cleven on 2018/12/14.
 */

public class CLUserManager {
    private static CLUserManager instence = new CLUserManager();
    private CLUserManager(){
    }
    public CLUserManager getInstence(){
        return instence;
    }

    private CLUserBean userInfo;

    public CLUserBean getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(CLUserBean userInfo) {
        this.userInfo = userInfo;
    }
}
