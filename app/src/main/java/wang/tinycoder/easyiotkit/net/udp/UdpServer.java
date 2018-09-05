package wang.tinycoder.easyiotkit.net.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import wang.tinycoder.easyiotkit.app.Constants;

/**
 * Progect：EasyIotKit
 * Package：wang.tinycoder.easyiotkit.net.udp
 * Desc：
 * Author：TinycoderWang
 * CreateTime：2018/9/4 21:53
 */
public class UdpServer {

    // 接收消息的监听
    private ReceiveListener mReceiveListener;
    // 端口
    private int port;
    // 接收的数据长度
    private int byteLength;
    private boolean life = true;
    private boolean alreadyListene = false;

    private UdpServer(Builder builder) {
        this.port = builder.port;
        this.byteLength = builder.byteLength;
        this.mReceiveListener = builder.receiveListener;
    }

    public boolean isLife() {
        return life;
    }

    public void setLife(boolean life) {
        this.life = life;
    }

    public void startListen() {
        if (!alreadyListene) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        alreadyListene = true;
                        DatagramSocket dSocket = new DatagramSocket(port);
                        while (life) {
                            byte[] msg = new byte[byteLength];
                            DatagramPacket dPacket = new DatagramPacket(msg, msg.length);
                            dSocket.receive(dPacket);
                            String strMsg = new String(dPacket.getData(), dPacket.getOffset(),
                                    dPacket.getLength());
                            if (mReceiveListener != null) {
                                mReceiveListener.onReceiveMessage(strMsg);
                            }
                        }
                        alreadyListene = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }


    public static class Builder {
        // 接收消息的监听
        private ReceiveListener receiveListener;
        // 端口
        private int port = Constants.UDP_LISTEN_PORT;
        // 接收的数据长度
        private int byteLength = 1024;

        public Builder(int port) {
            this.port = port;
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

        public UdpServer build() {
            return new UdpServer(this);
        }

    }

    public interface ReceiveListener {
        void onReceiveMessage(String message);
    }
}
