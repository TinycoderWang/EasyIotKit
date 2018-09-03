package wang.tinycoder.easyiotkit.module.splash;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import butterknife.BindView;
import io.reactivex.functions.Consumer;
import okhttp3.Cookie;
import wang.tinycoder.easyiotkit.R;
import wang.tinycoder.easyiotkit.app.EasyIotKit;
import wang.tinycoder.easyiotkit.base.BaseActivity;
import wang.tinycoder.easyiotkit.module.home.HomeActivity;
import wang.tinycoder.easyiotkit.module.login.LoginActivity;
import wang.tinycoder.easyiotkit.net.cookie.CookieManager;
import wang.tinycoder.easyiotkit.net.cookie.store.CookieStore;

/**
 * @author WangYh
 * @version V1.0
 * @Name: SplashActivity
 * @Package wang.tinycoder.easylinkerapp.module.splash
 * @Description: 闪屏页面
 * @date 2018/4/3 0003
 */
public class SplashActivity extends BaseActivity<SplashPresenter> implements SplashContract.View {


    @BindView(R.id.tv_version)
    TextView mTvVersion;

    // 闪屏延时时间（单位ms）
    public int DELAY_TIME = 2500;
    // 时间
    long start = 0;
    long end = 0;
    // 消息类型
    private final int GOTO_HOME = 0;
    private final int GOTO_LOGIN = 1;
    private int msgType = GOTO_HOME;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg != null) {
                Intent intent = null;
                switch (msg.what) {
                    case GOTO_HOME:
                        intent = new Intent(SplashActivity.this, HomeActivity.class);
                        SplashActivity.this.startActivity(intent);
                        break;
                    case GOTO_LOGIN:
                        intent = new Intent(SplashActivity.this, LoginActivity.class);
                        SplashActivity.this.startActivity(intent);
                        break;
                }
                SplashActivity.this.finish();
            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void initPresenter() {
        mPresenter = new SplashPresenter(this, new SplashModel());
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {

        // 版本
        mTvVersion.setText(EasyIotKit.getInstance().getVerName());


        // 申请定位权限
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.requestEach(
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.READ_PHONE_STATE)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            Logger.i("%s 允许权限！", TAG);
                        } else {
                            Logger.i("%s 不允许权限！", TAG);
                            showMessage("由于你拒绝了某些权限，部分功能将无法正常使用！");
                        }
                        start();
                    }
                });
    }

    private void start() {
        // 记录开始时间
        start = SystemClock.currentThreadTimeMillis();
        // 检查本地是否存在cookie
        boolean hasCookie = hasLocalCookie();
        end = start;
        if (!hasCookie) {   // 本地不存在cookie
            end = SystemClock.currentThreadTimeMillis();
            if (end - start > DELAY_TIME) {   // 耗时超过规定时间
                mHandler.sendEmptyMessage(GOTO_LOGIN);
            } else {
                new Thread() {
                    @Override
                    public void run() {
                        // 延时剩余时间
                        SystemClock.sleep(DELAY_TIME - (end - start));
                        mHandler.sendEmptyMessage(GOTO_LOGIN);
                    }
                }.start();
            }
        } else {   // 存在cookie联网确认cookie是否有效
            mPresenter.requestCurrentUser();
        }
    }

    @Override
    public void response(final boolean hasCookie) {
        end = SystemClock.currentThreadTimeMillis();
        if (end - start > DELAY_TIME) {   // 耗时超过规定时间
            if (hasCookie) {
                mHandler.sendEmptyMessage(GOTO_HOME);
            } else {
                mHandler.sendEmptyMessage(GOTO_LOGIN);
            }
        } else {
            new Thread() {
                @Override
                public void run() {
                    // 延时剩余时间
                    SystemClock.sleep(DELAY_TIME - (end - start));
                    if (hasCookie) {
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(GOTO_HOME);
                        }
                    } else {
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(GOTO_LOGIN);
                        }
                    }
                }
            }.start();
        }
    }

    // 检查是否需要登录
    private boolean hasLocalCookie() {
        // 判断本地是否存在cookie
        CookieStore cookieStore = CookieManager.getInstance().getCookieJar().getCookieStore();
        if (cookieStore != null) {
            List<Cookie> cookies = cookieStore.getCookies();
            if (cookies != null && cookies.size() > 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        mHandler.removeMessages(GOTO_HOME);
        mHandler.removeMessages(GOTO_LOGIN);
        mHandler = null;
        super.onDestroy();
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

    }


}
