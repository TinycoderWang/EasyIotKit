package wang.tinycoder.easyiotkit.net;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import wang.tinycoder.easyiotkit.bean.Device;
import wang.tinycoder.easyiotkit.bean.DeviceData;
import wang.tinycoder.easyiotkit.bean.Group;
import wang.tinycoder.easyiotkit.bean.GroupDevData;
import wang.tinycoder.easyiotkit.bean.NetResult;
import wang.tinycoder.easyiotkit.bean.SystemState;
import wang.tinycoder.easyiotkit.bean.User;

/**
 * Progect：EasyLinkerApp
 * Package：wang.tinycoder.easylinkerapp.net
 * Desc：
 * Author：TinycoderWang
 * CreateTime：2018/4/1 14:28
 */
public interface Api {


    /**
     * 用户注册
     *
     * @param body 参数体
     * @return
     */
    //@Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("user/register")
    Observable<NetResult> register(@Body RequestBody body);

    /**
     * 用户登录
     *
     * @param body 参数体
     * @return
     */
    @POST("userLogin")
    Observable<NetResult<User>> login(@Body RequestBody body);

    /**
     * 获取用户信息
     *
     * @return
     */
    @GET("front/getCurrentUserInfo")
    Observable<NetResult<User>> getCurrentUser();


    /**
     * 更新用户信息
     *
     * @param body
     * @return
     */
    @POST("user/updateUser")
    Observable<NetResult> updateUserInfo(@Body RequestBody body);

    /**
     * 退出登录
     *
     * @return
     */
    @GET("logOut")
    Observable<NetResult> logout();

    /**
     * 获取所有的设备分组
     *
     * @return
     */
    @GET("user/getALlGroups")
    Observable<NetResult<List<Group>>> getAllDeviceGroups();


    /**
     * 分页获取所有的设备分组
     *
     * @param page  页码
     * @param count 数量
     * @return
     */

    @GET("user/getAllGroupByPage/{page}/{count}")
    Observable<NetResult<List<Group>>> getAllDeviceGroupsByPage(@Path("page") int page,
                                                                @Path("count") int count);


    /**
     * 添加群组
     *
     * @param body
     * @return
     */
    @POST("user/addGroup")
    Observable<NetResult> addDevGroup(@Body RequestBody body);

    /**
     * 更新群组
     *
     * @param body
     * @return
     */
    @POST("user/updateGroup")
    Observable<NetResult> updateDevGroup(@Body RequestBody body);

    /**
     * 获取当前的设备状态
     *
     * @return
     */
    @GET("user/getCurrentState")
    Observable<NetResult<SystemState>> getCurrentState();

    /**
     * 根据分组id获取设备
     *
     * @param groupId 组id
     * @param page    页码
     * @param count   每页数量
     * @return
     */
    @GET("user/getAllDevicesByGroup/{groupId}/{page}/{count}")
    Observable<NetResult<GroupDevData>> getAllDevicesByGroup(@Path("groupId") String groupId,
                                                             @Path("page") int page,
                                                             @Path("count") int count);


    /**
     * 获取用户的所有设备
     *
     * @param page  页码
     * @param count 数量
     * @return
     */
    @GET("user/getAllDevices/{page}/{count}")
    Observable<NetResult<GroupDevData>> getAllDevices(@Path("page") int page,
                                                      @Path("count") int count);


    /**
     * 绑定设备
     *
     * @param groupId  群组id
     * @param deviceId 设备id
     * @return
     */
    @GET("user/bind/{deviceId}/{groupId}")
    Observable<NetResult<Device>> bindDevice(@Path("deviceId") String deviceId,
                                             @Path("groupId") String groupId);


    /**
     * 绑定设备到默认分组
     *
     * @param deviceId 设备id
     * @return
     */
    @GET("user/bindToDefaultGroup/{deviceId}")
    Observable<NetResult<Device>> bindDeviceByDefaute(@Path("deviceId") String deviceId);

    /**
     * 创建设备
     *
     * @param body
     * @return
     */
    @POST("user/createADevice")
    Observable<NetResult> createDevice(@Body RequestBody body);

    /**
     * 向设备发送指令
     *
     * @param body
     * @return
     */
    @POST("api/v1/sendCmdToDevice")
    Observable<NetResult> sendCommandToDevice(@Body RequestBody body);


    /**
     * 获取设备的数据
     *
     * @param page     页码
     * @param count    数量
     * @param deviceId 设备id
     * @return
     */
    @GET("/device/getDeviceData/{deviceId}/{page}/{count}")
    Observable<NetResult<List<DeviceData>>> getDeviceData(@Path("page") int page,
                                                          @Path("count") int count,
                                                          @Path("deviceId") String deviceId);


}
