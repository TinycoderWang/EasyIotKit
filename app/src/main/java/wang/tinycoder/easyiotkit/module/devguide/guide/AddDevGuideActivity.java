package wang.tinycoder.easyiotkit.module.devguide.guide;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import wang.tinycoder.easyiotkit.R;
import wang.tinycoder.easyiotkit.app.Constants;
import wang.tinycoder.easyiotkit.base.BaseActivity;
import wang.tinycoder.easyiotkit.bean.Device;
import wang.tinycoder.easyiotkit.module.devguide.devbind.DevBindActivity;
import wang.tinycoder.easyiotkit.module.devguide.smartconfig.SmartConfigActivity;

/**
 * Progect：EasyIotKit
 * Package：wang.tinycoder.easyiotkit.module.devguide.guide
 * Desc：
 * Author：TinycoderWang
 * CreateTime：2018/9/5 20:33
 */
public class AddDevGuideActivity extends BaseActivity<AddDevGuidePresenter> implements AddDevGuideContract.View {


    @BindView(R.id.tv_back)
    TextView mTvBack;
    @BindView(R.id.tv_notify)
    TextView mTvNotify;
    @BindView(R.id.tv_sure)
    TextView mTvSure;
    private Device mDevice;

    private final int BIND = 0;
    private final int CONFIG = 1;
    private final int FINISH = 2;
    private int stype = BIND;

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_dev_guide;
    }

    @Override
    public void initPresenter() {
        mPresenter = new AddDevGuidePresenter(this, new AddDevGuideModel());
        Intent intent = getIntent();
        mDevice = (Device) intent.getSerializableExtra(Constants.EXTRA_DEVICE);
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        if (mDevice != null) {   // 已经绑定，直接配网
            stype = CONFIG;
        } else {
            stype = BIND;
        }
        changeShow(stype);
    }

    private void changeShow(int stype) {
        if (CONFIG == stype) {   // 已经绑定，直接配网
            mTvNotify.setText(getResources().getString(R.string.add_dev_guide_second));
            mTvSure.setText(getResources().getString(R.string.add_dev_guide_config));
        } else if (BIND == stype) {
            mTvNotify.setText(getResources().getString(R.string.add_dev_guide_first));
            mTvSure.setText(getResources().getString(R.string.add_dev_guide_bind));
        } else {
            mTvNotify.setText(getResources().getString(R.string.add_dev_guide_third));
            mTvSure.setText(getResources().getString(R.string.add_dev_guide_finish));
        }
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

    }


    @OnClick({R.id.tv_back, R.id.tv_sure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_back:   // 返回
                onBackPressed();
                break;
            case R.id.tv_sure:   // 确定
                Intent intent = null;
                if (BIND == stype) {
                    intent = new Intent(this, DevBindActivity.class);
                    startActivityForResult(intent, stype);
                } else if (CONFIG == stype) {
                    intent = new Intent(this, SmartConfigActivity.class);
                    String key = "";
                    if(mDevice!=null){
                        key = mDevice.getKey();
                    }
                    intent.putExtra(Constants.EXTRA_DEVICE_KEY,key);
                    startActivityForResult(intent, stype);
                } else {
                    finish();
                }
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (BIND == requestCode) {
            if (RESULT_OK == resultCode) {
                if (data != null) {
                    mDevice = (Device) data.getSerializableExtra(Constants.EXTRA_DEVICE);
                    if (mDevice != null) {
                        stype = CONFIG;
                        // 开始smartconfig
                        Intent configIntent = new Intent(this,SmartConfigActivity.class);
                        configIntent.putExtra(Constants.EXTRA_DEVICE_KEY,mDevice.getKey());
                        startActivityForResult(configIntent,CONFIG);
                    }
                }
            } else {
                showToast("绑定失败！");
            }
        } else if (CONFIG == requestCode) {
            if (RESULT_OK == resultCode) {
                stype = FINISH;
            } else {
                showToast("配网失败！");
            }
        }

        changeShow(stype);
    }
}
