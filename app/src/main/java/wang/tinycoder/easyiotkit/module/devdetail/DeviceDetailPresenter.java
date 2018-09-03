package wang.tinycoder.easyiotkit.module.devdetail;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import wang.tinycoder.easyiotkit.base.BasePresenter;
import wang.tinycoder.easyiotkit.bean.DeviceData;
import wang.tinycoder.easyiotkit.bean.NetResult;

/**
 * Progect：EasyIotKit
 * Package：wang.tinycoder.easyiotkit.module.home.fragment.detail
 * Desc：
 * Author：TinycoderWang
 * CreateTime：2018/8/24 20:37
 */
public class DeviceDetailPresenter extends BasePresenter<DeviceDetailContract.View, DeviceDetailContract.Model> {

    private List<DeviceData> currentData;
    private int currentPage;
    private final int PAGECOUNT = 20;


    public DeviceDetailPresenter(DeviceDetailContract.View rootView, DeviceDetailContract.Model model) {
        super(rootView, model);
        currentData = new ArrayList<>();
    }

    // 请求数据
    public void requestDeviceData(String deviceId) {
        mView.showLoading();
        mModel.requestDeviceData(currentPage, PAGECOUNT, deviceId, new Observer<NetResult<List<DeviceData>>>() {
            @Override
            public void onSubscribe(Disposable d) {
                mCompositeDisposable.add(d);
            }

            @Override
            public void onNext(NetResult<List<DeviceData>> result) {
                if (result != null) {
                    if (NetResult.SUCCESS == result.getState()) {   // 请求成功
                        List<DeviceData> data = result.getData();
                        if (data != null && data.size() > 0) {
                            // 防止没请求到数据，界面空白（放里面清空）
                            currentData.clear();
                            currentData.addAll(data);
                        }
                    } else {
                        String message = result.getMessage();
                        mView.showMessage(TextUtils.isEmpty(message) ? "获取数据失败！" : message);
                    }
                } else {
                    mView.showMessage("获取数据失败！");
                }
            }

            @Override
            public void onError(Throwable e) {
                mView.showMessage("获取数据发生异常！");
                mView.hideLoading();
            }

            @Override
            public void onComplete() {
                mView.update();
                mView.hideLoading();
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
                    mView.showMessage("命令失败！");
                }
            }

            @Override
            public void onError(Throwable e) {
                Logger.e("%s requestDeviceBygroup error ! MSG : %s", TAG, e.getMessage());
                mView.showMessage("命令失败！");
            }

            @Override
            public void onComplete() {
                Logger.i("%s requestDeviceBygroup onComplete !", TAG);
            }
        });
    }


    public List<DeviceData> getCurrentData() {
        return currentData;
    }
}
