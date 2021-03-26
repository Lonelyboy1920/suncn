package com.kotlin.net

import android.preference.Preference
import com.kotlin.api.IKotlinRequestApi
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * @author :Sea
 * Date：2021-3-26 14:12
 * PackageName：com.kotlin.net
 * Desc：
 */
object RetrofitManager {

    val service: IKotlinRequestApi by lazy {
        getRetrofit().create(IKotlinRequestApi::class.java)
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
                .baseUrl("")
                .client(getOkHttpClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    private fun getOkHttpClient(): OkHttpClient {
        //添加一个Log拦截器
        var httpLoggingInterceptor = HttpLoggingInterceptor()
        //可以设置过滤拦截的水平,body ,basic,headers
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        //设置缓存的大小跟位置
        val cacheFile = File("", "cache")
        val cache = Cache(cacheFile, 1024 * 1024 * 50)//50Mb缓存的大小

        return OkHttpClient.Builder()
                .addInterceptor(addQueryParameterInterceptor()) //全局参数
                .addInterceptor(addHeaderInterceptor())
                .addInterceptor(httpLoggingInterceptor)
                .cache(cache)
                .connectTimeout(60L, TimeUnit.SECONDS)
                .readTimeout(60L, TimeUnit.SECONDS)
                .writeTimeout(60L, TimeUnit.SECONDS)
                .build()

    }

    /**
     * 设置公共参数
     */
    private fun addQueryParameterInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val request: Request
            val modifiedUrl = originalRequest.url().newBuilder()
                    .addQueryParameter("udid", "d2807c895f0348a180148c9dfa6f2feeac0781b5")
                    .addQueryParameter("deviceModel", "Android")
                    .build()
            request = originalRequest.newBuilder().url(modifiedUrl).build()
            chain.proceed(request)
        }
    }

    /**
     * 设置头
     */
    private fun addHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->
            val orginalRequest = chain.request()
            val requestBuilder = orginalRequest.newBuilder()
                    .header("token", "token")
                    .method(orginalRequest.method(), orginalRequest.body())
            val request = requestBuilder.build()
            chain.proceed(request)
        }
    }

//    /**
//     * 设置缓存
//     */
//    private fun addCacheInterceptor(): Interceptor {
//        return Interceptor { chain ->
//            var request = chain.request()
//            if (!NetworkUtil.isNetworkAvailable(MyApplication.context)) {
//                request = request.newBuilder()
//                        .cacheControl(CacheControl.FORCE_CACHE)
//                        .build()
//            }
//            val response = chain.proceed(request)
//            if (NetworkUtil.isNetworkAvailable(MyApplication.context)) {
//                val maxAge = 0
//                // 有网络时 设置缓存超时时间0个小时 ,意思就是不读取缓存数据,只对get有用,post没有缓冲
//                response.newBuilder()
//                        .header("Cache-Control", "public, max-age=" + maxAge)
//                        .removeHeader("Retrofit")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
//                        .build()
//            } else {
//                // 无网络时，设置超时为4周  只对get有用,post没有缓冲
//                val maxStale = 60 * 60 * 24 * 28
//                response.newBuilder()
//                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
//                        .removeHeader("nyn")
//                        .build()
//            }
//            response
//        }
//    }

}