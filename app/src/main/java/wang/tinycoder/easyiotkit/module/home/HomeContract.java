package wang.tinycoder.easyiotkit.module.home;


import wang.tinycoder.easyiotkit.base.interfaces.IModel;
import wang.tinycoder.easyiotkit.base.interfaces.IView;

/**
 * Progect：EasyLinkerAppNew
 * Package：wang.tinycoder.easylinkerapp.module.home
 * Desc：
 * Author：TinycoderWang
 * CreateTime：2018/4/8 17:21
 */
public interface HomeContract {

    interface View extends IView {

        void cookieOverTime();
    }

    interface Model extends IModel {
        
    }

}
