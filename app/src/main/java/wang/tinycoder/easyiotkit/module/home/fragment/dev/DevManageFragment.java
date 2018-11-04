package wang.tinycoder.easyiotkit.module.home.fragment.dev;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import wang.tinycoder.easyiotkit.R;
import wang.tinycoder.easyiotkit.base.BaseActivity;
import wang.tinycoder.easyiotkit.base.BaseFragment;
import wang.tinycoder.easyiotkit.bean.Banner;
import wang.tinycoder.easyiotkit.bean.Device;
import wang.tinycoder.easyiotkit.module.devguide.guide.AddDevGuideActivity;
import wang.tinycoder.easyiotkit.module.home.HomeActivity;
import wang.tinycoder.easyiotkit.util.DensityUtils;
import wang.tinycoder.easyiotkit.widget.recyclerbanner.RecyclerBanner;
import wang.tinycoder.easyiotkit.widget.recyclerview.decoration.HorizontalDividerItemDecoration;

import static android.app.Activity.RESULT_OK;

/**
 * Progect：EasyLinkerAppNew
 * Package：wang.tinycoder.easylinkerapp.module.home.fragment
 * Desc：
 * Author：TinycoderWang
 * CreateTime：2018/4/8 20:20
 */
public class DevManageFragment extends BaseFragment<DevManagePresenter> implements DevManageContract.View {

    private static final int BIND_DEV_REQUEST_CODE = 666;
    @BindView(R.id.rcb_banner)
    RecyclerBanner mRcbBanner;
    @BindView(R.id.cl_content)
    CoordinatorLayout mClContent;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.rcv_dev_list)
    RecyclerView mRcvDevList;
    @BindView(R.id.fab_add_device)
    FloatingActionButton mFlbAddDev;
    @BindView(R.id.tv_message)
    TextView mTvMessage;
    @BindView(R.id.rl_no_dev)
    RelativeLayout mRlNoDev;

    // 设备适配器
    private LinearLayoutManager mLayoutManager;

    private DeviceAdapter mDevAdapter;

    // 下拉刷新
    private OnRefreshListener refreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh(RefreshLayout refreshLayout) {
            mPresenter.requestAllDevice(true);
        }
    };

    // 上拉加载
    private OnLoadMoreListener loadmoreListener = new OnLoadMoreListener() {
        @Override
        public void onLoadMore(RefreshLayout refreshLayout) {
            mPresenter.requestAllDevice(false);
        }
    };


    @Override
    protected int getlayoutId() {
        return R.layout.fragment_dev_manage;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new DevManagePresenter(this, new DevManageModel());
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        mRcvDevList.setLayoutManager(mLayoutManager);
        HorizontalDividerItemDecoration divider = new HorizontalDividerItemDecoration.Builder(mActivity)
                .color(ContextCompat.getColor(mActivity, R.color.gray))
                .size(DensityUtils.dip2px(mActivity, 2))
                .build();
        mRcvDevList.addItemDecoration(divider);
        mRefreshLayout.setOnRefreshListener(refreshListener);
        mRefreshLayout.setOnLoadMoreListener(loadmoreListener);

        // 测试用，写死
        List<Banner> data = new ArrayList<>();
        Banner banner1 = new Banner();
        banner1.setImg("https://github.com/TinycoderWang/EasyIotKit/raw/master/art/1.jpeg");
        banner1.setTitle("标题1");
        data.add(banner1);
        Banner banner2 = new Banner();
        banner2.setImg("http://octklz398.bkt.clouddn.com/banner2.jpg");
        banner2.setTitle("标题2");
        data.add(banner2);
        Banner banner3 = new Banner();
        banner3.setImg("http://octklz398.bkt.clouddn.com/banner3.jpg");
        banner3.setTitle("标题3");
        data.add(banner3);
        Banner banner4 = new Banner();
        banner4.setImg("http://octklz398.bkt.clouddn.com/banner4.jpg");
        banner4.setTitle("标题4");
        data.add(banner4);
        Banner banner5 = new Banner();
        banner5.setImg("http://octklz398.bkt.clouddn.com/banner5.jpg");
        banner5.setTitle("标题5");
        data.add(banner5);
        mRcbBanner.setAdapter(new BannerAdapter(mActivity, data));

    }

    @Override
    protected void loadData(Bundle savedInstanceState) {
        mPresenter.requestAllDevice(true);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(String message) {
        showToast(message);
    }

    @Override
    public void refreshComplete(boolean refresh) {
        if (mRefreshLayout != null) {
            if (refresh) {
                mRefreshLayout.finishRefresh();
            } else {
                mRefreshLayout.finishLoadMore();
            }
        }
    }

    // 更新设备数据
    @Override
    public void updateAllDevices() {
        List<Device> allDevices = mPresenter.getDeviceList();
        if (allDevices != null && allDevices.size() > 0) {

            ((HomeActivity) mActivity).setCurrentDevice(allDevices.get(0));

            if (mDevAdapter == null) {
                // 初始化适配器
                mDevAdapter = new DeviceAdapter(allDevices);
                mRcvDevList.setAdapter(mDevAdapter);
            } else {
                mDevAdapter.notifyDataSetChanged();
            }
            isNoneDevice(false);
        } else {
            isNoneDevice(true);
        }
    }


    @Override
    public void updateBegin(int oldCount) {
        Logger.i("%s %d %d", TAG, mPresenter.getDeviceList().size(), oldCount);
        List<Device> allDevices = mPresenter.getDeviceList();
        if (allDevices != null && allDevices.size() > 0) {
            if (mDevAdapter == null) {
                mDevAdapter = new DeviceAdapter(allDevices);
                mRcvDevList.setAdapter(mDevAdapter);
            } else {
                mDevAdapter.notifyItemRangeInserted(oldCount, allDevices.size());
                mDevAdapter.notifyItemRangeChanged(oldCount, allDevices.size());
            }
            isNoneDevice(false);
        } else {
            isNoneDevice(true);
        }
    }


    @Override
    public void sendCommandError() {
        showToast("指令发送失败！");
    }

    @Override
    public void requestError() {
        mRlNoDev.setVisibility(View.GONE);
        mRcvDevList.setVisibility(View.GONE);
        mTvMessage.setText("网络错误\n点击刷新");
        mTvMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDeviceClicked(Device device) {
        // 去设备详情
        ((HomeActivity) mActivity).gotoDeviceDetail(device);

    }

    @Override
    public void onSendClicked(Device device, String command) {
        if (device != null) {
            if (TextUtils.isEmpty(command)) {
                showToast("请输入要发送的指令");
            } else {
                mPresenter.sendCommandToDevice(device.getId(), command);
                // 隐藏软键盘
                ((BaseActivity) mActivity).hideInput();
            }
        }
    }


    // 添加群组
    @OnClick({R.id.tv_message, R.id.iv_scan, R.id.fab_add_device})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_message:   // 刷新
                mPresenter.requestAllDevice(true);
                break;
            case R.id.iv_scan:   // 添加设备
                addNewDevice();
                break;
            case R.id.fab_add_device:
                addNewDevice();
                break;
        }

    }

    private void addNewDevice() {
        // 跳转到向导页面
        Intent intent = new Intent(mActivity, AddDevGuideActivity.class);
        startActivityForResult(intent, BIND_DEV_REQUEST_CODE);
    }


    /**
     * 显示或隐藏信息
     *
     * @param is 显示或隐藏
     */
    private void isNoneDevice(boolean is) {
        if (is) {
            mRlNoDev.setVisibility(View.VISIBLE);
            mRcvDevList.setVisibility(View.GONE);
            mTvMessage.setVisibility(View.GONE);
        } else {
            mRlNoDev.setVisibility(View.GONE);
            mRcvDevList.setVisibility(View.VISIBLE);
            mTvMessage.setVisibility(View.GONE);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mDevAdapter = null;
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        mRcbBanner.setPlaying(true);
    }


    @Override
    protected void onInvisible() {
        super.onInvisible();
        mRcbBanner.setPlaying(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (BIND_DEV_REQUEST_CODE == requestCode) {   // 绑定设备
            if (RESULT_OK == resultCode) {   // 绑定成功
                // 刷新设备列表
                mPresenter.requestAllDevice(true);
            }
        }
    }
}
