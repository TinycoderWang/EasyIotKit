package wang.tinycoder.easyiotkit.net;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Url;
import wang.tinycoder.easyiotkit.bean.WeatherBean;

/**
 * Progect：EasyIotKit
 * Package：wang.tinycoder.easyiotkit.net
 * Desc：其他API
 * Author：TinycoderWang
 * CreateTime：2018/11/4 13:15
 */
public interface OtherApi {
    /**
     * 高德地图天气API
     *
     * @param url
     * @return
     */
    @GET
    Observable<WeatherBean> requestWeather(@Url String url);
}
