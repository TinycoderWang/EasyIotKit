package wang.tinycoder.easyiotkit.base.interfaces;

/**
 * @author WangYh
 * @version V1.0
 * @Name: IFragment
 * @Package wang.tinycoder.easylinkerapp.base.interfaces
 * @Description: fragment的接口
 * @date 2018/4/4 0004
 */
public interface IFragment {

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
