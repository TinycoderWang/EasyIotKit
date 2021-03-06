package wang.tinycoder.easyiotkit.module.login;

import io.reactivex.Observer;
import wang.tinycoder.easyiotkit.base.interfaces.IModel;
import wang.tinycoder.easyiotkit.base.interfaces.IView;
import wang.tinycoder.easyiotkit.bean.NetResult;
import wang.tinycoder.easyiotkit.bean.User;

/**
 * Progect：EasyLinkerApp
 * Package：wang.tinycoder.easylinkerapp.module.login
 * Desc：登陆的契约类
 * Author：TinycoderWang
 * CreateTime：2018/4/1 8:43
 */
public interface LoginContract {

    interface View extends IView {

        String getUserName();

        String getUserPassword();

        void loginSuccess();
    }

    interface Model extends IModel {

        /**
         * 登陆
         *
         * @param userName 用户名
         * @param password 密码
         * @param observer
         */
        void login(String userName, String password, Observer<NetResult<User>> observer);
    }

}
