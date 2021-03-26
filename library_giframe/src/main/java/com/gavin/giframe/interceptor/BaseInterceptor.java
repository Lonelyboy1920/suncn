package com.gavin.giframe.interceptor;

import android.content.Context;

import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 请求拦截器
 */
public class BaseInterceptor implements Interceptor {
    private Map<String, String> headers;
    private Map<String, String> params;
    private Context context;

    public BaseInterceptor(Map<String, String> headers, Map<String, String> params, Context context) {
        this.headers = headers;
        this.params = params;
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        //获取request
        Request request = chain.request();
        //从request中获取原有的HttpUrl实例oldHttpUrl
        HttpUrl oldHttpUrl = request.url();
        //获取request的创建者builder
        Request.Builder builder = request.newBuilder();
        //从request中获取headers，通过给定的键url_name
        List<String> headerValues = request.headers("urlname");

        if (GISharedPreUtil.getBoolean(context, "isHasLogin")) {
            if (GISharedPreUtil.getBoolean(context, "resetUserRole")) { // 如果需要接受规定的intUserRole，则此处无需再传intUserRole
                params.remove("intUserRole"); // 需要进行此操作
                GISharedPreUtil.setValue(context, "resetUserRole", false);
            } else {
                params.put("intUserRole", GISharedPreUtil.getInt(context, "intUserRole") + ""); // 用户类型（0-委员；1-用户；2-承办单位）
            }
            params.put("strSid", GIStringUtil.nullToEmpty(GISharedPreUtil.getString(context, "strSid") + ""));
            params.put("strUdid", GIStringUtil.nullToEmpty(GISharedPreUtil.getString(context, "deviceCode") + ""));
        } else {
            params.remove("strSid"); // 需要进行此操作
            params.remove("intUserRole"); // 需要进行此操作
        }
        if (headerValues != null && headerValues.size() > 0) { // 替换服务器地址（如：贵阳）
            //如果有这个header，先将配置的header删除，因此header仅用作app和okhttp之间使用
            builder.removeHeader("urlname");
            //匹配获得新的BaseUrl
            String headerValue = headerValues.get(0);
            HttpUrl newBaseUrl = null;
            if ("url_gy".equals(headerValue)
                    && GIStringUtil.isNotBlank(GISharedPreUtil.getString(context, "strWebUrl"))) {
                newBaseUrl = HttpUrl.parse(GISharedPreUtil.getString(context, "strWebUrl"));
            } else {
                newBaseUrl = oldHttpUrl;
            }
            //重建新的HttpUrl，修改需要修改的url部分
            assert newBaseUrl != null;
            HttpUrl newFullUrl = oldHttpUrl
                    .newBuilder()
                    .host(newBaseUrl.host())//更换主机名
                    .port(newBaseUrl.port())//更换端口
                    .removePathSegment(1)
                    .removePathSegment(0)
                    .build();
            if (params != null && params.size() > 0) {
                Set<String> keys = params.keySet();
                for (String paramKey : keys) {
                    newFullUrl.newBuilder().addQueryParameter(paramKey, params.get(paramKey)).build();
                }
            }
            //重建这个request，通过builder.url(newFullUrl).build()；
            // 然后返回一个response至此结束修改
            return chain.proceed(builder.url(newFullUrl).build());
        } else {
            //添加请求参数
            HttpUrl.Builder urlBuilder = request.url().newBuilder();
            if (params != null && params.size() > 0) {
                Set<String> keys = params.keySet();
                for (String paramKey : keys) {
                    urlBuilder.addQueryParameter(paramKey, params.get(paramKey)).build();
                }
            }
            //添加请求头
            if (headers != null && headers.size() > 0) {
                Set<String> keys = headers.keySet();
                for (String headerKey : keys) {
                    builder.addHeader(headerKey, headers.get(headerKey)).build();
                }
            }
            builder.method(request.method(), request.body());
            builder.url(urlBuilder.build());
            //请求信息
            return chain.proceed(builder.build());
        }
    }
}