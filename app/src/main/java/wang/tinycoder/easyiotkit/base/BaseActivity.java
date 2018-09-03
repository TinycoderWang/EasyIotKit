package wang.tinycoder.easyiotkit.base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.gyf.barlibrary.OSUtils;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import wang.tinycoder.easyiotkit.app.AtyContainer;
import wang.tinycoder.easyiotkit.base.interfaces.IActivity;
import wang.tinycoder.easyiotkit.base.interfaces.IPresenter;
import wang.tinycoder.easyiotkit.base.interfaces.IView;
import wang.tinycoder.easyiotkit.widget.LoaddingDialog;

/**
 * Progect：EasyLinkerApp
 * Package：wang.tinycoder.easylinkerapp.base
 * Desc：
 * Author：TinycoderWang
 * CreateTime：2018/4/1 8:53
 */
public abstract class BaseActivity<P extends IPresenter> extends AppCompatActivity implements IActivity, IView {

    protected final String TAG = this.getClass().getSimpleName();

    protected Unbinder mUnbinder;
    protected P mPresenter;
    protected Toast mToast;
    protected LoaddingDialog mLoaddingDialog;

    // 沉浸式
    protected ImmersionBar mImmersionBar;
    private static final String NAVIGATIONBAR_IS_MIN = "navigationbar_is_min";
    private ContentObserver mNavigationStatusObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            int navigationBarIsMin = Settings.System.getInt(getContentResolver(),
                    NAVIGATIONBAR_IS_MIN, 0);
            if (navigationBarIsMin == 1) {
                //导航键隐藏了
                mImmersionBar.transparentNavigationBar().init();
            } else {
                //导航键显示了
                mImmersionBar.navigationBarColor(android.R.color.black) //隐藏前导航栏的颜色
                        .fullScreen(false)
                        .init();
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //初始化沉浸式
        initImmersionBar();
        //解决华为emui3.0与3.1手机手动隐藏底部导航栏时，导航栏背景色未被隐藏的问题
        if (OSUtils.isEMUI3_1()) {
            //第一种
            getContentResolver().registerContentObserver(Settings.System.getUriFor
                    (NAVIGATIONBAR_IS_MIN), true, mNavigationStatusObserver);
        }

        setContentView(getLayoutId());
        // 保存当前activity
        AtyContainer.getInstance().addActivity(this);
        initPresenter();
        //绑定到butterknife
        mUnbinder = ButterKnife.bind(this);
        initView(savedInstanceState);
        initData(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        AtyContainer.getInstance().removeActivity(this);
        if (mUnbinder != null && mUnbinder != Unbinder.EMPTY) {
            mUnbinder.unbind();
        }
        this.mUnbinder = null;
        if (mPresenter != null) {
            mPresenter.onDestroy();//释放资源
        }
        this.mPresenter = null;

        // 销毁沉浸式
        mImmersionBar.destroy();
        mImmersionBar = null;

        super.onDestroy();
    }

    private void initImmersionBar() {
        //在BaseActivity里初始化
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.init();
    }


    @Override
    public void showToast(@NonNull String message) {
        if (mToast == null) {
            mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        }
        mToast.setText(message);
        mToast.show();
    }

    @Override
    public void launchOtherActivity(@NonNull Intent intent, boolean finishSelfe) {
        startActivity(intent);
        if (finishSelfe) {
            finish();
        }
    }

    @Override
    public void launchOtherActivityForResult(@NonNull Intent intent, int requestCode, boolean finishSelfe) {
        startActivityForResult(intent, requestCode);
        if (finishSelfe) {
            finish();
        }
    }


    @Override
    public void showLoading(final String message) {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mLoaddingDialog == null) {
                        mLoaddingDialog = new LoaddingDialog();
                        mLoaddingDialog.setContext(BaseActivity.this);
                    }
                    mLoaddingDialog.setMessage(TextUtils.isEmpty(message) ? "正在加载" : message);
                    if (!mLoaddingDialog.isAdded()) {
                        mLoaddingDialog.show(getFragmentManager(), TAG);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void showLoading() {
        showLoading(null);
    }

    @Override
    public void hideLoading() {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mLoaddingDialog != null) {
                        mLoaddingDialog.dismiss();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void showMessage(String message) {
        showToast(message);
    }

    /**
     * 获取当前本地apk的版本
     *
     * @return
     */
    public int getVersionCode() {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = getPackageManager().
                    getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 隐藏软键盘
     *
     * @param view
     */
    public void hideInput(View view) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive()) {
                imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideInput() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            // 获取软键盘的显示状态
            if (imm.isActive()) {
                // 如果软键盘已经显示，则隐藏，反之则显示
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
