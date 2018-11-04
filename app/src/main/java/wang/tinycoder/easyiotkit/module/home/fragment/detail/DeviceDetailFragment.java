package wang.tinycoder.easyiotkit.module.home.fragment.detail;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.BindView;
import wang.tinycoder.easyiotkit.R;
import wang.tinycoder.easyiotkit.base.BaseActivity;
import wang.tinycoder.easyiotkit.base.BaseFragment;
import wang.tinycoder.easyiotkit.bean.Device;
import wang.tinycoder.easyiotkit.bean.DeviceData;
import wang.tinycoder.easyiotkit.bean.WeatherBean;
import wang.tinycoder.easyiotkit.module.home.HomeActivity;

/**
 * Progect：EasyIotKit
 * Package：wang.tinycoder.easyiotkit.module.home.fragment.detail
 * Desc：设备详情的页面
 * Author：TinycoderWang
 * CreateTime：2018/4/22 10:55
 */
public class DeviceDetailFragment extends BaseFragment<DeviceDetailPresenter> implements DeviceDetailContract.View {


    @BindView(R.id.tv_temperature)
    TextView mTvTemperature;
    @BindView(R.id.tv_humidity)
    TextView mTvHumidity;
    @BindView(R.id.imageView)
    ImageView mImageView;
    @BindView(R.id.tv_weather)
    TextView mTvWeather;
    @BindView(R.id.tv_wind)
    TextView mTvWind;
    @BindView(R.id.st_led1)
    Switch mStLed1;
    @BindView(R.id.st_led2)
    Switch mStLed2;
    @BindView(R.id.st_led3)
    Switch mStLed3;
    @BindView(R.id.st_led4)
    Switch mStLed4;
    @BindView(R.id.st_led5)
    Switch mStLed5;
    @BindView(R.id.st_led6)
    Switch mStLed6;

    // 当前设备
    private Device mDevice;
    // 定位
    private AMapLocationClient mlocationClient = null;
    private CompoundButton.OnCheckedChangeListener switchListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int id = buttonView.getId();
            int switchIndex = 0;
            switch (id) {
                case R.id.st_led1:
                    switchIndex = 1;
                    break;
                case R.id.st_led2:
                    switchIndex = 2;
                    break;
                case R.id.st_led3:
                    switchIndex = 3;
                    break;
                case R.id.st_led4:
                    switchIndex = 4;
                    break;
                case R.id.st_led5:
                    switchIndex = 5;
                    break;
                case R.id.st_led6:
                    switchIndex = 6;
                    break;
            }

            showToast("index:" + switchIndex + " , isCheck:" + isChecked);

            if (mDevice == null) {
                mDevice = ((HomeActivity) mActivity).getCurrentDevice();
            }
            if (mDevice != null) {
                mPresenter.sendCommandToDevice(mDevice.getId(), "{cmd:1}");
            }
        }
    };


    @Override
    protected void onVisible() {
        super.onVisible();
        mDevice = ((HomeActivity) mActivity).getCurrentDevice();
    }

    @Override
    protected int getlayoutId() {
        return R.layout.fragment_dev_detail;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new DeviceDetailPresenter(this, new DeviceDetailModel());
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        // 获取位置
        initLocation();
        // 初始化监听
        initListener();
    }

    private void initListener() {
        mStLed1.setOnCheckedChangeListener(switchListener);
        mStLed2.setOnCheckedChangeListener(switchListener);
        mStLed3.setOnCheckedChangeListener(switchListener);
        mStLed4.setOnCheckedChangeListener(switchListener);
        mStLed5.setOnCheckedChangeListener(switchListener);
        mStLed6.setOnCheckedChangeListener(switchListener);
    }

    private void initLocation() {
        //声明mLocationOption对象
        AMapLocationClientOption mLocationOption = null;
        mlocationClient = new AMapLocationClient(mActivity);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置返回地址信息，默认为true
        mLocationOption.setNeedAddress(true);
        //设置定位监听
        mlocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation amapLocation) {
                if (amapLocation != null) {
                    if (amapLocation.getErrorCode() == 0) {
//                        //定位成功回调信息，设置相关消息
//                        amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
//                        amapLocation.getLatitude();//获取纬度
//                        amapLocation.getLongitude();//获取经度
//                        amapLocation.getAccuracy();//获取精度信息
//                        amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
//                        amapLocation.getCountry();//国家信息
//                        amapLocation.getProvince();//省信息
//                        amapLocation.getCity();//城市信息
//                        amapLocation.getDistrict();//城区信息
//                        amapLocation.getStreet();//街道信息
//                        amapLocation.getStreetNum();//街道门牌号信息
//                        amapLocation.getCityCode();//城市编码
//                        amapLocation.getAoiName();//获取当前定位点的AOI信息
                        String adCode = amapLocation.getAdCode();//地区编码
                        // 获取天气信息
                        mPresenter.requestWeather(adCode);
                        // 停止定位
                        mlocationClient.stopAssistantLocation();
                        mlocationClient.stopLocation();
                    } else {
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        Logger.e("%s location Error, ErrCode:%d   , errInfo:%s", amapLocation.getErrorCode(), amapLocation.getErrorInfo());
                    }
                }
            }
        });
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(20000);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();
    }


    @Override
    protected void loadData(Bundle savedInstanceState) {
        lazyLoadData();
    }

    /**
     * 加载数据
     */
    @Override
    protected void lazyLoadData() {
        if (mDevice != null) {
            mPresenter.requestDeviceData(mDevice.getId());
        }
    }


    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(String message) {
        ((BaseActivity) mActivity).showToast(message);
    }


    // 更新数据
    @Override
    public void update() {
        List<DeviceData> currentData = mPresenter.getCurrentData();
        if (currentData != null && currentData.size() > 0) {
            // 当前状态
            showCurentState(currentData.get(0));
        }
    }

    @Override
    public void showWeather(WeatherBean weatherBean) {
        if (weatherBean != null && "1".equals(weatherBean.getStatus())) {
            List<WeatherBean.LivesBean> lives = weatherBean.getLives();
            if (lives != null && lives.size() > 0) {
                WeatherBean.LivesBean livesBean = lives.get(0);
                // 温度
                mTvTemperature.setText(String.format("%s°", livesBean.getTemperature()));
                // 天气
                String weather = livesBean.getWeather();
                mTvWeather.setText(weather);
                mImageView.setImageResource(R.drawable.weather_qing);
                if (!TextUtils.isEmpty(weather)) {
                    if (weather.contains("阴")) {
                        mImageView.setImageResource(R.drawable.weather_yin);
                    } else if (weather.contains("云")) {
                        mImageView.setImageResource(R.drawable.weather_duoyun);
                    } else if (weather.contains("雨")) {
                        mImageView.setImageResource(R.drawable.weather_yu);
                    }
                }
                // 风力
                mTvWind.setText(String.format("%s风 : %s级", livesBean.getWinddirection(), livesBean.getWindpower()));
                // 湿度
                mTvHumidity.setText(String.format("湿度 : %s%%", livesBean.getHumidity()));
            }

        } else {
            showToast("获取天气失败！");
        }
    }


    /**
     * 显示当前的状态
     *
     * @param deviceData
     */
    private void showCurentState(DeviceData deviceData) {

    }

    /**
     * 获取设备数据
     *
     * @param deviceData 数据
     * @param index      要获取的值的索引
     * @return
     */
    private float getValue(DeviceData deviceData, int index) {
        try {
            if (deviceData != null) {
                String data = deviceData.getData().getData();
                if (!TextUtils.isEmpty(data)) {
                    return Float.valueOf(data.split("-")[index]);
                }
            }
        } catch (Exception e) {
            return 0;
        }

        return 0;
    }

    @Override
    public void onDestroy() {
        if (mlocationClient != null) {
            mlocationClient.stopAssistantLocation();
            mlocationClient.onDestroy();
        }
        super.onDestroy();
    }

}


