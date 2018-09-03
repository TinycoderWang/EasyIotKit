package wang.tinycoder.mqtt;

/**
 * Progect：EasyIotKit
 * Package：wang.tinycoder.mqtt
 * Desc：
 * Author：TinycoderWang
 * CreateTime：2018/9/2 7:47
 */
public enum MqttCode {
    /**
     * 连接服务器失败
     */
    CONNECT_FAIL,
    /**
     * 发布消息失败
     */
    PUBLISH_FAIL,

    /**
     * 订阅消息失败
     */
    SUB_FAIL,

    /**
     * 连接服务器成功
     */
    CONNECTED_SUCCEED,
    /**
     * 发布消息成功
     */
    PUBLISH_SUCCEED,
    /**
     * 订阅成功
     */
    SUB_SUCCEED,
    /**
     * 断开连接失败
     */
    DISCONNECTED_FAIL,
    /**
     * 断开连接成功
     */
    DISCONNECTED_SUCCEED
}
