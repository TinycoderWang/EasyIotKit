package wang.tinycoder.easyiotkit.module.devguide.guide;

import wang.tinycoder.easyiotkit.base.interfaces.IModel;
import wang.tinycoder.easyiotkit.base.interfaces.IView;

/**
 * Progect：EasyIotKit
 * Package：wang.tinycoder.easyiotkit.module.devguide.guide
 * Desc：
 * Author：TinycoderWang
 * CreateTime：2018/9/5 20:34
 */
public interface AddDevGuideContract {
    interface View extends IView {

        void onBindResult(boolean result);
    }

    interface Model extends IModel {
    }
}
