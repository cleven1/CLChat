package com.cleven.clchat.utils;

/**
 * 所有常量
 */
public class CLAPPConst {

    /**
     * 代理服务器ip地址
     */
    public static final String MQTT_BROKER_HOST = "tcp://47.74.178.206:1883";
//    public static final String MQTT_BROKER_HOST = "tcp://192.168.31.98:1883";
//    public static final String MQTT_BROKER_HOST = "tcp://192.168.10.6:1883";

    /**
     * 客户端唯一标识
     */
    public static final String MQTT_CLIENT_ID = "android-client";//ADBUtils.getIMEI();//"android-CLChat";

    /**
     * 订阅标识
     */
    public static final String MQTT_BASE_TOPIC = "CLChat/topic/";
    /// 单聊 + userId
    public static final String MQTT_SINLE_CHAT_TOPIC = "chat/single/";
    /// 群聊 + groupId
    public static final String MQTT_GROUP_CHAT_TOPIC = "chat/group/";
    /// 邀请 + userId
    public static final String MQTT_INVITE_TOPIC = "chat/invite/";
    /// 添加好友 + userId
    public static final String MQTT_ADD_FRIEND_TOPIC = "add/friend/";
    /// 删除好友 + userId
    public static final String MQTT_DELETE_FRIEND_TOPIC = "delete/friend/";
    /// 系统通知
    public static final String MQTT_SYSTEM_TOPIC = "system";
    /// 连接成功 + userId
    public static final String MQTT_CONNECT_TOPIC = "connect/user/";
    /// 断开连接 + userId
    public static final String MQTT_DISCONNECT_TOPIC = "disconnect/user/";
}
