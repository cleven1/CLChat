package com.cleven.clchat.manager;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.cleven.clchat.contack.bean.CLFriendBean;
import com.cleven.clchat.contack.bean.CLNewFriendBean;
import com.cleven.clchat.fragment.home.bean.CLSessionBean;
import com.cleven.clchat.home.Bean.CLMessageBean;
import com.cleven.clchat.home.Bean.CLMessageBodyType;
import com.cleven.clchat.home.Bean.CLReceivedStatus;
import com.cleven.clchat.home.Bean.CLSendStatus;
import com.cleven.clchat.home.CLEmojiCommon.utils.CLEmojiFileUtils;
import com.cleven.clchat.utils.CLJsonUtil;
import com.cleven.clchat.utils.CLUtils;

import dev.utils.LogPrintUtils;
import dev.utils.app.image.BitmapUtils;

import static com.cleven.clchat.home.Bean.CLSendStatus.SendStatus_FAILED;
import static com.cleven.clchat.home.Bean.CLSendStatus.SendStatus_SENDING;
import static dev.DevUtils.runOnUiThread;

/**
 * Created by cleven on 2018/12/16.
 */

public class CLMessageManager {

    private static CLMessageManager instance = new CLMessageManager();
    private CLMessageManager(){}
    public static CLMessageManager getInstance(){
        return instance;
    }

    /**
     * 定义发送消息回调接口
     */
    public interface CLSendMessageStatusOnListener {
        void onSuccess(CLMessageBean message);
        void onFailure(CLMessageBean message);
    }
    private CLSendMessageStatusOnListener messageStatusOnListener;
    public void setMessageStatusOnListener(CLSendMessageStatusOnListener messageStatusOnListener) {
        this.messageStatusOnListener = messageStatusOnListener;
    }

    /**
     * 定义上传回调接口
     */
    public interface CLUploadStatusOnListener {
        void onSuccess(String fileName);
        void onFailure(String fileName);
    }
    private CLUploadStatusOnListener uploadStatusOnListener;
    public void setUploadStatusOnListener(CLUploadStatusOnListener uploadStatusOnListener) {
        this.uploadStatusOnListener = uploadStatusOnListener;
    }

    /**
     * 定义收到消息的接口
     */
    public interface CLReceiveMessageOnListener{
        void onMessage(CLMessageBean message);
    }
    private CLReceiveMessageOnListener receiveMessageOnListener;
    public void setReceiveMessageOnListener(CLReceiveMessageOnListener receiveMessageOnListener) {
        this.receiveMessageOnListener = receiveMessageOnListener;
    }

    /**
     * 定义收到好友请求接口
     */
    public interface CLReceiveFriendOnListener{
        void onMessage(CLNewFriendBean friendBean);
    }
    private CLReceiveFriendOnListener receiveFriendOnListener;
    public void setReceiveFriendOnListener(CLReceiveFriendOnListener receiveFriendOnListener) {
        this.receiveFriendOnListener = receiveFriendOnListener;
    }

    /**
     * 发送emoji
     * @param filePath
     */
    public CLMessageBean sendEmojiMessage(String filePath,String userId,boolean isGroup){
        CLMessageBean message = new CLMessageBean();
        /// 是否是群聊会话
        message.setGroupSession(isGroup);
        /// 消息内容
        message.setContent("");
        /// 消息类型
        message.setMessageType(CLMessageBodyType.MessageBodyType_Emoji.getTypeName());
        message.setLocalUrl(filePath);
        int[] size = BitmapUtils.getImageWidthHeight(CLEmojiFileUtils.getFolderPath("/") + filePath);
        /// 设置图片的size
        if (size != null && size.length > 0){
            message.setWitdh(size[0]);
            message.setHeight(size[1]);
        }

        String imageName = CLUtils.getTimeStamp() + ".png";
        message.setMediaUrl(imageName);
        /// 发送者信息
        message.setUserInfo(CLUserManager.getInstence().getUserInfo());
        /// 目标id
        message.setTargetId(userId);
        /// 消息id
        message.setMessageId(CLUtils.getUUID());
        return message;
    }

    /**
     * 发送图片
     * @param filePath
     */
    public CLMessageBean sendImageMessage(String filePath,String fileName,int w,int h,String userId,boolean isGroup,boolean isUploadFile){
        CLMessageBean message = new CLMessageBean();
        /// 是否是群聊会话
        message.setGroupSession(isGroup);
        /// 消息内容
        message.setContent("");
        /// 消息类型
        message.setMessageType(CLMessageBodyType.MessageBodyType_Image.getTypeName());
        message.setLocalUrl(filePath);
        int[] size = BitmapUtils.getImageWidthHeight(filePath);
        /// 设置图片的size
        message.setWitdh(w);
        message.setHeight(h);
        message.setMediaUrl(fileName);
        /// 发送者信息
        message.setUserInfo(CLUserManager.getInstence().getUserInfo());
        /// 目标id
        message.setTargetId(userId);
        /// 消息id
        message.setMessageId(CLUtils.getUUID());
        /// 上传
        if (isUploadFile){
            CLUploadManager.getInstance().uploadImage(filePath,fileName,new MyUploadListener());
        }
        return message;
    }

    /**
     * 发送音频
     * @param filePath
     */
    public CLMessageBean sendAudioMessage(String filePath,int duration,String userId,boolean isGroup,boolean isUploadFile){
        CLMessageBean message = new CLMessageBean();
        /// 是否是群聊会话
        message.setGroupSession(isGroup);
        /// 消息内容
        message.setContent("");
        /// 消息类型
        message.setMessageType(CLMessageBodyType.MessageBodyType_Voice.getTypeName());
        message.setLocalUrl(filePath);
        String fileName = CLUtils.getTimeStamp() + ".mp3";
        message.setMediaUrl(fileName);
        message.setDuration(duration);
        /// 发送者信息
        message.setUserInfo(CLUserManager.getInstence().getUserInfo());
        /// 目标id
        message.setTargetId(userId);
        /// 消息id
        message.setMessageId(CLUtils.getUUID());
        /// 上传
        if (isUploadFile){
            CLUploadManager.getInstance().uploadAudio(filePath,fileName,new MyUploadListener());
        }
        return message;
    }

    /**
     * 发送视频
     * @param filePath
     */
    public CLMessageBean sendVideoMessage(String filePath,String thumnailPath,int duration,String userId,boolean isGroup,boolean isUploadFile){
        CLMessageBean message = new CLMessageBean();
        /// 是否是群聊会话
        message.setGroupSession(isGroup);
        /// 消息内容
        message.setContent("");
        /// 消息类型
        message.setMessageType(CLMessageBodyType.MessageBodyType_Video.getTypeName());
        message.setLocalUrl(filePath);
        String videoName = CLUtils.getTimeStamp() + ".mp4";
        message.setMediaUrl(videoName);
        int[] size = BitmapUtils.getImageWidthHeight(CLEmojiFileUtils.getFolderPath("/") + thumnailPath);
        /// 设置图片的size
        if (size != null && size.length > 0){
            message.setWitdh(size[0]);
            message.setHeight(size[1]);
        }
        message.setVideoThumbnail(thumnailPath);
        message.setDuration(duration);
        /// 发送者信息
        message.setUserInfo(CLUserManager.getInstence().getUserInfo());
        /// 目标id
        message.setTargetId(userId);

        /// 消息id
        message.setMessageId(CLUtils.getUUID());

        return message;
    }

    /**
     * 发送文本消息
     * @param text 内容
     */
    public CLMessageBean sendTextMessage(String text,String userId,boolean isGroup){
        CLMessageBean message = new CLMessageBean();
        /// 是否是群聊会话
        message.setGroupSession(isGroup);
        /// 消息内容
        message.setContent(text);
        /// 消息类型
        message.setMessageType(CLMessageBodyType.MessageBodyType_Text.getTypeName());
        /// 发送者信息
        message.setUserInfo(CLUserManager.getInstence().getUserInfo());
        /// 目标id
        message.setTargetId(userId);
        /// 消息id
        message.setMessageId(CLUtils.getUUID());
        sendMessage(message,userId);

        return message;
    }

    public CLMessageBean sendMessage(CLMessageBean message, String userId){

        /// 监听发送消息状态
        instance.obersverMessageSendStatus();

        /// 是否是群聊会话
        message.setGroupSession(false);

        /// 发送状态
        if (CLMQTTManager.getInstance().getCurrentStatus() != CLMQTTManager.CLMQTTStatus.connect_succss){
            message.setSendStatus(SendStatus_FAILED.getTypeName());
        }else {
            message.setSendStatus(SendStatus_SENDING.getTypeName());
        }

        /// 自己发的标记为已读
        message.setReceivedStatus(CLReceivedStatus.ReceivedStatus_READ.getTypeName());
        message.setCurrentUserId(CLUserManager.getInstence().getUserInfo().getUserId());

        /// 发送时间
        message.setSendTime("" + CLUtils.getTimeStamp());
        String jsonString = JSON.toJSONString(message);

        /// 插入数据库
        CLMessageBean.insertMessageData(message);
        SessionMessageHandler(message);
        /// 发送
        if (CLMQTTManager.getInstance().getCurrentStatus() == CLMQTTManager.CLMQTTStatus.connect_succss){
            CLMQTTManager.getInstance().sendSingleMessage(jsonString,userId);
        }

        return message;
    }

    /// 时间大于两分钟发送
    public CLMessageBean sendTimeMessage(String userId){
        /// 判断上一条时间消息和本条相差是否是两分钟,两分钟先发送时间消息
        CLMessageBean userMessageData = CLMessageBean.getUserMessageLastTimeData(userId);
        if (userMessageData.getMessageType() != CLMessageBodyType.MessageBodyType_Time.getTypeName()){
            return null;
        }
        if (userMessageData == null || CLUtils.getTimeStamp() - Long.parseLong(userMessageData.getSendTime()) >= 2 * 60 * 1000){
            if (CLMQTTManager.getInstance().getCurrentStatus() == CLMQTTManager.CLMQTTStatus.connect_succss){
                /// 发送时间
                CLMessageBean timeMessage = new CLMessageBean();
                timeMessage.setTargetId(userId);
                timeMessage.setMessageId(CLUtils.getUUID());
                timeMessage.setSendStatus(CLSendStatus.SendStatus_SEND.getTypeName());
                timeMessage.setSendTime("" + CLUtils.getTimeStamp());
                timeMessage.setMessageType(CLMessageBodyType.MessageBodyType_Time.getTypeName());
                timeMessage.setCurrentUserId(CLUserManager.getInstence().getUserInfo().getUserId());
                String timeJson = JSON.toJSONString(timeMessage);
//                CLMQTTManager.getInstance().sendSingleMessage(timeJson,userId);
                /// 插入数据库
                CLMessageBean.insertMessageData(timeMessage);
                return timeMessage;
            }
        }
        return null;
    }


    /// 监听消息发送状态
    private void obersverMessageSendStatus(){
        CLMQTTManager.getInstance().setMessageStatusOnListener(new CLMQTTManager.CLSendMessageStatusOnListener() {
            @Override
            public void onSuccess(String message) {
                if (messageStatusOnListener != null){
                    Log.e("tag","message = " + message);
                    CLMessageBean messageBean = JSON.parseObject(message, CLMessageBean.class);
                    // 发送成功  CLSendStatus
                    messageBean.setSendStatus(CLSendStatus.SendStatus_SEND.getTypeName());
                    /// 更新数据库
                    CLMessageBean.insertMessageData(messageBean);
                    SessionMessageHandler(messageBean);
                    messageStatusOnListener.onSuccess(messageBean);
                }
            }

            @Override
            public void onFailure(String message) {
                if (messageStatusOnListener != null){
                    CLMessageBean messageBean = JSON.parseObject(message, CLMessageBean.class);
                    /// 发送失败 CLSendStatus
                    messageBean.setSendStatus(SendStatus_FAILED.getTypeName());
                    /// 更新数据库
                    CLMessageBean.insertMessageData(messageBean);
                    SessionMessageHandler(messageBean);
                    messageStatusOnListener.onFailure(messageBean);
                }
            }
        });
    }

    /// 收到聊天消息
    public void receiveMessageHandler(String msg){
        CLMessageBean messageBean = JSON.parseObject(msg, CLMessageBean.class);
        /// 过滤掉自己发的信息
        if (messageBean == null || messageBean.getCurrentUserId() == null) {
            return;
        }
        if (messageBean.getCurrentUserId().equals(CLUserManager.getInstence().getUserInfo().getUserId())){
            return;
        }
        messageBean.setTargetId(messageBean.getCurrentUserId());
        messageBean.setCurrentUserId(CLUserManager.getInstence().getUserInfo().getUserId());

        messageBean.setReceivedStatus(CLReceivedStatus.ReceivedStatus_UNREAD.getTypeName());
        /// 插入数据库
        CLMessageBean.insertMessageData(messageBean);
        SessionMessageHandler(messageBean);
        if (receiveMessageOnListener != null){
            receiveMessageOnListener.onMessage(messageBean);
        }
    }

    /// 插入消息会话列表
    public void SessionMessageHandler(CLMessageBean messageBean){
        CLSessionBean sessionBean = new CLSessionBean();
        CLFriendBean friendUserInfo = CLFriendBean.getFriendUserInfo(messageBean.getTargetId());
        if (friendUserInfo != null){
            sessionBean.setAliasName(friendUserInfo.getAliasName());
            sessionBean.setAvatarUrl(friendUserInfo.getAvatarUrl());
            sessionBean.setName(friendUserInfo.getName());
        }else {
            sessionBean.setAliasName(messageBean.getUserInfo().getAliasName());
            sessionBean.setAvatarUrl(messageBean.getUserInfo().getAvatarUrl());
            sessionBean.setName(messageBean.getUserInfo().getName());
        }
        sessionBean.setCurrentUserId(CLUserManager.getInstence().getUserInfo().getUserId());
        sessionBean.setContent(messageBean.getContent());
        sessionBean.setGroupSession(messageBean.isGroupSession());
        sessionBean.setMessageType(messageBean.getMessageType());
        sessionBean.setSendStatus(messageBean.getSendStatus());
        sessionBean.setTargetId(messageBean.getTargetId());
        sessionBean.setSendTime(messageBean.getSendTime());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 插入数据库
                CLSessionBean.updateData(sessionBean);
            }
        });
    }

    /// 收到添加好友
    public void receiveFriendHandler(String msg){
        CLFriendBean friendBean = CLJsonUtil.parseJsonToObj(msg, CLFriendBean.class);
        friendBean.setCurrentUserId(CLUserManager.getInstence().getUserInfo().getUserId());
        if (receiveFriendOnListener != null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /// 插入好友数据库
                    CLFriendBean.updateData(friendBean);
                }
            });
        }
    }

    /// 收到好友邀请
    public void receiveInviteFriendHandler(String msg){
        CLNewFriendBean friendBean = CLJsonUtil.parseJsonToObj(msg, CLNewFriendBean.class);
        friendBean.setCurrentUserId(CLUserManager.getInstence().getUserInfo().getUserId());
        if (receiveFriendOnListener != null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ///
                    CLNewFriendBean.updateData(friendBean);
                    receiveFriendOnListener.onMessage(friendBean);
                }
            });
        }
    }

    private class MyUploadListener implements CLUploadManager.CLUploadOnLitenser {
        @Override
        public void uploadSuccess(String fileName) {
            LogPrintUtils.eTag("上传成功",fileName);
            if (uploadStatusOnListener != null){
                uploadStatusOnListener.onSuccess(fileName);
            }
        }
        @Override
        public void uploadError(String fileName) {
            if (uploadStatusOnListener != null){
                uploadStatusOnListener.onFailure(fileName);
            }
        }
        @Override
        public void uploadProgress(int progress) {

        }
        @Override
        public void uploadCancel(String fileName) {

        }
    }

}
