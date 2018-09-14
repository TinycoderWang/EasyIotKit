package wang.tinycoder.easyiotkit.app;

/**
 * Progect：EasyLinkerApp
 * Package：wang.tinycoder.easylinkerapp.app
 * Desc：常量类
 * Author：TinycoderWang
 * CreateTime：2018/4/1 13:07
 */
public class Constants {

    // 服务器地址·
//    public static final String BASE_URL = "http://10.0.2.2:2500/";
//    public static final String BASE_HOST = "http://10.0.2.2/";
    public static final String BASE_URL = "http://116.196.90.9:2500/";
    public static final String BASE_HOST = "http://116.196.90.9/";
    public static final String MQTT_URL = "116.196.90.9";
    public static final int MQTT_PORT = 1883;
    public static final int UDP_SEND_PORT = 2345;
    public static final int UDP_LISTEN_PORT = 2345;
    public static final String UDP_BORDCAST_ADDR = "255.255.255.255";

    // 接收的mqtt topic模板
    public static final String RECIVE_TOPIC_TEMP = "OUT/DEVICE/%s/%s/%s";
    // 发送的mqtt topic模板
    public static final String SEND_TOPIC_TEMP = "IN/DEVICE/%s/%s/%s";
    // sp名称
    public static final String SP_NAME = "easyiotkit";
    // 是否记住用户状态
    public static final String REMEMBER_USER_STATE = "remember_user_state";
    // 群组bean
    public static final String EXTRA_GROUP_BEAN = "extra_group_bean";
    // 群组id
    public static final String EXTRA_GROUP_ID = "extra_group_id";
    // 是否关闭其他activity
    public static final String EXTRA_CLOSE_OTHER_ACTIVITY = "extra_close_other_activity";
    // 所在城市
    public static final String EXTRA_CITY_NAME = "extra_city_name";
    // 所在城市经度
    public static final String EXTRA_CITY_LONGITUDE = "extra_city_longitude";
    // 所在城市纬度
    public static final String EXTRA_CITY_LATITUDE = "extra_city_latitude";
    // 设备
    public static final String EXTRA_DEVICE = "extra_device";
    public static final String EXTRA_DEVICE_KEY = "extra_device_key";
    
    public static final String WEB_TITLE = "web_title";
    public static final String WEB_URL = "web_url";

}
