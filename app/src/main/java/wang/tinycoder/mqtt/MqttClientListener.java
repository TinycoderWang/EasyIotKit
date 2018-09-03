package wang.tinycoder.mqtt;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Progect：EasyIotKit
 * Package：wang.tinycoder.mqtt
 * Desc：
 * Author：TinycoderWang
 * CreateTime：2018/9/2 7:46
 */
public interface MqttClientListener {


    //所有成功回调--> 包括成功连接服务器，成功订阅主题，成功推送消息
    void onSuccessMQTT(MqttCode code, String topic, String message);

    //所有失败回调
    void onFailureMQTT(MqttCode code, String errorMesage);

    //成功接收到服务器的信息回调
    void MessageArrived(String topic, MqttMessage message);


}