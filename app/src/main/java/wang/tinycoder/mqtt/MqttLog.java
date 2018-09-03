package wang.tinycoder.mqtt;

import android.util.Log;

/**
 * Progect：EasyIotKit
 * Package：wang.tinycoder.mqtt
 * Desc：
 * Author：TinycoderWang
 * CreateTime：2018/9/2 7:47
 */
public class MqttLog {


    private static boolean OPENLOG = true;

    private static final String TAG = "MqttLog";

    public static void i(String tag, String msg) {
        if (OPENLOG) {
            Log.i(tag, msg);
        }
    }

    public static void i(String msg) {
        if (OPENLOG) {
            Log.i(TAG, msg);
        }
    }


    public static void d(String tag, String msg) {
        if (OPENLOG) {
            Log.d(tag, msg);
        }
    }

    public static void d(String msg) {
        if (OPENLOG) {
            Log.d(TAG, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (OPENLOG) {
            Log.e(tag, msg);
        }
    }

    public static void e(String msg) {
        if (OPENLOG) {
            Log.e(TAG, msg);
        }
    }

    public static void setOpenLog(boolean openLog) {
        MqttLog.OPENLOG = openLog;
    }
}

