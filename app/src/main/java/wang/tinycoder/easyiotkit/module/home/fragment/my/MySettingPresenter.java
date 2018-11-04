package wang.tinycoder.easyiotkit.module.home.fragment.my;

import com.orhanobut.logger.Logger;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import wang.tinycoder.easyiotkit.base.BasePresenter;
import wang.tinycoder.easyiotkit.bean.NetResult;
import wang.tinycoder.easyiotkit.bean.User;
import wang.tinycoder.easyiotkit.net.cookie.CookieManager;

/**
 * Progect：EasyLinkerAppNew
 * Package：wang.tinycoder.easylinkerapp.module.home.fragment.my
 * Desc：个人设置的presenter
 * Author：TinycoderWang
 * CreateTime：2018/4/22 10:54
 */
public class MySettingPresenter extends BasePresenter<MySettingContract.View, MySettingContract.Model> {
    private User mUserInfo;

    public MySettingPresenter(MySettingContract.View rootView, MySettingContract.Model model) {
        super(rootView, model);
    }

    /**
     * 加载个人信息
     */
    public void loadSelfInfo() {
        mModel.getCurrentUser(new Observer<NetResult<User>>() {
            @Override
            public void onSubscribe(Disposable d) {
                mCompositeDisposable.add(d);
            }

            @Override
            public void onNext(NetResult<User> userNetResult) {
                if (userNetResult != null) {
                    if (NetResult.SUCCESS == userNetResult.getState()) {
                        mUserInfo = userNetResult.getData();
                        mView.onLoadSelfInfo(true);
                    } else {
                        mView.onLoadSelfInfo(false);
                    }
                } else {
                    mView.onLoadSelfInfo(false);
                }
            }

            @Override
            public void onError(Throwable e) {
                Logger.e("%s --- %s", TAG, e.getMessage());
                mView.onLoadSelfInfo(false);
            }

            @Override
            public void onComplete() {
                Logger.i("%s --- onComplete", TAG);
            }
        });
    }

    public User getUserInfo() {
        return mUserInfo;
    }

    // 退出登录
    public void logout() {
        mModel.logout(new Observer<NetResult>() {
            @Override
            public void onSubscribe(Disposable d) {
                mCompositeDisposable.add(d);
            }

            @Override
            public void onNext(NetResult netResult) {
                if (netResult != null) {
                    if (NetResult.SUCCESS == netResult.getState()) {
                        // 删除cookie
                        CookieManager.getInstance().getCookieJar().cleanCookie();
                        // 回到登陆页面
                        mView.logoutSuccess();
                    }
                    mView.showMessage(netResult.getMessage());
                } else {

                }
            }

            @Override
            public void onError(Throwable e) {
                Logger.i("%s --- %s", TAG, e.getMessage());
            }

            @Override
            public void onComplete() {
                Logger.i("%s --- onComplete", TAG);
            }
        });

    }
}
