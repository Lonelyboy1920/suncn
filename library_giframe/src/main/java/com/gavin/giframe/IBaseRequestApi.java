package com.gavin.giframe;

import com.gavin.giframe.entity.CrashReportResult;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;

/**
 * 请求接口
 */
public interface IBaseRequestApi {
    //异常数据收集
    @Multipart
    @POST("BugCollectServlet")
    Observable<CrashReportResult> doCrashReport(@Part() List<MultipartBody.Part> parts, @QueryMap Map<String, String> map);
}
