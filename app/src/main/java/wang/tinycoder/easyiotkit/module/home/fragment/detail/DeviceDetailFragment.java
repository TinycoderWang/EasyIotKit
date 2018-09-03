package wang.tinycoder.easyiotkit.module.home.fragment.detail;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.Unbinder;
import wang.tinycoder.easyiotkit.R;
import wang.tinycoder.easyiotkit.base.BaseActivity;
import wang.tinycoder.easyiotkit.base.BaseFragment;
import wang.tinycoder.easyiotkit.bean.Device;
import wang.tinycoder.easyiotkit.bean.DeviceData;
import wang.tinycoder.easyiotkit.util.SpannableStringUtils;

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
    @BindView(R.id.chart)
    LineChart mChart;
    Unbinder unbinder;
    private Device mDevice;
    private Legend legend;
    private XAxis xAxis;
    private YAxis leftYAxis;
    private YAxis rightYaxis;

    @Override
    protected int getlayoutId() {
        return R.layout.activity_dev_detail;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new DeviceDetailPresenter(this, new DeviceDetailModel());
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        // 初始化chart
        initLineChart();
    }

    private void initLineChart() {

        /***图表设置***/
        //是否展示网格线
        mChart.setDrawGridBackground(false);
        //是否显示边界
        mChart.setDrawBorders(true);
        //是否可以拖动
        mChart.setDragEnabled(false);
        //是否有触摸事件
        mChart.setTouchEnabled(true);
        //设置XY轴动画效果
        mChart.animateY(1500);
        mChart.animateX(1500);

        /***XY轴的设置***/
        xAxis = mChart.getXAxis();
        leftYAxis = mChart.getAxisLeft();
        rightYaxis = mChart.getAxisRight();
        //X轴设置显示位置在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        //保证Y轴从0开始，不然会上移一点
        leftYAxis.setAxisMinimum(0f);
        rightYaxis.setAxisMinimum(0f);
        // 网格线
        xAxis.setDrawGridLines(false);
        leftYAxis.setDrawGridLines(true);
        leftYAxis.enableGridDashedLine(10f, 10f, 0f);
        rightYaxis.setDrawGridLines(false);
        rightYaxis.setEnabled(false);

        // 轴显示
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return "";
            }
        });


        mChart.setBackgroundColor(Color.WHITE);
        //是否显示边界
        mChart.setDrawBorders(false);

        /***折线图例 标签 设置***/
        legend = mChart.getLegend();
        //设置显示类型，LINE CIRCLE SQUARE EMPTY 等等 多种方式，查看LegendForm 即可
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(12f);
        //显示位置 右上方
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //是否绘制在图表里面
        legend.setDrawInside(false);

        // 不显示数据描述
        mChart.getDescription().setEnabled(false);
        // 没有数据的时候，显示“暂无数据”
        mChart.setNoDataText("暂无数据");

        mChart.invalidate();
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
            // 趋势
            showTrend(currentData);
        }
    }


    /**
     * 显示当前的状态
     *
     * @param deviceData
     */
    private void showCurentState(DeviceData deviceData) {

        // 温度
        SpannableStringBuilder temp = SpannableStringUtils.getBuilder("温度：")
                .append(String.format("%.2f°", getValue(deviceData, 0)))
                .setForegroundColor(0xFFF48C7A)
                .setProportion(1.8f)
                .setBold()
                .create();
        mTvTemperature.setText(temp);

        // 湿度
        SpannableStringBuilder hum = SpannableStringUtils.getBuilder("湿度：")
                .append(String.format("%.2f%%", getValue(deviceData, 1)))
                .setForegroundColor(0xFF1DE1F7)
                .setProportion(1.8f)
                .setBold()
                .create();
        mTvHumidity.setText(hum);
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

    /**
     * 显示趋势
     *
     * @param currentData
     */
    private void showTrend(List<DeviceData> currentData) {
        // 逆序
        Collections.reverse(currentData);

        List<Entry> tempEntry = new ArrayList<>();
        genValue(currentData, 0, tempEntry);
        List<Entry> humEntry = new ArrayList<>();
        genValue(currentData, 1, humEntry);
        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 1) {
            ((LineDataSet) mChart.getData().getDataSetByIndex(0)).setValues(tempEntry);
            ((LineDataSet) mChart.getData().getDataSetByIndex(1)).setValues(humEntry);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.invalidate();
        } else {
            addLine(currentData, "温度", Color.parseColor("#F48C7A"), 0);
            Drawable drawable = getResources().getDrawable(R.drawable.chart_temp_shader);
            setChartFillDrawable(drawable, 0);
            addLine(currentData, "湿度", Color.parseColor("#1DE1F7"), 1);
            drawable = getResources().getDrawable(R.drawable.chart_hum_shader);
            setChartFillDrawable(drawable, 1);
        }

    }


    /**
     * 曲线初始化设置 一个LineDataSet 代表一条曲线
     *
     * @param lineDataSet 线条
     * @param color       线条颜色
     * @param mode
     */
    private void initLineDataSet(LineDataSet lineDataSet, int color, LineDataSet.Mode mode) {
        // 设置曲线颜色
        lineDataSet.setColor(color);
        // 设置圆圈颜色
        lineDataSet.setCircleColor(color);
        // 设置线宽
        lineDataSet.setLineWidth(1f);
        // 圆半径
        lineDataSet.setCircleRadius(3f);
        //设置曲线值的圆点是实心还是空心
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueTextSize(10f);
        //设置折线图填充
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFormLineWidth(1f);
        lineDataSet.setFormSize(15.f);
        // 设置平滑曲线
        if (mode == null) {
            //设置曲线展示为圆滑曲线（如果不设置则默认折线）
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        } else {
            lineDataSet.setMode(mode);
        }
        // 显示坐标点的小圆点
        lineDataSet.setDrawCircles(true);
        // 显示坐标点的数据
        lineDataSet.setDrawValues(true);
        // 不显示定位线
        lineDataSet.setHighlightEnabled(false);
    }


    /**
     * 设置线条填充背景颜色
     *
     * @param drawable
     */
    public void setChartFillDrawable(Drawable drawable, int index) {
        if (mChart.getData() != null && mChart.getData().getDataSetCount() > index) {
            LineDataSet lineDataSet = (LineDataSet) mChart.getData().getDataSetByIndex(index);
            //避免在 initLineDataSet()方法中 设置了 lineDataSet.setDrawFilled(false); 而无法实现效果
            lineDataSet.setDrawFilled(true);
            lineDataSet.setFillDrawable(drawable);
        }
    }

    /**
     * 添加曲线
     */
    private void addLine(List<DeviceData> dataList, String name, int color, int index) {
        List<Entry> entries = new ArrayList<>();
        genValue(dataList, index, entries);
        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        initLineDataSet(lineDataSet, color, null);
        LineData lineData = mChart.getLineData();
        if (lineData == null) {
            lineData = new LineData(lineDataSet);
            mChart.setData(lineData);
        } else {
            lineData.addDataSet(lineDataSet);
        }
        mChart.invalidate();
    }

    private void genValue(List<DeviceData> dataList, int index, List<Entry> entries) {
        String data;
        float y = 0;
        for (int i = 0; i < dataList.size(); i++) {
            data = dataList.get(i).getData().getData();
            String[] split = data.split("-");
            if (split != null && split.length > index) {
                try {
                    y = Float.parseFloat(split[index]);
                } catch (Exception e) {
                    y = 0;
                }
                entries.add(new Entry(i, y));
            }
        }
    }

}


