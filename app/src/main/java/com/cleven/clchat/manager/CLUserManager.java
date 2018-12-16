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
        userInfo.setAvatarUrl("https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=%E5%A4%B4%E5%83%8F%20%E4%B8%8D%E5%90%8C%E9%A3%8E%E6%A0%BC%20%E5%8F%AF%E7%88%B1&step_word=&hs=2&pn=1&spn=0&di=67776481961&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=0&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=undefined&cs=2935369566%2C270781726&os=1804162796%2C3428172578&simid=3509279076%2C395504514&adpicid=0&lpn=0&ln=3572&fr=&fmq=1544957112202_R&fm=&ic=undefined&s=undefined&hd=undefined&latest=undefined&copyright=undefined&se=&sme=&tab=0&width=undefined&height=undefined&face=undefined&ist=&jit=&cg=&bdtype=0&oriquery=&objurl=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F018a6058e4c8f8a801219c778c9492.png%401280w_1l_2o_100sh.png&fromurl=ippr_z2C%24qAzdH3FAzdH3Fooo_z%26e3Bzv55s_z%26e3Bv54_z%26e3BvgAzdH3Fo56hAzdH3FZM3EyMTIyND2%3D_z%26e3Bip4s%3FfotpviPw2j%3D5g&gsm=5a&rpstart=0&rpnum=0&islist=&querylist=&selected_tags=0");
        return userInfo;
    }

    public void setUserInfo(CLUserBean userInfo) {
        this.userInfo = userInfo;
    }
}
