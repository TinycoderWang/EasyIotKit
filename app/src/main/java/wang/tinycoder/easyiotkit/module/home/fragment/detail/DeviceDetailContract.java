package wang.tinycoder.easyiotkit.module.home.fragment.detail;

import java.util.List;

import io.reactivex.Observer;
import wang.tinycoder.easyiotkit.base.interfaces.IModel;
import wang.tinycoder.easyiotkit.base.interfaces.IView;
import wang.tinycoder.easyiotkit.bean.DeviceData;
import wang.tinycoder.easyiotkit.bean.NetResult;

/**
 * Progect：EasyIotKit
 * Package：wang.tinycoder.easyiotkit.module.home.fragment.detail
 * Desc：
 * Author：TinycoderWang
 * CreateTime：2018/8/24 20:36
 */
public interface DeviceDetailContract {

    interface View extends IView {

        void update();

    }

    interface Model extends IModel {
        /**
         * 获取设备的数据
         *
         * @param page     页码
         * @param count    数量
         * @param deviceId 设备id
         * @param observer
         */
        void requestDeviceData(int page, int count, String deviceId, Observer<NetResult<List<DeviceData>>> observer);


        /**
         * 发送指令
         * @param deviceId 设备id
         * @param command 命令
         * @param observer
         */
        void sendCommandToDevice(String deviceId, String command, Observer<NetResult> observer);
    }

}
