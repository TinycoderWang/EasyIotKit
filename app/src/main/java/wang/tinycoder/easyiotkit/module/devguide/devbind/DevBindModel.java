package wang.tinycoder.easyiotkit.module.devguide.devbind;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wang.tinycoder.easyiotkit.bean.Device;
import wang.tinycoder.easyiotkit.bean.NetResult;
import wang.tinycoder.easyiotkit.net.GlobalRetrofit;

/**
 * Progect：EasyLinkerAppNew
 * Package：wang.tinycoder.easylinkerapp.module.device.devbind
 * Desc：
 * Author：TinycoderWang
 * CreateTime：2018/4/27 20:59
 */
public class DevBindModel implements DevBindContract.Model {
    @Override
    public void onDestroy() {

    }


    /**
     * 绑定设备
     *
     * @param groupId  群组id
     * @param deviceId 设备id
     * @param observer
     */
    @Override
    public void bindDevice(String groupId, String deviceId, Observer<NetResult<Device>> observer) {
        GlobalRetrofit.getInstance().getApi()
                .bindDevice(deviceId, groupId)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    public void bindDeviceToDefaultGroup(String deviceId, Observer<NetResult<Device>> observer) {
        GlobalRetrofit.getInstance().getApi()
                .bindDeviceByDefaute(deviceId)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }


}
