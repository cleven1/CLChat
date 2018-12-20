package com.cleven.clchat.utils;

/**
 * Created by cleven on 2018/12/16.
 */

public class CLUtils {
    /// 时间戳,毫秒级
    public static long timeStamp  = (int) (System.currentTimeMillis());

    public static String formatTiem(String time){
        long parseLong = Long.parseLong(time) / 1000;
        long currentTime = timeStamp/1000;
        long marginTime = currentTime - parseLong;

        long year = 3600 * 24 * 365;
        long mouth = year/12;
        long weak = mouth/4;
        long day = weak/7;
        
        if (marginTime > year) {

        }

        return "";
    }

}
