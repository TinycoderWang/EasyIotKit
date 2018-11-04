package wang.tinycoder.easyiotkit.module.devguide.devbind;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import io.reactivex.functions.Consumer;
import wang.tinycoder.easyiotkit.R;
import wang.tinycoder.easyiotkit.app.Constants;
import wang.tinycoder.easyiotkit.base.BaseActivity;
import wang.tinycoder.easyiotkit.bean.Device;
import wang.tinycoder.qrcoder.core.QRCodeView;
import wang.tinycoder.qrcoder.zxing.ZXingView;

/**
 * Progect：EasyLinkerAppNew
 * Package：wang.tinycoder.easylinkerapp.module.device.devbind
 * Desc：
 * Author：TinycoderWang
 * CreateTime：2018/4/27 20:57
 */
public class DevBindActivity extends BaseActivity<DevBindPresenter> implements DevBindContract.View, QRCodeView.Delegate {


    // 是否拥有摄像头的权限
    private boolean hasCameraPermission = false;

    @BindView(R.id.zxv_scanner)
    ZXingView mZxvScanner;
    private String mGroupId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_dev_bind;
    }

    @Override
    public void initPresenter() {

        // 群组的id
        mGroupId = getIntent().getStringExtra(Constants.EXTRA_GROUP_ID);
        mPresenter = new DevBindPresenter(this, new DevBindModel());
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        mZxvScanner.setDelegate(this);
        // 权限检验
        RxPermissions rxPermissions = new RxPermissions(this);
        // 获取相机权限
        rxPermissions.request(Manifest.permission.CAMERA)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        hasCameraPermission = aBoolean.booleanValue();
                        if (hasCameraPermission) {
                            // 开始扫描二维码
                            mZxvScanner.startSpotDelay(500);
                            mZxvScanner.startCamera();
                            mZxvScanner.showScanRect();
                        } else {
                            showMessage("您拒绝了相机权限，无法进行扫码绑定！");
                            finish();
                        }
                    }
                });
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (hasCameraPermission) {
            mZxvScanner.startSpotDelay(1000);
            mZxvScanner.startCamera();
            mZxvScanner.showScanRect();
        }
    }

    @Override
    protected void onStop() {
        if (hasCameraPermission) {
            mZxvScanner.stopSpot();
            mZxvScanner.stopCamera();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mZxvScanner.onDestroy();
        super.onDestroy();
    }


    @Override
    public void onScanQRCodeSuccess(String result) {
        Logger.i("%s onScanQRCodeSuccess result : ", TAG, result);
        if (!TextUtils.isEmpty(result) && isMatchDevId(result)) {
            // 停止扫码
            mZxvScanner.stopSpot();
            // 绑定设备
            mPresenter.bindDevice(mGroupId, result);
        }

    }

    // 是否符合设备id格式
    private boolean isMatchDevId(String result) {
        Pattern deviceId = Pattern.compile("\\d{13}");
        Matcher m = deviceId.matcher(result);
        return m.matches();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错,请确认您是否开启了相应权限！");
    }

    /**
     * 绑定结果
     *
     * @param result
     * @param device
     */
    @Override
    public void onBindResult(boolean result, Device device) {

        if (Constants.DEBUG) {
            Intent intent = getIntent();
            Device deviceDebug = new Device();
            deviceDebug.setId("1234567891234");
            deviceDebug.setKey("1533608432741/1533635877417/1536378237855");
            intent.putExtra(Constants.EXTRA_DEVICE, deviceDebug);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            if (result) {   // 绑定成功
                Intent intent = getIntent();
                intent.putExtra(Constants.EXTRA_DEVICE, device);
                setResult(RESULT_OK, intent);
                finish();
            } else {   // 绑定失败
                if (hasCameraPermission) {
                    mZxvScanner.startSpotDelay(100);
                    mZxvScanner.startCamera();
//            // 打开前摄像头
//            mZxvScanner.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
                    mZxvScanner.showScanRect();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (Constants.DEBUG) {
            Intent intent = getIntent();
            Device deviceDebug = new Device();
            deviceDebug.setId("1536378237855");
            deviceDebug.setKey("1533608432741/1533635877417/1536378237855");
            intent.putExtra(Constants.EXTRA_DEVICE, deviceDebug);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
            finish();
        }
    }
}
