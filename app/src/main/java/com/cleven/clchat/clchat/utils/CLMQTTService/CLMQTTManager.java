package com.cleven.clchat.clchat.utils.CLMQTTService;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Date;
import java.util.Map;

/**
 * Created by cleven on 2018/12/12.
 */

public class CLMQTTManager {

    /**
     * 代理服务器ip地址
     */
    public static final String MQTT_BROKER_HOST = "tcp://192.168.31.98:1883";

    /**
     * 客户端唯一标识
     */
    public static final String MQTT_CLIENT_ID = "android-CLChat";

    /**
     * 订阅标识
     */
    public static final String MQTT_TOPIC = "chat";

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
            mqttClient = new MqttClient(MQTT_BROKER_HOST,MQTT_CLIENT_ID,new MemoryPersistence());
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
            options.setKeepAliveInterval(20);
            // 连接
            mqttClient.connect(options);

            // 订阅
            mqttClient.subscribe(MQTT_TOPIC);

            // 设置回调
            mqttClient.setCallback(new MqttCallback() {
                //连接丢失后，一般在这里面进行重连
                @Override
                public void connectionLost(Throwable throwable) {
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
    public void sendMessage(Map msg){
        if (mqttClient != null && mqttClient.isConnected()){
            MqttMessage message = new MqttMessage();
            Date date = new Date();
            String timestamp = String.valueOf(date.getTime()/1000);
//            message.setId(Integer.valueOf(StringUtils.toUTF8Encode("1000")));
            message.setQos(1);
            message.setRetained(false);
//            message.setPayload(ByteUtils.objectToByte(msg));
            message.setPayload("回复".getBytes());
            System.out.println("\"======\" + msg.toString().getBytes()");
            try {
                mqttClient.publish("chat",message);
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
                    mqttClient.disconnect();
                    mqttClient = null;
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
