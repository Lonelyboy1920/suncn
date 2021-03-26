/*
 * Copyright 2014-2017 Eduard Ereza Martínez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gavin.giframe.crash;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gavin.giframe.IBaseRequestApi;
import com.gavin.giframe.R;
import com.gavin.giframe.entity.CrashReportResult;
import com.gavin.giframe.interceptor.logging.Level;
import com.gavin.giframe.interceptor.logging.LoggingInterceptor;
import com.gavin.giframe.utils.GIDensityUtil;
import com.gavin.giframe.utils.GIPhoneUtils;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.RxUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ConnectionPool;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.internal.platform.Platform;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public final class CrashActivity extends AppCompatActivity {
    private Retrofit retrofit;
    private Activity activity;

    @SuppressLint("PrivateResource")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        GIDensityUtil.initStatusBar(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        initRetrofit();

        //This is needed to avoid a crash if the developer has not specified
        //an app-level theme that extends Theme.AppCompat
        TypedArray a = obtainStyledAttributes(R.styleable.AppCompatTheme);
        if (!a.hasValue(R.styleable.AppCompatTheme_windowActionBar)) {
            setTheme(R.style.Theme_AppCompat_Light_DarkActionBar);
        }
        a.recycle();

        setContentView(R.layout.activity_crash);

        //Close/restart button logic:
        //If a class if set, use restart.
        //Else, use close and just finish the app.
        //It is recommended that you follow this logic if implementing a custom error activity.
        Button restartButton = findViewById(R.id.customactivityoncrash_error_activity_restart_button);

        final CaocConfig config = CustomActivityOnCrash.getConfigFromIntent(getIntent());

        if (config.isShowRestartButton() && config.getRestartActivityClass() != null) {
            restartButton.setText(R.string.customactivityoncrash_error_activity_restart_app);
            restartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomActivityOnCrash.restartApplication(CrashActivity.this, config);
                }
            });
        } else {
            restartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomActivityOnCrash.closeApplication(CrashActivity.this, config);
                }
            });
        }

        Button moreInfoButton = findViewById(R.id.customactivityoncrash_error_activity_more_info_button);

        if (config.isShowErrorDetails()) {
            moreInfoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //We retrieve all the error data and show it

                    AlertDialog dialog = new AlertDialog.Builder(CrashActivity.this)
                            .setTitle(R.string.customactivityoncrash_error_activity_error_details_title)
                            .setMessage(CustomActivityOnCrash.getAllErrorDetailsFromIntent(CrashActivity.this, getIntent()))
                            .setPositiveButton(R.string.customactivityoncrash_error_activity_error_details_close, null)
                            .setNeutralButton(R.string.customactivityoncrash_error_activity_error_details_copy,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            copyErrorToClipboard();
                                            Toast.makeText(CrashActivity.this, R.string.customactivityoncrash_error_activity_error_details_copied, Toast.LENGTH_SHORT).show();
                                        }
                                    })
                            .show();
                    TextView textView = dialog.findViewById(android.R.id.message);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
                }
            });
        } else {
            moreInfoButton.setVisibility(View.GONE);
        }

        Integer defaultErrorActivityDrawableId = config.getErrorDrawable();
        ImageView errorImageView = findViewById(R.id.customactivityoncrash_error_activity_image);

        if (defaultErrorActivityDrawableId != null) {
            errorImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), defaultErrorActivityDrawableId, getTheme()));
        }

        doRequest();
    }

    private void copyErrorToClipboard() {
        String errorInformation = CustomActivityOnCrash.getAllErrorDetailsFromIntent(CrashActivity.this, getIntent());

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(getString(R.string.customactivityoncrash_error_activity_error_details_clipboard_label), errorInformation);
        clipboard.setPrimaryClip(clip);
    }

    /**
     * 初始化Retrofit
     */
    private void initRetrofit() {
        retrofit = new Retrofit.Builder()
                .client(initClient())
                .baseUrl("http://www.suncn.com.cn/suncnoa/ios/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    private OkHttpClient initClient() {
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggingInterceptor
                        .Builder()//构建者模式
                        .loggable(GISharedPreUtil.getBoolean(activity, "isDebugMode")) //是否开启日志打印
                        .setLevel(Level.BODY) //打印的等级
                        .log(Platform.WARN) // 打印类型
                        .request("GILogApi") // request的Tag
                        .response("GILogApi")// Response的Tag
                        .build()
                )
                .connectionPool(new ConnectionPool(8, 15, TimeUnit.SECONDS));
        return okHttpClient.build();
    }

    /**
     * 执行请求操作
     */
    private void doRequest() {
        HashMap<String, String> textBodyParamMap = new HashMap<String, String>();
        textBodyParamMap.put("websvrpwd", "suncnihold654321");
        textBodyParamMap.put("intPlatform", "20");
        textBodyParamMap.put("strModel", Build.MANUFACTURER + "（" + Build.MODEL + "）");
        textBodyParamMap.put("strRom", Build.VERSION.RELEASE);
        textBodyParamMap.put("strAppName", getText(R.string.app_name).toString());
        textBodyParamMap.put("strVersion", GIPhoneUtils.getAppVersionName(CrashActivity.this));//mDeviceCrashInfo.getProperty(VERSION_NAME));
        textBodyParamMap.put("strReport", CustomActivityOnCrash.getAllErrorDetailsFromIntent(CrashActivity.this, getIntent()));
        textBodyParamMap.put("strUserId", GISharedPreUtil.getString(activity, "strUserId") + "/" + GISharedPreUtil.getString(activity, "strName"));

        IBaseRequestApi api = retrofit.create(IBaseRequestApi.class);
        List<MultipartBody.Part> partList = new LinkedList<>();
        for (Map.Entry<String, String> entry : textBodyParamMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            partList.add(MultipartBody.Part.createFormData(key, value));
        }
        Observable<CrashReportResult> observable = api.doCrashReport(partList, textBodyParamMap);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxUtils.schedulersTransformer()) //线程调度
                .compose(RxUtils.exceptionTransformer()) // 网络错误的异常转换, 这里可以换成自己的ExceptionHandle
                // .compose(bindUntilEvent(ActivityEvent.PAUSE))
                .subscribe(new Consumer<CrashReportResult>() {
                    @Override
                    public void accept(CrashReportResult tBaseResponse) {
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }
}
