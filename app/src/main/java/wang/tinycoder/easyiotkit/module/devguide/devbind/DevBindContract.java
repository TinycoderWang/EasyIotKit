package wang.tinycoder.easyiotkit.module.devguide.devbind;

import io.reactivex.Observer;
import wang.tinycoder.easyiotkit.base.interfaces.IModel;
import wang.tinycoder.easyiotkit.base.interfaces.IView;
import wang.tinycoder.easyiotkit.bean.NetResult;

/**
 * Progect：EasyLinkerAppNew
 * Package：wang.tinycoder.easylinkerapp.module.device.devbind
 * Desc：绑定设备页面的契约类
 * Author：TinycoderWang
 * CreateTime：2018/4/27 20:57
 */
public interface DevBindContract {

    interface View extends IView {

        void onBindResult(boolean result);
    }

    interface Model extends IModel {
        /**
         * 绑定设备
         *
         * @param groupId  群组id
         * @param deviceid 设备id
         * @param observer
         */
        void bindDevice(String groupId, String deviceid, Observer<NetResult> observer);


        /**
         * 新增设备
         * @param deviceId 设备id
         * @param observer
         */
        void bindDeviceToDefaultGroup(String deviceId, Observer<NetResult> observer);
    }

}
