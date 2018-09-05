package wang.tinycoder.easyiotkit.net.udp;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import wang.tinycoder.easyiotkit.app.Constants;

/**
 * Progect：EasyIotKit
 * Package：wang.tinycoder.easyiotkit.net.udp
 * Desc：udp客户端
 * Author：TinycoderWang
 * CreateTime：2018/9/4 21:46
 */
public class UdpClient {

    // 接收消息的监听
    private ReceiveListener mReceiveListener;
    // 监听端口
    private int listenPort;
    // 发送端口
    private int sendPort;
    // ip地址
    private String sendIp;
    // 接收的数据长度
    private int byteLength;
    // 指示监听线程是否终止
    public boolean IsThreadDisable = false;
    // 是否开始监听
    private boolean alreadyListen = false;

    private UdpClient(Builder builder) {
        this.sendIp = builder.sendIp;
        this.sendPort = builder.sendPort;
        this.listenPort = builder.listenPort;
        this.byteLength = builder.byteLength;
        this.mReceiveListener = builder.receiveListener;
    }

    // 开始监听
    public void startListen() {
        if (!alreadyListen) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        alreadyListen = true;
                        // 数据大小
                        byte[] message = new byte[byteLength];
                        // 建立Socket连接
                        DatagramSocket datagramSocket = new DatagramSocket(listenPort);
                        datagramSocket.setBroadcast(true);
                        DatagramPacket datagramPacket = new DatagramPacket(message,
                                message.length);
                        while (!IsThreadDisable) {
                            // 准备接收数据
                            datagramSocket.receive(datagramPacket);
                            String strMsg = new String(datagramPacket.getData()).trim();
                            if (mReceiveListener != null) {
                                mReceiveListener.onReceiveMessage(strMsg);
                            }
                        }
                        alreadyListen = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }


    /**
     * 发送消息
     *
     * @param message
     */
    public void send(final String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramSocket socket = new DatagramSocket();
                    InetAddress local = InetAddress.getByName(sendIp);
                    byte[] messageByte = message.getBytes();
                    DatagramPacket packet = new DatagramPacket(messageByte, messageByte.length, local, sendPort);
                    socket.send(packet);
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public boolean isThreadDisable() {
        return IsThreadDisable;
    }

    public void setThreadDisable(boolean threadDisable) {
        IsThreadDisable = threadDisable;
    }


    public static class Builder {
        // 接收消息的监听
        private ReceiveListener receiveListener;
        // ip地址
        private String sendIp = Constants.UDP_BORDCAST_ADDR;
        // 端口
        private int sendPort = Constants.UDP_SEND_PORT;
        private int listenPort = Constants.UDP_LISTEN_PORT;
        // 接收的数据长度
        private int byteLength = 1024;

        public Builder(@NonNull String ip, int sendPort, int listenPort) {
            this.sendIp = ip;
            this.sendPort = sendPort;
            this.listenPort = listenPort;
        }

        // 设置监听
        public Builder setReceiveListener(ReceiveListener receiveListener) {
            this.receiveListener = receiveListener;
            return this;
        }

        // 设置数据长度
        public Builder setDataLength(int byteLength) {
            this.byteLength = byteLength;
            return this;
        }

        public UdpClient build() {
            return new UdpClient(this);
        }

    }


    public interface ReceiveListener {
        void onReceiveMessage(String message);
    }
}

