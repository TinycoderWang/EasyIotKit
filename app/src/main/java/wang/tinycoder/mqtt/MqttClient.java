package wang.tinycoder.mqtt;

import android.content.Context;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Arrays;

/**
 * Progect：EasyIotKit
 * Package：wang.tinycoder.mqtt
 * Desc：
 * Author：TinycoderWang
 * CreateTime：2018/9/2 7:45
 */
public class MqttClient {


    //MqttAndroidClient
    private MqttAndroidClient mqttAndroidClient;

    private MqttConnectOptions mqttConnectOptions;

    //上下文
    private Context mContext;
    //本地的ID，设备唯一识别码
    private String clientID = null;
    //是否重连
    private boolean isAutoReConnect = true;
    //是否清除缓存
    private boolean isClearSession = true;
    //连接的用户名
    private String userName = null;
    //连接的用户名密码
    private char[] userPaw = null;
    //设置连接的超时时间
    private int connectionTimeout = 30;
    //设置心跳时间
    private int keepAliveTimes = 30;

    //服务器地址
    private String serverUrl = "116.196.90.9";
    //端口
    private int port = 1883;

    //订阅质量, 默认1
    private int quality = 1;

    //成功连接了的标志
    private boolean isConnected = false;

    private MqttClient(Context mContext) {
        this.mContext = mContext;
    }

    private MqttClientListener clientListener;


    /******************************************************下面是对外的方法******************************************************************************/
    /**
     * @return 是否成功连接服务器
     */
    public void startConnect() {

        mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(this.isAutoReConnect);
        mqttConnectOptions.setCleanSession(isClearSession);
        mqttConnectOptions.setUserName(userName);
        mqttConnectOptions.setPassword(userPaw);
        mqttConnectOptions.setConnectionTimeout(connectionTimeout);
        mqttConnectOptions.setKeepAliveInterval(keepAliveTimes);
        mqttAndroidClient = new MqttAndroidClient(mContext.getApplicationContext(), "tcp://" + this.serverUrl + ":" + port, this.clientID);

        try {
            MqttLog.e("startConnect ....");
            MqttLog.e("startConnect URL:" + mqttAndroidClient.getServerURI());
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    isConnected = true;
                    MqttLog.e("startConnect succeed !");
                    if (null != clientListener) {
                        clientListener.onSuccessMQTT(MqttCode.CONNECTED_SUCCEED, null, null);
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    isConnected = false;
                    MqttLog.e("startConnect onFailureMQTT:" + exception);
                    if (null != clientListener) {
                        clientListener.onFailureMQTT(MqttCode.CONNECT_FAIL, exception.getMessage());
                    }
                }
            });

            mqttAndroidClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    MqttLog.i("MqttCallback messageArrived ! this topic :" + topic + " , message:" + message.toString());
                    if (null != clientListener) {
                        clientListener.MessageArrived(topic, message);
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
            MqttLog.e("startConnect fail:" + e);
            if (null != clientListener) {
                clientListener.onFailureMQTT(MqttCode.CONNECT_FAIL, e.getMessage());
            }
        }
    }

    /**
     * @return 是否成功断开连接
     */
    public boolean disConnect() {
        if (null != mqttAndroidClient) {
            try {
                mqttAndroidClient.disconnect();
                if (null != clientListener) {
                    clientListener.onSuccessMQTT(MqttCode.DISCONNECTED_SUCCEED, null, null);
                }
                return true;
            } catch (MqttException exception) {
                exception.printStackTrace();
                if (null != clientListener) {
                    clientListener.onFailureMQTT(MqttCode.DISCONNECTED_FAIL, exception.getMessage());
                }
                return false;
            }
        }
        return false;
    }

    /**
     * 关闭连接，释放资源
     */
    public void close() {
        if (null != mqttAndroidClient) {
            mqttAndroidClient.close();
        }
    }


    /**
     * @param topic 订阅主题 , 质量为1
     * @return
     */
    public void subTopic(String topic) {
        if (null != mqttAndroidClient && isConnected) {
            MqttLog.d("mqttAndroidClient is not null !");
            try {
                mqttAndroidClient.subscribe(topic, 0, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        MqttLog.d("subTopic onSuccessMQTT...");
                        if (null != clientListener) {
                            clientListener.onSuccessMQTT(MqttCode.SUB_SUCCEED, null, null);
                        }
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        MqttLog.d("subTopic onFailureMQTT :" + exception.getMessage());
                        if (null != clientListener) {
                            clientListener.onFailureMQTT(MqttCode.SUB_FAIL, exception.getMessage());
                        }
                    }
                });

            } catch (MqttException exception) {
                exception.printStackTrace();
                MqttLog.d("subTopic fail:" + exception);
                if (null != clientListener) {
                    clientListener.onFailureMQTT(MqttCode.SUB_FAIL, exception.getMessage());
                }
            }
        }
    }

    /**
     * @param topic 订阅的主题
     * @param qos   质量
     * @return
     */
    public void subTopic(String topic, int qos) {
        if (null != mqttAndroidClient) {
            try {
                mqttAndroidClient.subscribe(topic, qos, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        MqttLog.d("subTopicWithQos onSuccessMQTT...");
                        if (null != clientListener) {
                            clientListener.onSuccessMQTT(MqttCode.SUB_SUCCEED, null, null);
                        }
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        MqttLog.d("subTopicWithQos onFailureMQTT :" + exception.getMessage());
                        if (null != clientListener) {
                            clientListener.onFailureMQTT(MqttCode.SUB_FAIL, exception.getMessage());
                        }
                    }
                });
                this.quality = qos;
            } catch (MqttException e) {
                e.printStackTrace();
                MqttLog.d("subTopic fail:" + e);
                if (null != clientListener) {
                    clientListener.onFailureMQTT(MqttCode.SUB_FAIL, e.getMessage());
                }
            }
        }

    }


    public void publishMessage(final String topic, final String message) {

        if (null != mqttAndroidClient && topic != null && null != message) {
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setPayload(message.getBytes());
            try {
                mqttAndroidClient.publish(topic, mqttMessage, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        MqttLog.d("publish onSuccessMQTT!");
                        if (null != clientListener) {
                            clientListener.onSuccessMQTT(MqttCode.PUBLISH_SUCCEED, topic, message);
                        }
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        MqttLog.d("publish fail:" + exception.getMessage());
                        if (null != clientListener) {
                            clientListener.onFailureMQTT(MqttCode.PUBLISH_FAIL, exception.getMessage());
                        }
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
                if (null != clientListener) {
                    clientListener.onFailureMQTT(MqttCode.PUBLISH_FAIL, e.getMessage());
                }
            }
        }
    }

    /**
     * @param clientListener 设置回调事件
     */
    public void setClientListener(MqttClientListener clientListener) {
        if (clientListener != null) {
            this.clientListener = clientListener;
        }
    }

    /**
     * @return 获取ClientID
     */
    public String getClientID() {
        return clientID;
    }

    /**
     * @return 是否设置重连
     */
    public boolean isAutoReConnect() {
        return isAutoReConnect;
    }


    public static class Builder {

        private MqttClient mAndroidClient;

        private Context mContext;


        public Builder(Context mContext) {
            mAndroidClient = new MqttClient(mContext);
        }


        public MqttClient creat() {
            return mAndroidClient;
        }

        /**
         * @param clientID 安卓客户端ID
         * @return
         */
        public Builder setClientID(String clientID) {
            mAndroidClient.clientID = clientID;
            return this;
        }

        /**
         * @param uri 服务器地址，默认：tcp://iot.eclipse.org
         * @return
         */
        public Builder setServerUri(String uri) {
            mAndroidClient.serverUrl = uri;
            return this;
        }

        /**
         * @param port 端口号，默认1883
         * @return
         */
        public Builder setPort(int port) {
            mAndroidClient.port = port;
            return this;
        }

        /**
         * @param isReConnect 设置重连，默认
         * @return
         */
        public Builder setReConnect(boolean isReConnect) {
            mAndroidClient.isAutoReConnect = isReConnect;
            return this;
        }

        /**
         * @param clientListener 设置监听
         * @return
         */
        public Builder setClientListener(MqttClientListener clientListener) {
            if (clientListener != null) {
                mAndroidClient.clientListener = clientListener;
            }
            return this;
        }

        /**
         * @param userName 用户名
         * @return
         */
        public Builder setUserName(String userName) {
            mAndroidClient.userName = userName;
            return this;
        }

        /**
         * @param password 密码
         * @return
         */
        public Builder setPassword(String password) {
            mAndroidClient.userPaw = password.toCharArray();
            return this;
        }
    }


    //是否已经连接
    public boolean isConnected() {
        return this.mqttAndroidClient.isConnected();
    }

    //获取连接的名字
    public String getUserName() {
        return this.userName;
    }

    //获取连接的密码
    public String getUserPaw() {
        return Arrays.toString(this.userPaw);
    }

    //获取URL
    public String getServerUrl() {
        return serverUrl;
    }

    //获取端口
    public int getPort() {
        return port;
    }

}

