package wang.tinycoder.easyiotkit.module.home;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import io.reactivex.functions.Consumer;
import wang.tinycoder.easyiotkit.R;
import wang.tinycoder.easyiotkit.app.Constants;
import wang.tinycoder.easyiotkit.base.BaseActivity;
import wang.tinycoder.easyiotkit.bean.Device;
import wang.tinycoder.easyiotkit.module.home.fragment.HomeFragmentFactory;
import wang.tinycoder.easyiotkit.module.login.LoginActivity;

/**
 * @author WangYh
 * @version V1.0
 * @Name: HomeActivity
 * @Package wang.tinycoder.easylinkerapp.module.home
 * @Description: 主页
 * @date 2018/4/4 0004
 */
public class HomeActivity extends BaseActivity<HomePresenter> implements HomeContract.View {

    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.fl_content)
    FrameLayout mFlContent;
    @BindView(R.id.rg_tab)
    RadioGroup mRgTab;
    @BindView(R.id.rb_dev_manage)
    RadioButton mRbDevManage;
    @BindView(R.id.rb_dev_detail)
    RadioButton mRbDevDetail;
    @BindView(R.id.rb_my_setting)
    RadioButton mRbMySetting;


    // 设备管理
    private final int DEV_MANAGE = 0;
    // 设备详情
    private final int DEV_DETAIL = 1;
    // 个人设置
    private final int MY_SETTING = 2;

    // 当前的类型
    private int currentTab = DEV_MANAGE;


    private HomeFragmentFactory fragmentFactory;
    private boolean isFirst = true;

    // 当前选中的设备
    private Device mCurrentDevice;

    @Override
    public int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    public void initPresenter() {
        mPresenter = new HomePresenter(this, new HomeModel());
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {

        // 申请定位权限
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.requestEach(
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.READ_PHONE_STATE)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            Logger.i("%s 允许定位权限！", TAG);
                        } else {
                            Logger.i("%s 不允许定位权限！", TAG);
                            showMessage("由于你拒绝了某些权限，系统状态部分功能将无法正常使用！");
                        }
                    }
                });

        fragmentFactory = HomeFragmentFactory.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFirst) {
            mPresenter.switchContent(R.id.fl_content, null, fragmentFactory.create(DEV_MANAGE), DEV_MANAGE);
            currentTab = DEV_MANAGE;
            isFirst = false;
        }
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void showLoading() {
        super.showLoading();
    }

    @Override
    public void hideLoading() {
        super.hideLoading();
    }

    @Override
    public void showMessage(String message) {
        showToast(message);
    }


    // 底部选择
    @OnCheckedChanged({R.id.rb_dev_manage, R.id.rb_dev_detail, R.id.rb_my_setting})
    public void onRadioButtonClicked(RadioButton radioButton) {

        if (radioButton.isChecked()) {
            switch (radioButton.getId()) {
                case R.id.rb_dev_manage:   // 设备管理
                    mTvTitle.setText("设备管理");
                    mPresenter.switchContent(R.id.fl_content, fragmentFactory.create(currentTab), fragmentFactory.create(DEV_MANAGE), DEV_MANAGE);
                    currentTab = DEV_MANAGE;
                    break;
                case R.id.rb_dev_detail:   // 系统状态
                    mTvTitle.setText("设备详情");
                    mPresenter.switchContent(R.id.fl_content, fragmentFactory.create(currentTab), fragmentFactory.create(DEV_DETAIL), DEV_DETAIL);
                    currentTab = DEV_DETAIL;
                    break;
                case R.id.rb_my_setting:   // 设置
                    mTvTitle.setText("个人中心");
                    mPresenter.switchContent(R.id.fl_content, fragmentFactory.create(currentTab), fragmentFactory.create(MY_SETTING), MY_SETTING);
                    currentTab = MY_SETTING;
                    break;
            }
        }

    }


    /**
     * 显示设备的详情
     *
     * @param device 设备
     */
    public void showDetail(Device device) {
        mTvTitle.setText("系统状态");
        mPresenter.switchContent(R.id.fl_content, fragmentFactory.create(currentTab), fragmentFactory.create(DEV_DETAIL), DEV_DETAIL);
        currentTab = DEV_DETAIL;
        //((DeviceDetailFragment)fragmentFactory.create(DEV_DETAIL))
    }

    @Override
    public void cookieOverTime() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(Constants.EXTRA_CLOSE_OTHER_ACTIVITY, true);
        startActivity(intent);
        finish();
    }


    public Device getCurrentDevice() {
        return mCurrentDevice;
    }

    public void setCurrentDevice(Device currentDevice) {
        mCurrentDevice = currentDevice;
    }

    // 跳转到设备详情
    public void gotoDeviceDetail(Device device) {
        if (device != null) {
            mCurrentDevice = device;
            mTvTitle.setText("设备详情");
            mPresenter.switchContent(R.id.fl_content, fragmentFactory.create(currentTab), fragmentFactory.create(DEV_DETAIL), DEV_DETAIL);
            currentTab = DEV_DETAIL;
            mRbDevDetail.setChecked(true);
        }
    }
}
