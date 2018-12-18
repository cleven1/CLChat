package com.cleven.clchat.manager;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class CLMQTTManager {

    enum CLMQTTStatus {
        connect_onknow,
        connect_succss,
        connect_fail,
        disconnect_success,
        disconnect_fail,
    }

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
                client.publish(MQTT_BASE_TOPIC+MQTT_DISCONNECT_TOPIC + "1","用户1 断开连接".getBytes(),1,false);
                client.disconnect(null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.d("MQTT","断开连接成功");
                        currentStatus = CLMQTTStatus.disconnect_success;
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.d("MQTT","断开连接失败");
                        currentStatus = CLMQTTStatus.disconnect_fail;
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
                currentStatus = CLMQTTStatus.disconnect_fail;
            }
        }else {
            Log.d("MQTT","未连接,请先连接");
            currentStatus = CLMQTTStatus.connect_onknow;
        }
    }

    /**
     * 发送单聊消息
     * @param msg 文本
     */
    public void sendSingleMessage(String msg,String userId){
        if (client != null && client.isConnected()){
            MqttMessage message = new MqttMessage();
            message.setQos(1);
            message.setRetained(false);
            message.setPayload(msg.getBytes());

            try {
                client.publish(MQTT_BASE_TOPIC + MQTT_SINLE_CHAT_TOPIC + userId,message,null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Toast.makeText(mContext,"发送成功",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Toast.makeText(mContext,"发送失败",Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }else {
            Log.d("MQTT","未连接,请先连接");
            currentStatus = CLMQTTStatus.connect_onknow;
        }
    }

    /// 初始化MQTT
    private void initMQTT() {
        final String userId = CLUserManager.getInstence().getUserInfo().getUserId();
        client = new MqttAndroidClient(mContext,MQTT_BROKER_HOST,MQTT_CLIENT_ID == null ? userId : MQTT_CLIENT_ID);
        options = new MqttConnectOptions();
        options.setCleanSession(true);
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
                Log.e("断开连接了","断开连接了");
                currentStatus = CLMQTTStatus.disconnect_success;
            }
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("收到消息",message.toString());
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                try {
                    Log.d("发送",token.getMessage().toString());
                } catch (MqttException e) {
                    e.printStackTrace();
                    Log.e("失败",e.getMessage());
                }
            }
        });
        try {
            client.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("TA","连接成功");
                    currentStatus = CLMQTTStatus.connect_succss;
                    // 订阅
                    final String[] topic = new String[1];
                    /// 订阅别人发给自己的消息
                    topic[0] = MQTT_BASE_TOPIC + MQTT_SINLE_CHAT_TOPIC + userId;
                    int[] qos = new int[topic.length];
                    qos[0] = 1;
                    try {
                        client.subscribe(topic,qos);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("失败",exception.getLocalizedMessage());
//                    this.retryCount = 0;
                    currentStatus = CLMQTTStatus.connect_fail;
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            currentStatus = CLMQTTStatus.connect_fail;
        }

    }

}
