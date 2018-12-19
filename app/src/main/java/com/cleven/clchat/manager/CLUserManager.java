package com.cleven.clchat.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;
import com.cleven.clchat.app.CLApplication;
import com.cleven.clchat.model.CLUserBean;

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
        SharedPreferences sp = CLApplication.mContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String string = sp.getString(CACHE_USERINFOKEY,"");
        CLUserBean userBean = JSON.parseObject(string, CLUserBean.class);
        return userBean;
    }

    public void setUserInfo(CLUserBean userInfo) {
        this.userInfo = userInfo;
        SharedPreferences sp = CLApplication.mContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String jsonString = JSON.toJSONString(userInfo);
        sp.edit().putString(CACHE_USERINFOKEY,jsonString).commit();
    }
}
