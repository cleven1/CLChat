package com.cleven.clchat.clchat.utils.CLMQTTService;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Date;
import java.util.Map;

import dev.utils.LogPrintUtils;
import dev.utils.app.ADBUtils;
import dev.utils.common.StringUtils;

/**
 * Created by cleven on 2018/12/12.
 */

public class CLMQTTManager {

    /**
     * 代理服务器ip地址
     */
    public static final String MQTT_BROKER_HOST = "tcp://192.168.31.98:1883";
//    public static final String MQTT_BROKER_HOST = "tcp://192.168.10.6:1883";

    /**
     * 客户端唯一标识
     */
    public static final String MQTT_CLIENT_ID = ADBUtils.getIMEI();//"android-CLChat";

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

    private MqttClient mqttClient;


    private CLMQTTManager() {

    }
    private static class SingleHodler{
        private static final CLMQTTManager instance=new CLMQTTManager();
    }
    public static CLMQTTManager getInstance() {
        return SingleHodler.instance;
    }

    /**
     * 连接mqtt
     */
    public void connect(){
        try {
            // host为主机名，clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，
            // MemoryPersistence设置clientid的保存形式，默认为以内存保存
            LogPrintUtils.d("安卓标识 = " + MQTT_CLIENT_ID);
            LogPrintUtils.d("安卓标识 androidID = " + ADBUtils.getAndroidId());
            mqttClient = new MqttClient(MQTT_BROKER_HOST,MQTT_CLIENT_ID == null ? "android-client" : MQTT_CLIENT_ID,new MemoryPersistence());
            // 配置参数信息
            MqttConnectOptions options = new MqttConnectOptions();
            // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，
            // 这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(false);
            // 设置用户名
            options.setUserName(USERNAME);
            // 设置密码
            options.setPassword(PASSWORD.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(10);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(60);
            /// 设置遗嘱
            options.setWill( MQTT_BASE_TOPIC+MQTT_DISCONNECT_TOPIC + "1","android 断开连接".getBytes(),1,false);
            // 连接
            IMqttToken connectWithResult = mqttClient.connectWithResult(options);
            if (connectWithResult.getClient().getClientId() != null){
                // 连接成功
                mqttClient.publish(MQTT_BASE_TOPIC+MQTT_CONNECT_TOPIC + "1","用户1 连接成功".getBytes(),1,false);
            }
            LogPrintUtils.d("连接结果 = "+ connectWithResult.toString());
            LogPrintUtils.d("连接结果 clientId = "+ connectWithResult.getClient().getClientId());
            LogPrintUtils.d("连接结果 serverId = "+ connectWithResult.getClient().getServerURI());
            // 订阅
            String[] topic = new String[1];
            topic[0] = MQTT_BASE_TOPIC + MQTT_SINLE_CHAT_TOPIC + "1";
            int[] qos = new int[topic.length];
            qos[0] = 1;
            mqttClient.subscribe(topic,qos);

            // 设置回调
            mqttClient.setCallback(new MqttCallback() {
                //连接丢失后，一般在这里面进行重连
                @Override
                public void connectionLost(Throwable throwable) {
                    try {
                        mqttClient.publish(MQTT_BASE_TOPIC+MQTT_DISCONNECT_TOPIC + "1","用户1 断开连接".getBytes(),1,false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    Log.d("test","connectionLost");
                }


                //subscribe后得到的消息会执行到这里面
                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    Log.d("test","messageArrived = "+mqttMessage.toString());
                }

                //publish后会执行到这里
                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    Log.d("test","deliveryComplete");
                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送消息
     * @param msg 消息体
     */
    public void sendMessage(String userId,Map msg){
        if (mqttClient != null && mqttClient.isConnected()){
            MqttMessage message = new MqttMessage();
            Date date = new Date();
            String timestamp = String.valueOf(date.getTime()/1000);
            message.setId(Integer.valueOf(StringUtils.toUTF8Encode(timestamp)));
            message.setQos(1);
            message.setRetained(false);
            message.setPayload(msg.toString().getBytes());
            try {
                mqttClient.publish( MQTT_BASE_TOPIC + MQTT_SINLE_CHAT_TOPIC + userId,message);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 断开连接
     */
    public void disconnect(){
        if(mqttClient != null){
            if(mqttClient.isConnected()){
                try {
                    mqttClient.publish(MQTT_BASE_TOPIC+MQTT_DISCONNECT_TOPIC + "1","用户1 断开连接".getBytes(),1,false);
                    mqttClient.disconnect();
                    mqttClient = null;
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
