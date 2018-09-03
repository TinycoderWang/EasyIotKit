package wang.tinycoder.easyiotkit.bean;

/**
 * Progect：EasyIotKit
 * Package：wang.tinycoder.easyiotkit.bean
 * Desc：
 * Author：TinycoderWang
 * CreateTime：2018/8/31 20:32
 */
public class DeviceReciveData {
    private String data;

    public DeviceReciveData() {
    }


    public DeviceReciveData(String data) {
        this.data = data;
    }


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
