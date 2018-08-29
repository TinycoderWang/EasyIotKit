package wang.tinycoder.easyiotkit.module.home.fragment.dev;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import wang.tinycoder.easyiotkit.base.BasePresenter;
import wang.tinycoder.easyiotkit.bean.Device;
import wang.tinycoder.easyiotkit.bean.GroupDevData;
import wang.tinycoder.easyiotkit.bean.NetResult;
import wang.tinycoder.easyiotkit.bean.event.DeviceEvent;

/**
 * Progect：EasyLinkerAppNew
 * Package：wang.tinycoder.easylinkerapp.module.home.fragment.dev
 * Desc：
 * Author：TinycoderWang
 * CreateTime：2018/4/8 20:26
 */
public class DevManagePresenter extends BasePresenter<DevManageContract.View, DevManageContract.Model> {
    private static final int PAGE_COUNT = 10;
    private List<Device> mDeviceList;
    private int mPosition = -1;
    private int currentPage;
    public int allPage;
    private boolean isLast;

    @Override
    public void onStart() {
        registerRxBus(DeviceEvent.class, new Consumer<DeviceEvent>() {

            @Override
            public void accept(DeviceEvent deviceEvent) throws Exception {
                if (deviceEvent != null) {
                    mPosition = deviceEvent.getPosition();
                    Device device = deviceEvent.getData();
                    switch (deviceEvent.getType()) {
                        case DeviceEvent.DEVICE_CLICK_TYPE:   // 点击条目
                            mView.onDeviceClicked(device);
                            break;
                        case DeviceEvent.SEND_CLICK_TYPE:   // 点击发送
                            mView.onSendClicked(device, deviceEvent.getCommand());
                            break;
                    }
                }
            }
        });
    }

    public DevManagePresenter(DevManageContract.View rootView, DevManageContract.Model model) {
        super(rootView, model);
        mDeviceList = new ArrayList<>();
    }


    /**
     * 获取所有设备
     */
    public void requestAllDevice(final boolean refresh) {

        if (refresh) {
            currentPage = 0;
        }

        if (!refresh && isLast) {   // 没有更多数据了
            mView.refreshComplete(refresh);
            return;
        }

        // 请求数据
        mModel.requestAllDevices(currentPage, PAGE_COUNT, new Observer<NetResult<GroupDevData>>() {

            @Override
            public void onSubscribe(Disposable d) {
                mCompositeDisposable.add(d);
            }

            @Override
            public void onNext(NetResult<GroupDevData> groupDevDataNetResult) {
                if (groupDevDataNetResult != null && NetResult.SUCCESS == groupDevDataNetResult.getState()) {
                    // 如果为刷新就清空
                    if (refresh) {
                        mDeviceList.clear();
                    }
                    GroupDevData data = groupDevDataNetResult.getData();
                    if (data != null) {
                        List<Device> deviceData = data.getData();
                        // 总页码
                        allPage = data.getTotalPages();
                        // 是否为最后
                        if (!(isLast = data.isIsLast())) {
                            currentPage++;
                        }
                        // 请求回来的设备列表
                        if (deviceData != null && deviceData.size() > 0) {
                            int oldCount = mDeviceList.size();
                            mDeviceList.addAll(deviceData);
                            if (refresh) {
                                mView.updateAllDevices();
                            } else {
                                mView.updateBegin(oldCount);
                            }
                        } else {
                            mView.updateAllDevices();
                        }
                    }
                } else {
                    mView.requestError();
                }
            }

            @Override
            public void onError(Throwable e) {
                Logger.e("%s requestDeviceBygroup error ! MSG : %s", TAG, e.getMessage());
                mView.requestError();
                mView.refreshComplete(refresh);
            }

            @Override
            public void onComplete() {
                Logger.i("%s requestDeviceBygroup onComplete !", TAG);
                mView.refreshComplete(refresh);
            }
        });


    }


    /**
     * 向设备发送指令
     *
     * @param deviceId 设备id
     * @param command  指令
     */
    public void sendCommandToDevice(String deviceId, String command) {

        // 处理command
        if (command.startsWith("{") && command.endsWith("}")) {   // 用户自己输入的简单json内容（不支持嵌套）
            try {
                command = command.substring(1, command.length() - 1);
                StringBuilder sb = new StringBuilder();
                String[] commands = command.split(",");
                if (commands != null) {
                    for (String item : commands) {
                        String[] innerItem = item.split(":");
                        if (innerItem != null) {
                            sb.append("\"")
                                    .append(innerItem[0].trim())
                                    .append("\":")
                                    .append("\"")
                                    .append(innerItem[1].trim())
                                    .append("\"")
                                    .append(",");
                        }
                    }
                    if (sb.length() > 0) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    command = sb.toString();
                }
                command = "{" + command + "}";

            } catch (Exception e) {
                e.printStackTrace();
                mView.showMessage("指令格式错误！");
                return;
            }
        } else {   // 固定的cmd
            StringBuilder sb = new StringBuilder("{\"cmd\":\"");
            sb.append(command)
                    .append("\"}");
            command = sb.toString();
        }


        mModel.sendCommandToDevice(deviceId, command, new Observer<NetResult>() {
            @Override
            public void onSubscribe(Disposable d) {
                mCompositeDisposable.add(d);
            }

            @Override
            public void onNext(NetResult netResult) {
                if (netResult != null) {

                    if (NetResult.SUCCESS == netResult.getState()) {

                    }
                    mView.showMessage(netResult.getMessage());
                } else {
                    mView.sendCommandError();
                }
            }

            @Override
            public void onError(Throwable e) {
                Logger.e("%s requestDeviceBygroup error ! MSG : %s", TAG, e.getMessage());
                mView.requestError();
            }

            @Override
            public void onComplete() {
                Logger.i("%s requestDeviceBygroup onComplete !", TAG);
            }
        });
    }


    public List<Device> getDeviceList() {
        return mDeviceList;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

}
