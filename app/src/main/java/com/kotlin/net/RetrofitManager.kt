package com.kotlin.net

import android.content.Context
import com.gavin.giframe.http.CookieJarImpl
import com.gavin.giframe.http.PersistentCookieStore
import com.gavin.giframe.utils.GISharedPreUtil
import com.gavin.giframe.utils.GIStringUtil
import com.kotlin.api.IKotlinRequestApi
import com.suncn.ihold_zxztc.ApiManager
import com.suncn.ihold_zxztc.MyApplication
import com.suncn.ihold_zxztc.utils.DefineUtil
import com.suncn.ihold_zxztc.utils.HttpsUtils
import com.suncn.ihold_zxztc.utils.Utils
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author :Sea
 * Date：2021-3-26 14:12
 * PackageName：com.kotlin.net
 * Desc：
 */
class RetrofitManager {
    lateinit var retrofit: Retrofit
    var mContext: Context = MyApplication.getInstance().applicationContext


    lateinit var INSTANCE: IKotlinRequestApi


    open fun getInstance(): IKotlinRequestApi {
//        if (INSTANCE == null) {
//            synchronized(RetrofitManager) {
        INSTANCE = getAppApi()
//            }
//        }
        return INSTANCE
    }


    private fun getAppApi(): IKotlinRequestApi {
        var baseUrl = Utils.getFileDomain(mContext) + "ios/"
        retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .client(getOkHttpClient())
                .build()
        return retrofit.create(IKotlinRequestApi::class.java)
    }

    private fun getOkHttpClient(): OkHttpClient {
        var params = HashMap<String, String>()
        params.put("websvrpwd", DefineUtil.websvrpwd)
        var sslParams = HttpsUtils.getSslSocketFactory()
        var okHttpClient = OkHttpClient.Builder()
                .cookieJar(CookieJarImpl(PersistentCookieStore(MyApplication.myApplication.applicationContext)))
                .addInterceptor(BaseInterceptor(null, params, MyApplication.myApplication.applicationContext))
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .connectTimeout(30L, TimeUnit.SECONDS)
                .readTimeout(30L, TimeUnit.SECONDS)
                .writeTimeout(30L, TimeUnit.SECONDS)
                .connectionPool(ConnectionPool(8, 15, TimeUnit.SECONDS))
                .build()
        return okHttpClient
    }

    class BaseInterceptor(headers: HashMap<String, String>?, params: HashMap<String, String>, context: Context) : Interceptor {
        var mHeaders = headers
        var mParams = params
        var mContext = context

        override fun intercept(chain: Interceptor.Chain): Response {
            var request: Request = chain.request()
            var builder = request.newBuilder()
            mParams.remove("strSid")
            mParams.remove("intUserRole")
//            if (GISharedPreUtil.getBoolean(mContext, "isHasLogin")) {
            mParams.put("intUserRole", GISharedPreUtil.getInt(mContext, "intUserRole").toString() + "") // 用户类型（0-委员；1-用户；2-承办单位）
            mParams.put("strSid", GIStringUtil.nullToEmpty(GISharedPreUtil.getString(mContext, "strSid") + ""))
            mParams.put("strUdid", GIStringUtil.nullToEmpty(GISharedPreUtil.getString(mContext, "deviceCode") + ""))
//            } else {
//            mParams.remove("strSid")
//            mParams.remove("intUserRole")
//            }
            var urlBuilder = request.url().newBuilder()
            if (null != mParams && mParams.size > 0) {
                var keys = mParams.keys
                for (headKey in keys) {
                    urlBuilder.addQueryParameter(headKey, mParams.get(headKey)).build()
                }
            }
            builder.method(request.method(), request.body())
            builder.url(urlBuilder.build())
            return chain.proceed(builder.build())
        }
    }
}

