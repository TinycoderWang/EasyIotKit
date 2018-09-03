package wang.tinycoder.easyiotkit.bean;

/**
 * Progect：EasyIotKit
 * Package：wang.tinycoder.easyiotkit.bean
 * Desc：设备上传的数据
 * Author：TinycoderWang
 * CreateTime：2018/8/26 9:55
 */
public class DeviceData {


    /**
     * data : {"data":"25.688473830663078-91.6602942702875-0"}
     * create_time : 2018-08-29T09:09:06.000+0000
     * id : 1535533831085
     */

    private DeviceReciveData data;
    private String create_time;
    private long id;

    public DeviceReciveData getData() {
        return data;
    }

    public void setData(DeviceReciveData data) {
        this.data = data;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
