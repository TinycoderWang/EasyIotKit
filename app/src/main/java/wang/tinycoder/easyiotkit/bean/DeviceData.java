package wang.tinycoder.easyiotkit.bean;

/**
 * Progect：EasyIotKit
 * Package：wang.tinycoder.easyiotkit.bean
 * Desc：设备上传的数据
 * Author：TinycoderWang
 * CreateTime：2018/8/26 9:55
 */
public class DeviceData {

    private String data;

    public DeviceData() {
    }


    public DeviceData(String data) {
        this.data = data;
    }


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
