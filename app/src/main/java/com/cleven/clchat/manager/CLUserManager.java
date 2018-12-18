package com.cleven.clchat.manager;

import com.alibaba.fastjson.JSON;
import com.cleven.clchat.app.CLApplication;
import com.cleven.clchat.model.CLUserBean;

import dev.utils.app.cache.DevCache;

/**
 * Created by cleven on 2018/12/14.
 */

public class CLUserManager {

    private static final String CACHE_USERINFOKEY = "CLChat_UserInfo";

    private static CLUserManager instence = new CLUserManager();
    private CLUserManager(){
    }
    public static CLUserManager getInstence(){
        return instence;
    }

    private CLUserBean userInfo;

    public CLUserBean getUserInfo() {
        DevCache cache = DevCache.get(CLApplication.mContext, CACHE_USERINFOKEY);
        String string = cache.getAsString(CACHE_USERINFOKEY);
        CLUserBean userBean = JSON.parseObject(string, CLUserBean.class);
        return userBean;
    }

    public void setUserInfo(CLUserBean userInfo) {
        this.userInfo = userInfo;
        String jsonString = JSON.toJSONString(userInfo);
        DevCache cache = DevCache.get(CLApplication.mContext);
        cache.put(CACHE_USERINFOKEY,jsonString);
    }
}
