package wang.tinycoder.easyiotkit.module.splash;

import io.reactivex.Observer;
import wang.tinycoder.easyiotkit.base.interfaces.IModel;
import wang.tinycoder.easyiotkit.base.interfaces.IView;
import wang.tinycoder.easyiotkit.bean.NetResult;
import wang.tinycoder.easyiotkit.bean.User;

/**
 * @author WangYh
 * @version V1.0
 * @Name: SplashContract
 * @Package wang.tinycoder.easylinkerapp.module.splash
 * @Description: 闪屏契约类
 * @date 2018/4/3 0003
 */
public interface SplashContract {

    interface View extends IView {
        // 请求结果返回
        void response(boolean hasCookie);
    }

    interface Model extends IModel {
        /**
         * 获取当前登陆的用户
         */
        void getCurrentUser(Observer<NetResult<User>> observer);
    }

}
