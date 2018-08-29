package wang.tinycoder.easyiotkit.module.home.fragment.dev;

import io.reactivex.Observer;
import wang.tinycoder.easyiotkit.base.interfaces.IModel;
import wang.tinycoder.easyiotkit.base.interfaces.IView;
import wang.tinycoder.easyiotkit.bean.Device;
import wang.tinycoder.easyiotkit.bean.GroupDevData;
import wang.tinycoder.easyiotkit.bean.NetResult;

/**
 * Progect：EasyLinkerAppNew
 * Package：wang.tinycoder.easylinkerapp.module.home.fragment.dev
 * Desc：
 * Author：TinycoderWang
 * CreateTime：2018/4/8 20:26
 */
public interface DevManageContract {
    interface View extends IView {

        void refreshComplete(boolean refresh);

        void updateAllDevices();

        void updateBegin(int oldCount);

        void requestError();

        void onDeviceClicked(Device device);

        void onSendClicked(Device device, String command);

        void sendCommandError();
    }

    interface Model extends IModel {
        void requestAllDevices(int page, int count, Observer<NetResult<GroupDevData>> observer);
        void sendCommandToDevice(String deviceId, String command, Observer<NetResult> observer);
    }
}
