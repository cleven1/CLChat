package com.cleven.clchat.utils;

import com.cleven.clchat.home.Bean.CLMessageBean;
import com.cleven.clchat.home.Bean.CLMessageBodyType;
import com.cleven.clchat.manager.CLUserManager;
import com.cleven.clchat.home.CLEmojiCommon.utils.CLEmojiFileUtils;

import java.util.Date;

import dev.utils.LogPrintUtils;
import dev.utils.common.DateUtils;
import dev.utils.common.FileUtils;

/**
 * Created by cleven on 2018/12/16.
 */

public class CLUtils {
    /// 时间戳,毫秒级
    public static long timeStamp  = (long) (System.currentTimeMillis());

    public static String formatTiem(String time){
        long parseLong = Long.parseLong(time) / 1000;
        long currentTime = timeStamp/1000;
        long marginTime = currentTime - parseLong;

        String formatTime = DateUtils.formatTime(Long.parseLong(time), "yyyy-MM-dd HH:mm:ss");
        Date dateTime = DateUtils.parseDate(formatTime);
        String format = "00:00";
        int month = DateUtils.getMonth(dateTime);
        int day1 = DateUtils.getDay(dateTime);
        int hour = DateUtils.get24Hour(dateTime);
        int minute = DateUtils.getMinute(dateTime);
        LogPrintUtils.eTag("时间戳",marginTime + "");
        LogPrintUtils.eTag("时间戳 parseLong",parseLong + "");
        LogPrintUtils.eTag("时间戳 currentTime",currentTime + "");
        if (marginTime > 3600 * 24 * 365) {
            format = formatTime;
        }else if (marginTime > 3600 * 24) {
            format = month + "-" + day1 + " " + hour + ":" + minute;
        }else {
            format = hour + ":" + minute;
        }
        return format;
    }

    /// 检查本地文件是否存在
    public static String checkLocalPath(CLMessageBean message){
        String currentUserId = CLUserManager.getInstence().getUserInfo().getUserId();
        if (currentUserId.equals(message.getUserInfo().getUserId())){//判断自己发送的
            if (CLMessageBodyType.fromTypeName(message.getMessageType())==CLMessageBodyType.MessageBodyType_Emoji){
                message.setLocalAudioUrl(CLEmojiFileUtils.getFolderPath("assets") + message.getLocalAudioUrl());
            }
            if (FileUtils.isFileExists(message.getLocalAudioUrl())){
                return message.getLocalAudioUrl();
            }else {
                return message.getMediaUrl();
            }
        }else{
            return message.getMediaUrl();
        }

    }

}
