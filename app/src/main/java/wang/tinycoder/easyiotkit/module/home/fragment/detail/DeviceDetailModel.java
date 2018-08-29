package wang.tinycoder.easyiotkit.module.home.fragment.detail;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import wang.tinycoder.easyiotkit.bean.DeviceData;
import wang.tinycoder.easyiotkit.bean.NetResult;
import wang.tinycoder.easyiotkit.net.GlobalRetrofit;

/**
 * Progect：EasyIotKit
 * Package：wang.tinycoder.easyiotkit.module.home.fragment.detail
 * Desc：
 * Author：TinycoderWang
 * CreateTime：2018/8/24 20:37
 */
public class DeviceDetailModel implements DeviceDetailContract.Model {
    @Override
    public void onDestroy() {

    }


    /**
     * 获取设备数据
     *
     * @param page     页码
     * @param count    数量
     * @param deviceId 设备id
     * @param observer
     */
    @Override
    public void requestDeviceData(int page, int count, String deviceId, Observer<NetResult<List<DeviceData>>> observer) {
        GlobalRetrofit.getInstance().getApi()
                .getDeviceData(page, count, deviceId)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void sendCommandToDevice(String deviceId, String command, Observer<NetResult> observer) {
        // 将参数封装为json
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"deviceId\":")
                .append(deviceId)
                .append(",")
                .append("\"payload\":")
                .append(command)
                .append("}");
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, application/json"), sb.toString());

        GlobalRetrofit.getInstance().getApi()
                .sendCommandToDevice(requestBody)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
