package com.cleven.clchat.manager;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import dev.utils.LogPrintUtils;
import dev.utils.app.assist.manager.ThreadManager;

import static com.cleven.clchat.utils.CLAPPConst.MQTT_ADD_FRIEND_TOPIC;
import static com.cleven.clchat.utils.CLAPPConst.MQTT_BASE_TOPIC;
import static com.cleven.clchat.utils.CLAPPConst.MQTT_BROKER_HOST;
import static com.cleven.clchat.utils.CLAPPConst.MQTT_CLIENT_ID;
import static com.cleven.clchat.utils.CLAPPConst.MQTT_CONNECT_TOPIC;
import static com.cleven.clchat.utils.CLAPPConst.MQTT_DELETE_FRIEND_TOPIC;
import static com.cleven.clchat.utils.CLAPPConst.MQTT_DISCONNECT_TOPIC;
import static com.cleven.clchat.utils.CLAPPConst.MQTT_GROUP_CHAT_TOPIC;
import static com.cleven.clchat.utils.CLAPPConst.MQTT_SINLE_CHAT_TOPIC;
import static com.cleven.clchat.utils.CLAPPConst.MQTT_SYSTEM_TOPIC;

public class CLMQTTManager {

    private final static String TAG = CLMQTTManager.class.getSimpleName();

    /// 连接状态枚举
    public enum CLMQTTStatus {
        connect_onknow,
        connect_succss,
        connect_fail,
        disconnect_success,
        disconnect_fail,
    }
    /// 连接失败重拾次数
    private int retryCount = 0;
    ///
    private boolean isConnect = false;

    /**
     * 用户名
     */
    public static final String USERNAME = "admin";
    /**
     *  密码
     */
    public static final String PASSWORD = "password";

    private MqttAndroidClient client;
    private MqttConnectOptions options;

    private static CLMQTTManager instance = new CLMQTTManager();
    private Context mContext;

    private CLMQTTManager(){}
    public static CLMQTTManager getInstance(){
        return instance;
    }

    /**
     * 当前连接状态
     */
    public CLMQTTStatus currentStatus;
    public void setCurrentStatus(CLMQTTStatus currentStatus) {
        this.currentStatus = currentStatus;
        /// 回调状态
        if (connectStatusOnListener != null){
            connectStatusOnListener.onConnectStatus(currentStatus);
        }
    }
    public CLMQTTStatus getCurrentStatus() {
        return currentStatus;
    }

    /**
     * 定义连接状态的回调接口
     */
    public interface CLMQTTConnectStatusOnListener{
        void onConnectStatus(CLMQTTStatus status);
    }
    private CLMQTTConnectStatusOnListener connectStatusOnListener;
    public void setConnectStatusOnListener(CLMQTTConnectStatusOnListener connectStatusOnListener) {
        this.connectStatusOnListener = connectStatusOnListener;
    }

    /**
     * 定义发送消息回调接口
     */
    public interface CLSendMessageStatusOnListener {
        void onSuccess(String message);
        void onFailure(String message);
    }
    private CLSendMessageStatusOnListener messageStatusOnListener;
    public void setMessageStatusOnListener(CLSendMessageStatusOnListener messageStatusOnListener) {
        this.messageStatusOnListener = messageStatusOnListener;
    }

    /**
     * 连接MQTT
     * @param context 上下文
     */
    public void connectMQTT(Context context){
        this.mContext = context;
        initMQTT();
    }

    /**
     * 断开连接MQTT
     */
    public void disconnectMQTT(){
        if (client != null && client.isConnected()){
            try {
                /// 断开之前先告诉服务端
                String userId = CLUserManager.getInstence().getUserInfo().getUserId();
                client.publish(MQTT_BASE_TOPIC+MQTT_DISCONNECT_TOPIC + userId,"disconnect".getBytes(),1,false);
                client.disconnect(null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.d(TAG,"断开连接成功");
                        currentStatus = CLMQTTStatus.disconnect_success;
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.d(TAG,"断开连接失败");
                        currentStatus = CLMQTTStatus.disconnect_fail;
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
                currentStatus = CLMQTTStatus.disconnect_fail;
            }
        }else {
            Log.d(TAG,"未连接,请先连接");
            currentStatus = CLMQTTStatus.connect_onknow;
        }
    }

    /**
     * 发送单聊消息
     * @param msg 文本
     */
    public void sendSingleMessage(final String msg, String userId){
        sendMessage(msg,MQTT_SINLE_CHAT_TOPIC,userId);
    }

    /**
     * 添加好友
     * @param userId
     */
    public void sendAddFriendMessage(String userId){
        String msg = JSON.toJSONString(CLUserManager.getInstence().getUserInfo());
        sendMessage(msg,MQTT_ADD_FRIEND_TOPIC,userId);
    }

    public void sendMessage(final String msg, String topic, String userId){
        if (client != null && client.isConnected()){
            final MqttMessage message = new MqttMessage();
            message.setQos(1);
            message.setRetained(false);
            message.setPayload(msg.getBytes());

            try {
                client.publish(MQTT_BASE_TOPIC + topic + userId,message,null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        if (messageStatusOnListener != null){
                            messageStatusOnListener.onSuccess(msg);
                        }
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        if (messageStatusOnListener != null) {
                            messageStatusOnListener.onFailure(msg);
                        }
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }else {
            Log.d(TAG,"未连接,请先连接");
            currentStatus = CLMQTTStatus.connect_onknow;
        }
    }

    /// 初始化MQTT
    private void initMQTT() {
        if (isConnect == true){return;}
        isConnect = true;
        client = null;
        if (CLUserManager.getInstence().getUserInfo() == null){return;}
        final String userId = CLUserManager.getInstence().getUserInfo().getUserId();
        client = new MqttAndroidClient(mContext,MQTT_BROKER_HOST,MQTT_CLIENT_ID == null ? userId : MQTT_CLIENT_ID);
        options = new MqttConnectOptions();
        /// 保持会话
        options.setCleanSession(false);
        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
        options.setKeepAliveInterval(60);
        // 设置超时时间 单位为秒
        options.setConnectionTimeout(30);
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());
        /// 设置遗嘱
        options.setWill(MQTT_BASE_TOPIC+MQTT_DISCONNECT_TOPIC + userId,"disconnect".getBytes(),1,false);
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.e(TAG,"断开连接了");
                currentStatus = CLMQTTStatus.disconnect_success;
                /// 重新连接
                initMQTT();
            }
            /// 消息到达
            @Override
            public void messageArrived(final String topic, final MqttMessage message) throws Exception {
                Log.d(TAG,message.toString());
                LogPrintUtils.eTag(TAG,"topic = " + topic);
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        String baseTopic = MQTT_BASE_TOPIC;
                        if (topic.equals(baseTopic + MQTT_SINLE_CHAT_TOPIC + userId)){/// 单聊
                            CLMessageManager.getInstance().receiveMessageHandler(message.toString());
                        }else if (topic.equals(baseTopic + MQTT_GROUP_CHAT_TOPIC + userId)){ //群聊

                        }else if (topic.equals(baseTopic + MQTT_ADD_FRIEND_TOPIC + userId)){ //添加好友
                            CLMessageManager.getInstance().receiveFriendHandler(message.toString());
                        }else if (topic.equals(baseTopic + MQTT_DELETE_FRIEND_TOPIC + userId)){ //删除好友

                        }else if (topic.equals(baseTopic + MQTT_SYSTEM_TOPIC)){ //系统通知

                        }
                    }
                };
                /// 添加到线程池中执行
                ThreadManager.getInstance().addTask(runnable);
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                try {
                    Log.d(TAG,token.getMessage().toString());
                } catch (MqttException e) {
                    e.printStackTrace();
                    Log.e(TAG,e.getMessage());
                }
            }
        });
        try {
            client.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.e(TAG,"连接成功");
                    retryCount = 0;
                    isConnect = false;
                    currentStatus = CLMQTTStatus.connect_succss;
                    try {
                        /// 连接成功通知服务器
                        client.publish(MQTT_BASE_TOPIC + MQTT_CONNECT_TOPIC + userId,"connect".getBytes(),1,false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    subscribeTopic();
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG,exception.getLocalizedMessage());
//                    this.retryCount = 0;
                    isConnect = false;
                    if (retryCount < 3) {
                        initMQTT();
                        retryCount += 1;
                    }else {
                        currentStatus = CLMQTTStatus.connect_fail;
                        retryCount = 0;
                    }
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            currentStatus = CLMQTTStatus.connect_fail;
        }

    }

    /**
     * 订阅主题
     */
    private void subscribeTopic(){
        String userId = CLUserManager.getInstence().getUserInfo().getUserId();
        // 订阅
        final String[] topic = new String[5];
        /// 单聊
        topic[0] = MQTT_BASE_TOPIC + MQTT_SINLE_CHAT_TOPIC + userId;
        /// 群聊
        topic[1] = MQTT_BASE_TOPIC + MQTT_GROUP_CHAT_TOPIC + userId;
        /// 添加好友
        topic[2] = MQTT_BASE_TOPIC + MQTT_ADD_FRIEND_TOPIC + userId;
        /// 删除好友
        topic[3] = MQTT_BASE_TOPIC + MQTT_DELETE_FRIEND_TOPIC + userId;
        /// 系统通知
        topic[4] = MQTT_BASE_TOPIC + MQTT_SYSTEM_TOPIC;

        int[] qos = new int[topic.length];
        qos[0] = 1;
        qos[1] = 1;
        qos[2] = 1;
        qos[3] = 1;
        qos[4] = 1;
        try {
            client.subscribe(topic,qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}
