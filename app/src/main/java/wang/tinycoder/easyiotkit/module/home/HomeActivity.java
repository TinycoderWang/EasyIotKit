package wang.tinycoder.easyiotkit.module.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
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
    // 当前操作中的设备
    private Device currentDevice;

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
        fragmentFactory = HomeFragmentFactory.getInstance();
        mPresenter.switchContent(R.id.fl_content, null, fragmentFactory.create(DEV_MANAGE), DEV_MANAGE);
        currentTab = DEV_MANAGE;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

    }

    // 底部选择
    @OnCheckedChanged({R.id.rb_dev_manage, R.id.rb_dev_detail, R.id.rb_my_setting})
    public void onRadioButtonClicked(RadioButton radioButton) {
        if (radioButton.isChecked()) {
            switch (radioButton.getId()) {
                case R.id.rb_dev_manage:   // 设备管理
                    mPresenter.switchContent(R.id.fl_content, fragmentFactory.create(currentTab), fragmentFactory.create(DEV_MANAGE), DEV_MANAGE);
                    currentTab = DEV_MANAGE;
                    break;
                case R.id.rb_dev_detail:   // 系统状态
                    mPresenter.switchContent(R.id.fl_content, fragmentFactory.create(currentTab), fragmentFactory.create(DEV_DETAIL), DEV_DETAIL);
                    currentTab = DEV_DETAIL;
                    break;
                case R.id.rb_my_setting:   // 设置
                    mPresenter.switchContent(R.id.fl_content, fragmentFactory.create(currentTab), fragmentFactory.create(MY_SETTING), MY_SETTING);
                    currentTab = MY_SETTING;
                    break;
            }
        }

    }

    @Override
    public void cookieOverTime() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(Constants.EXTRA_CLOSE_OTHER_ACTIVITY, true);
        startActivity(intent);
        finish();
    }


    // 跳转到设备详情
    public void gotoDeviceDetail(Device device) {

        // 保存当前选中设备
        currentDevice = device;

        // 不做设备详情页的独立页面了，简单切换到fragment
        mRbDevDetail.setChecked(true);

//        // 跳转独立的详情页面
//        if (device != null) {
//            Intent intent = new Intent(this, DeviceDetailActivity.class);
//            intent.putExtra(Constants.EXTRA_DEVICE, device);
//            startActivity(intent);
//        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public Device getCurrentDevice() {
        return currentDevice;
    }

    public void setCurrentDevice(Device currentDevice) {
        this.currentDevice = currentDevice;
    }
}
