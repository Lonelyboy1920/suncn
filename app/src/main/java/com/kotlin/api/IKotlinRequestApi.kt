package com.kotlin.api

import com.kotlin.bean.*
import io.reactivex.Observable
import retrofit2.http.FieldMap
import retrofit2.http.POST
import retrofit2.http.QueryMap

/**
 * @author :Sea
 * Date：2021-3-26 11:34
 * PackageName：com.kotlin
 * Desc：
 */
interface IKotlinRequestApi {

    @POST("AuthServlet")
    fun doLogin(@QueryMap map: Map<String, String>): Observable<KotlinBaseResponse<LoginBean>>


//    //登录
//    @POST("AuthServlet")
//    Observable<BaseResponse<LoginBean>> doLogin(@QueryMap Map<String, String> map);
//
//    //退出登录
//    @POST("LogoutServlet")
//    Observable<BaseResponse<BaseGlobal>> doLoginOut(@QueryMap Map<String, String> map);


    @POST("MyNewsColumnServlet")
    fun getMyNewsColumnServlet(@QueryMap map: Map<String, String>): Observable<KotlinBaseResponse<KotlinNewsColumnListBean>>

    @POST("NewsInfoListServlet")
    fun getNewsInfoListServlet(@QueryMap map: Map<String, String>): Observable<KotlinBaseResponse<KotlinNewsInfoListServletBean>>

    @POST("DynamicListServlet")
    fun getDynamicListServlet(@QueryMap map: Map<String, String>): Observable<KotlinBaseResponse<DynamicListServletBean>>

}