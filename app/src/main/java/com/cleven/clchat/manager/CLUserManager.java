package com.cleven.clchat.manager;

import com.cleven.clchat.model.CLUserBean;

/**
 * Created by cleven on 2018/12/14.
 */

public class CLUserManager {
    private static CLUserManager instence = new CLUserManager();
    private CLUserManager(){
    }
    public static CLUserManager getInstence(){
        return instence;
    }

    private CLUserBean userInfo;

    public CLUserBean getUserInfo() {
        CLUserBean userInfo = new CLUserBean();
        userInfo.setName("cleven");
        userInfo.setUserId("123456");
        userInfo.setAvatarUrl("http://img.zcool.cn/community/018a6058e4c8f8a801219c778c9492.png@1280w_1l_2o_100sh.png");
        return userInfo;
    }

    public void setUserInfo(CLUserBean userInfo) {
        this.userInfo = userInfo;
    }
}
