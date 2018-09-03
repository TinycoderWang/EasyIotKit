package wang.tinycoder.easyiotkit.module.devguide.smartconfig;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuhong.xsmartconfiglib.api.xEspTouchTask;

import butterknife.BindView;
import butterknife.OnClick;
import wang.tinycoder.easyiotkit.R;
import wang.tinycoder.easyiotkit.base.BaseActivity;
import wang.tinycoder.easyiotkit.util.WifiUtils;
import wang.tinycoder.easyiotkit.widget.PasswordEditText;

/**
 * Progect：EasyIotKit
 * Package：wang.tinycoder.easyiotkit.module.devguide.smartconfig
 * Desc：一键配网页面
 * Author：TinycoderWang
 * CreateTime：2018/9/2 9:04
 */
public class SmartConfigActivity extends BaseActivity<SmartConfigPresenter> implements SmartConfigContract.View {

    private static final int WIFI_SET_REQUEST_CODE = 9000;
    @BindView(R.id.iv_back_ground)
    ImageView mIvBackGround;
    @BindView(R.id.tv_back)
    TextView mTvBack;
    @BindView(R.id.tv_wifi_ssid)
    TextView mTvWifiSsid;
    @BindView(R.id.et_wifi_pwd)
    PasswordEditText mEtWifiPwd;
    @BindView(R.id.tv_start_config)
    TextView mTvStartConfig;

    private WifiUtils mWifiUtils;
    private xEspTouchTask espTouchTask;
    private ProgressDialog dialog;

    @Override
    public int getLayoutId() {
        return R.layout.activity_smart_config;
    }

    @Override
    public void initPresenter() {
        mPresenter = new SmartConfigPresenter(this, new SmartConfigModel());
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        // 获取当前连接的WIFI
        mWifiUtils = new WifiUtils(this);
        genWifiSsid();

    }

    /**
     * 填写wifi的ssid
     */
    private void genWifiSsid() {
        String wifiConnectedSsid = mWifiUtils.getWifiConnectedSsid();
        if (!TextUtils.isEmpty(wifiConnectedSsid)) {
            mTvWifiSsid.setText(wifiConnectedSsid);
        }
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

    }

    @OnClick({R.id.iv_back_ground, R.id.tv_start_config, R.id.tv_wifi_ssid})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back_ground:   // 返回
                onBackPressed();
                break;
            case R.id.tv_start_config:   // 配置网络
                // 配网不可点击
                mTvStartConfig.setClickable(false);
                startConfig();
                break;
            case R.id.tv_wifi_ssid:   // wifi设置页面
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivityForResult(intent, WIFI_SET_REQUEST_CODE);
                break;
        }
    }

    /**
     * 开始配网
     */
    private void startConfig() {
        // 判断是否连接wifi
        if (mWifiUtils.isWifiConnected()) {
            // 获取wifi的ssid
            String ssid = mTvWifiSsid.getText().toString();
            // 获取wifi密码
            String pwd = mEtWifiPwd.getText().toString();
            if (TextUtils.isEmpty(pwd)) {
                showMessage("请输入当前WIFI密码");
                mTvStartConfig.setClickable(true);
                return;
            }
            espTouchTask = new xEspTouchTask.Builder(this)
                    .setSsid(ssid)
                    .setPassWord(pwd)
                    .creat();
            startSmartConfig();
            dialog = new ProgressDialog(SmartConfigActivity.this);
            dialog.setMessage("努力配网中...");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialog.dismiss();
                    espTouchTask.stopSmartConfig();
                }
            });
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.GONE);
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.GONE);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    mTvStartConfig.setClickable(true);
                }
            });

        } else {
            showMessage("请首先连接WIFI!");
            mTvStartConfig.setClickable(true);
        }

    }

    //配网后还带UDP的方法使用示范代码
    private void startConfigWithUDP() {
        espTouchTask.startSmartConfig(30, 8989);
        espTouchTask.setEspTouchTaskListener(new xEspTouchTask.EspTouchTaskListener() {
            @Override
            public void EspTouchTaskCallback(int code, String message) {
                Log.e("==w", "code:" + code + ",message:" + message);
                switch (code) {
                    case 0:
                        dialog.setMessage("配网成功，设备正在连接服务器...");
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.GONE);
                        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        dialog.setMessage("设备连接服务器成功...");
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
                        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.GONE);
                        Log.e("==w", "UDP广播后获取到的信息：" + message);
                        break;
                    case 2:
                    case 4:
                        dialog.setMessage("配网失败...");
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
                        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.GONE);
                        break;
                }
            }
        });

    }

    //配网后不带UDP的使用示范
    private void startSmartConfig() {
        espTouchTask.startSmartConfig();
        espTouchTask.setEspTouchTaskListener(new xEspTouchTask.EspTouchTaskListener() {
            @Override
            public void EspTouchTaskCallback(int code, String message) {
                Log.e("==w", "code:" + code + ",message:" + message);
                switch (code) {
                    case 0:
                        dialog.setMessage("恭喜，配网成功!");
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.GONE);
                        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        dialog.setMessage("配网失败...");
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
                        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.GONE);
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (WIFI_SET_REQUEST_CODE == requestCode) {
            genWifiSsid();
        }
    }
}
