package com.kotlin.api

import com.gavin.giframe.http.BaseResponse
import com.suncn.ihold_zxztc.bean.LoginBean
import io.reactivex.Observable
import retrofit2.http.POST
import retrofit2.http.QueryMap

/**
 * @author :Sea
 * Date：2021-3-26 11:34
 * PackageName：com.kotlin
 * Desc：
 */
abstract class IKotlinRequestApi {

    @POST
    abstract fun doLogin(@QueryMap map: Map<String, String>): Observable<BaseResponse<LoginBean>>


//    //登录
//    @POST("AuthServlet")
//    Observable<BaseResponse<LoginBean>> doLogin(@QueryMap Map<String, String> map);
//
//    //退出登录
//    @POST("LogoutServlet")
//    Observable<BaseResponse<BaseGlobal>> doLoginOut(@QueryMap Map<String, String> map);

}