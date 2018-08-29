package wang.tinycoder.easyiotkit.net.cookie;


import wang.tinycoder.easyiotkit.app.EasyIotKit;
import wang.tinycoder.easyiotkit.net.cookie.store.SharedPreCookieStore;

/**
 * @author WangYh
 * @version V1.0
 * @Name: CookieManager
 * @Package wang.tinycoder.easylinkerapp.net.cookie
 * @Description: cookie管理器
 * @date 2018/4/3 0003
 */
public class CookieManager {


    private final CookieJarImpl mCookieJar;

    private CookieManager() {
        mCookieJar = new CookieJarImpl(new SharedPreCookieStore(EasyIotKit.getContext()));
    }

    public static CookieManager getInstance() {
        return InnerHolder.INSTANCE;
    }

    private static class InnerHolder {
        private static CookieManager INSTANCE = new CookieManager();
    }

    public CookieJarImpl getCookieJar() {
        return mCookieJar;
    }
}
