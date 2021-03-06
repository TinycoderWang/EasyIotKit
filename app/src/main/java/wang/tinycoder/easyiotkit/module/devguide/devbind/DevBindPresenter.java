package wang.tinycoder.easyiotkit.module.devguide.devbind;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import wang.tinycoder.easyiotkit.base.BasePresenter;
import wang.tinycoder.easyiotkit.bean.Device;
import wang.tinycoder.easyiotkit.bean.NetResult;


/**
 * Progect：EasyLinkerAppNew
 * Package：wang.tinycoder.easylinkerapp.module.device.devbind
 * Desc：
 * Author：TinycoderWang
 * CreateTime：2018/4/27 20:59
 */
public class DevBindPresenter extends BasePresenter<DevBindContract.View, DevBindContract.Model> {
    public DevBindPresenter(DevBindContract.View rootView, DevBindContract.Model model) {
        super(rootView, model);
    }

    /**
     * 绑定设备
     *
     * @param groupId
     * @param deviceId
     */
    public void bindDevice(String groupId, String deviceId) {

        if (TextUtils.isEmpty(groupId)) {   // 没有分组id，绑定到默认分组

            mModel.bindDeviceToDefaultGroup(deviceId, bindObserver());

        } else {
            mModel.bindDevice(groupId, deviceId, bindObserver());
        }

    }

    private Observer<NetResult<Device>> bindObserver() {
        return new Observer<NetResult<Device>>() {
            @Override
            public void onSubscribe(Disposable d) {
                mCompositeDisposable.add(d);
            }

            @Override
            public void onNext(NetResult<Device> deviceNetResult) {
                if (deviceNetResult != null) {

                    if (NetResult.SUCCESS == deviceNetResult.getState()) {
                        Device device = deviceNetResult.getData();
                        mView.onBindResult(true,device);
                    } else {
                        mView.onBindResult(false,null);
                    }

                    mView.showMessage(deviceNetResult.getMessage());

                } else {
                    mView.showMessage("网络异常，请稍后再试！");
                    mView.onBindResult(false,null);
                }
            }


            @Override
            public void onError(Throwable e) {
                Logger.e("%s requestDeviceBygroup error ! MSG : %s", TAG, e.getMessage());
                mView.onBindResult(false,null);
            }

            @Override
            public void onComplete() {
                Logger.i("%s requestDeviceBygroup onComplete !", TAG);
            }
        };
    }
}
