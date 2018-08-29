package wang.tinycoder.easyiotkit.module.home.fragment.dev;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import wang.tinycoder.easyiotkit.bean.GroupDevData;
import wang.tinycoder.easyiotkit.bean.NetResult;
import wang.tinycoder.easyiotkit.net.GlobalRetrofit;

/**
 * Progect：EasyLinkerAppNew
 * Package：wang.tinycoder.easylinkerapp.module.home.fragment.dev
 * Desc：
 * Author：TinycoderWang
 * CreateTime：2018/4/13 22:30
 */
public class DevManageModel implements DevManageContract.Model {
    @Override
    public void onDestroy() {

    }


    @Override
    public void requestAllDevices(int page, int count, Observer<NetResult<GroupDevData>> observer) {
        GlobalRetrofit.getInstance().getApi()
                .getAllDevices(page, count)
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
