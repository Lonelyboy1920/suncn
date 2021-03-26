package com.suncn.ihold_zxztc;

import android.annotation.SuppressLint;
import android.content.Context;

import com.gavin.giframe.http.CookieJarImpl;
import com.gavin.giframe.http.PersistentCookieStore;
import com.gavin.giframe.interceptor.BaseInterceptor;
import com.gavin.giframe.interceptor.logging.Level;
import com.gavin.giframe.interceptor.logging.LoggingInterceptor;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.HttpsUtils;
import com.suncn.ihold_zxztc.utils.Utils;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.internal.platform.Platform;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * RetrofitClient封装单例类, 实现网络请求
 */
public class ApiManager {
    private Retrofit retrofit;
    private static final int DEFAULT_TIMEOUT = 20;  //超时时间
    private static final int CACHE_TIMEOUT = 10 * 1024 * 1024; //缓存时间
    @SuppressLint("StaticFieldLeak")
    private static Context mContext = GIUtil.getContext();

    private static volatile IRequestApi INSTANCE;

    public static IRequestApi getInstance() {
        if (INSTANCE == null) {
            synchronized (ApiManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ApiManager().getAppApi();
                }
            }
        }
        return INSTANCE;
    }

    public ApiManager() {
        this(null);
    }

    /**
     * 重新初始化RetrofitClient
     */
    public static void refreshRetrofit() {
        INSTANCE = null;
        INSTANCE = new ApiManager().getAppApi();
    }

    private IRequestApi getAppApi() {
        return retrofit.create(IRequestApi.class);
    }

    private ApiManager(Map<String, String> headers) {
        String baseUrl = Utils.getFileDomain(mContext) + "ios/"; //服务端根路径
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .client(initClient())
                .build();
        //requestApi = retrofit.create(IRequestApi.class);
    }

    private static OkHttpClient initClient() {
        Map<String, String> params = new HashMap<>();
        params.put("websvrpwd", DefineUtil.websvrpwd);
//        boolean isHasLogin = GISharedPreUtil.getBoolean(mContext, "isHasLogin", false);
//        if (isHasLogin) {
//            params.put("intUserRole", GISharedPreUtil.getInt(mContext, "intUserRole") + ""); // 用户类型（0-委员；1-用户；2-承办单位）
//            params.put("strSid", GIStringUtil.nullToEmpty(GISharedPreUtil.getString(mContext, "strSid") + ""));
//        }
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder()
                .cookieJar(new CookieJarImpl(new PersistentCookieStore(mContext)))
                .addInterceptor(new BaseInterceptor(null, params, mContext))
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .addInterceptor(new LoggingInterceptor
                        .Builder()//构建者模式
                        .loggable(GISharedPreUtil.getBoolean(mContext, "isDebugMode")) //是否开启日志打印
                        .setLevel(Level.BODY) //打印的等级
                        .log(Platform.WARN) // 打印类型
                        .request("GILogApi") // request的Tag
                        .response("GILogApi")// Response的Tag
                        .isSimpleChinese(GISharedPreUtil.getBoolean(mContext, "isSimpleChinase", true))   //是否是简体中文
                        .build()
                )
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(8, 15, TimeUnit.SECONDS));
        return okHttpClient.build();
    }
}
