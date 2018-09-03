package wang.tinycoder.easyiotkit.base.interfaces;

/**
 * Progect：EasyLinkerApp
 * Package：wang.tinycoder.easylinkerapp.base
 * Desc：view的接口
 * Author：TinycoderWang
 * CreateTime：2018/4/1 8:47
 */
public interface IView {

    /**
     * 显示提示信息
     *
     * @param message
     */
    void showMessage(String message);


    /**
     * 显示加载
     */
    void showLoading();

    void showLoading(String message);

    /**
     * 隐藏加载
     */
    void hideLoading();
}
