package com.suncn.ihold_zxztc.activity.hot;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.gavin.giframe.interceptor.logging.Level;
import com.gavin.giframe.interceptor.logging.LoggingInterceptor;
import com.gavin.giframe.utils.GIKeyboardUtil;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.RobotListViewAdapter;
import com.suncn.ihold_zxztc.bean.BroadcastListBean;
import com.suncn.ihold_zxztc.bean.RobotBean;
import com.suncn.ihold_zxztc.databinding.ActivityRobotBinding;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.utils.XfJsonParserUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.internal.platform.Platform;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 机器人助手
 */
public class RobotActivity extends BaseActivity {
    private ActivityRobotBinding binding;
    private Retrofit retrofit;
    private RobotBean robotBean;
    private RobotListViewAdapter adapter;

    private SpeechRecognizer mIat; // 语音听写对象
    public SpeechSynthesizer mTts; // 语音合成对象
    public boolean isPlayNews;
    private ArrayList<BroadcastListBean.NewsInfo> leaderList = new ArrayList<>();//猜你想问列表
    private ArrayList<BroadcastListBean.NewsInfo> maybeList = new ArrayList<>();//您是否想问列表
    private ArrayList<BroadcastListBean.NewsInfo> newList;//语音播报列表
    private SimpleTextAdapter simpleTextAdapter;
    public int currentIndex = 1;//猜你想问页数
    private int lastBrocastPosion = -1;//上次语音播报组件的位置
    //定位相关
    private AMapLocationClientOption mLocationOption;
    private String strCityName = "";
    private String strCityCode = "";
    private AMapLocationClient mLocationClient;

    @Override
    public void setRootView() {
        isShowBackBtn = true;
//        GIDensityUtil.initStatusBar(activity);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_robot);
        binding.setViewModel(this);
    }

    public ArrayList<BroadcastListBean.NewsInfo> getLeaderList() {
        return leaderList;
    }

    public ArrayList<BroadcastListBean.NewsInfo> getMaybeList() {
        return maybeList;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mLocationClient != null) {
            mLocationClient.startLocation(); // 启动定位
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLocationClient != null) {
            mLocationClient.stopLocation();//停止定位
        }
    }

    @Override
    protected void onStop() {
        if (mLocationClient != null) {
            mLocationClient.onDestroy();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mIat) {
            mIat.cancel();
            mIat.destroy();
        }
        if (null != mTts) {
            mTts.stopSpeaking();
            mTts.destroy();
        }
    }

    /**
     * 初始化定位并设置定位回调监听
     */
    private void getCurrentLocationLatLng() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(3500);
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    @Override
    public void initData() {
        super.initData();
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        setHeadTitle(getString(R.string.string_intelligent_assistant));
        initRetrofit();
        mIat = SpeechRecognizer.createRecognizer(this, mInitListener);//使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener); // 初始化合成对象
        adapter = new RobotListViewAdapter(activity);
        binding.recyclerView.setAdapter(adapter);
        findViewById(R.id.view_place).setVisibility(View.GONE);
        //请求授权
        HiPermission.create(activity).checkSinglePermission(Manifest.permission.ACCESS_FINE_LOCATION, new PermissionCallback() {
            @Override
            public void onGuarantee(String permisson, int position) { // 同意/已授权
                prgDialog.closePrgDialog();
                //locationService.start();// 定位SDK
                getCurrentLocationLatLng();
                mLocationClient.startLocation();
                getLeaderListData();
            }

            @Override
            public void onClose() { // 用户关闭权限申请
                GILogUtil.i("onClose");
                prgDialog.closePrgDialog();
            }

            @Override
            public void onFinish() { // 所有权限申请完成
                GILogUtil.i("onFinish");

            }

            @Override
            public void onDeny(String permisson, int position) { // 拒绝
                GILogUtil.i("onDeny");
                prgDialog.closePrgDialog();
            }
        });
    }

    public void getLeaderListData() {
        textParamMap = new HashMap<>();
        if (!GISharedPreUtil.getBoolean(activity, "isHasLogin")) {
            textParamMap.put("strUserId", GISharedPreUtil.getString(activity, "strUserId"));
        }
        textParamMap.put("intCurPage", currentIndex + "");
        textParamMap.put("intPageSize", "3");
        doRequestNormal(ApiManager.getInstance().KnowledgeQuestionAServlet(textParamMap), 0);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_center: // 录入语音
                HiPermission.create(activity).checkSinglePermission(Manifest.permission.RECORD_AUDIO, new PermissionCallback() {
                    @Override
                    public void onGuarantee(String permisson, int position) { // 同意/已授权
                        if (!binding.ripple.isRippleAnimationRunning()) {
                            binding.ivCenter.setImageResource(R.mipmap.icon_center);
                            if (null == mIat) {
                                showToast(getString(R.string.string_voice_assistant_failed_to_initialize));
                                return;
                            }
                            Utils.setMikeParam(mIat, true); // 设置参数
                            // 不显示听写对话框
                            int code = mIat.startListening(mRecognizerListener);
                            if (code != ErrorCode.SUCCESS) {
                                GILogUtil.i("听写失败,错误码: " + code);
                                showToast(getString(R.string.string_dictation_failed));
                            }
                        }
                    }

                    @Override
                    public void onClose() { // 用户关闭权限申请

                    }

                    @Override
                    public void onFinish() { // 所有权限申请完成

                    }

                    @Override
                    public void onDeny(String permisson, int position) { // 拒绝

                    }
                });
                break;
            case R.id.tv_switchInput: // 切换到输入模式
                binding.llInput.setVisibility(View.VISIBLE);
                binding.rlMike.setVisibility(View.GONE);
                GIKeyboardUtil.showKeyboard(binding.etContent, true);
                binding.etContent.requestFocus();
                break;
            case R.id.tv_switchMike: // 切换到语音模式
                GIKeyboardUtil.hideKeyboard(binding.etContent);
                binding.llInput.setVisibility(View.GONE);
                binding.rlMike.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_send: // 软键盘输入发送按钮
                String value = binding.etContent.getText().toString().trim();
                if (GIStringUtil.isNotBlank(value)) {
                    setRequestData(value);
                }
                break;
        }
    }

    /**
     * 初始化Retrofit
     */
    private void initRetrofit() {
        retrofit = new Retrofit.Builder()
                .client(initClient())
                .baseUrl("http://openapi.tuling123.com/")
                .addConverterFactory(GsonConverterFactory.create())
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
     * 生成请求数据
     */
    public void setRequestData(String content) {
        getPlayNews(content);
        robotBean = new RobotBean(1, content);
        adapter.getRobotBeans().add(robotBean);
        adapter.notifyDataSetChanged();
    }


    /**
     * 安安答复内容展示
     */
    private void showAnAnContent(String content) {
        speakText(false, content);
        robotBean = new RobotBean(0, content);
        adapter.getRobotBeans().add(robotBean);
        adapter.notifyDataSetChanged();
    }

    /**
     * 安安答复内容展示
     */
    private void showAnAnContentMaybe(String content) {
        speakText(false, content);
        robotBean = new RobotBean(4, content);
        robotBean.setList(maybeList);
        adapter.getRobotBeans().add(robotBean);
        adapter.notifyDataSetChanged();
    }

    /**
     * 安安答复内容展示
     */
    private void showAnAnContentNews(String content) {
        speakText(true, content);
        robotBean = new RobotBean(3, content);
        //每次都只显示一个播放的组件，所以需要把之前的移除
        if (lastBrocastPosion != -1) {
            adapter.getRobotBeans().remove(lastBrocastPosion);
        }
        adapter.getRobotBeans().add(robotBean);
        lastBrocastPosion = adapter.getRobotBeans().indexOf(robotBean);
        adapter.setPosition(adapter.getRobotBeans().indexOf(robotBean));
        adapter.notifyDataSetChanged();
    }


    /**
     * 新闻播报的信息
     */
    private void getPlayNews(String content) {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        if (!GISharedPreUtil.getBoolean(activity, "isHasLogin")) {
            textParamMap.put("strUserId", GISharedPreUtil.getString(activity, "strUserId"));
        }
        if (content.contains("天气")) {
            textParamMap.put("strCityName", strCityName);
            textParamMap.put("strCityCode", strCityCode);
        }
        textParamMap.put("strKeyValue", content);
        doRequestNormal(ApiManager.getInstance().getPlayNews(textParamMap), 1);
    }

    private void doLogic(Object data, int sign) {
        String toastMessage = null;
        prgDialog.closePrgDialog();
        try {
            switch (sign) {
                case 0:
                    BroadcastListBean result1 = (BroadcastListBean) data;
                    leaderList = result1.getObjList();
                    if (simpleTextAdapter == null) {
                        simpleTextAdapter = new SimpleTextAdapter();
                    }
                    simpleTextAdapter.notifyDataSetChanged();
                    //在后台取cutpage，因为这里要循环的取数据
                    currentIndex = result1.getIntCurPage();
                    if (adapter.getCount() == 0) {
                        showAnAnContent(getString(R.string.string_hi_this_is_an_an_what_can_i_do_for_you));
                    }
                    break;
                case 1:
                    BroadcastListBean result = (BroadcastListBean) data;
                    ArrayList<BroadcastListBean.NewsInfo> newsInfoList = result.getObjList();
                    maybeList = new ArrayList<>();
                    if (result.getIntType() == 0) {//是新闻资讯类进行语音播报
                        if (newsInfoList != null && newsInfoList.size() > 0) {
                            String newsInfo = "";
                            for (int i = 0; i < newsInfoList.size(); i++) {
                                newsInfo = newsInfo + newsInfoList.get(i).getStrTitle();
                            }
                            adapter.setNewsInfos(newsInfoList);
                            showAnAnContentNews(newsInfo);
                            // speakText(true, newsInfo.toString());
                        } else {
                            showAnAnContent(getString(R.string.string_no_hot_news_today));
                        }
                    } else if (result.getIntType() == 2) {
                        maybeList = result.getObjList();
                        if (maybeList != null && maybeList.size() > 0) {
                            String newsInfo = "";
                            for (int i = 0; i < maybeList.size(); i++) {
                                newsInfo = newsInfo + newsInfoList.get(i).getStrTitle();
                            }
                            showAnAnContentMaybe(getString(R.string.string_do_you_want_to_ask) + newsInfo);
                        }
                        //adapter.notifyDataSetChanged();
                    } else {
                        if (newsInfoList != null && newsInfoList.size() > 0) {
                            String newsInfo = "";
                            for (int i = 0; i < newsInfoList.size(); i++) {
                                newsInfo = newsInfo + newsInfoList.get(i).getStrTitle();
                            }
                            showAnAnContent(newsInfo);
                        }
                    }
                    binding.recyclerView.smoothScrollToPosition(adapter.getCount() - 1);
                    binding.etContent.setText("");
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            toastMessage = getString(R.string.data_error);
        }
        if (GIStringUtil.isNotEmpty(toastMessage))
            showToast(toastMessage);
    }

    /**
     * 读出图灵机器人说的话的文本
     */
    public void speakText(boolean isPlayNews, String content) {
        this.isPlayNews = isPlayNews;
        Utils.setPlayParam(mTts);  // 设置参数
        int code = mTts.startSpeaking(content, mTtsListener);
        if (code != ErrorCode.SUCCESS) {
            GILogUtil.i("语音合成失败,错误码: " + code);
            showToast(getString(R.string.string_speech_synthesis_failed));
        }
    }

    /**
     * 初始化监听。
     */
    public InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                GILogUtil.i("语音合成初始化失败,错误码: " + code);
                showToast(getString(R.string.string_speech_synthesis_failed_to_initialize));
            }
        }
    };

    /**
     * 合成回调监听。
     */
    public SynthesizerListener mTtsListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() { // 开始播放
        }

        @Override
        public void onSpeakPaused() { // 暂停播放
        }

        @Override
        public void onSpeakResumed() { // 继续播放
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) { // 合成进度
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) { // 播放进度
            if (isPlayNews) {
                adapter.getSeekBar().setProgress(percent);
            } else if (adapter.getSeekBar() != null && adapter.getSeekBar().getProgress() != 0) {//如果之前播放过新闻，现在播放的是普通文本，那就把新闻的播放状态重置
                adapter.getSeekBar().setProgress(0);
            }
        }

        @Override
        public void onCompleted(SpeechError error) { // 播放完成
            if (error != null) {
                showToast(error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }
    };


    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                GILogUtil.i("语音识别初始化失败,错误码: " + code);
                showToast(getString(R.string.string_voice_recognition_failed_to_initialize));
            }
        }
    };

    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {
        private StringBuffer buffer = new StringBuffer();

        @Override
        public void onBeginOfSpeech() { // 开始说话");
            buffer = new StringBuffer();
            robotBean = new RobotBean(1, "");
            adapter.getRobotBeans().add(robotBean);
            adapter.notifyDataSetChanged();
            binding.ripple.startRippleAnimation();
        }

        @Override
        public void onError(SpeechError error) {
            showToast(error.getPlainDescription(true));
        }

        @Override
        public void onEndOfSpeech() {// 结束说话
            binding.ripple.stopRippleAnimation();
            robotBean = (RobotBean) adapter.getItem(adapter.getCount() - 1);
            GILogUtil.i("onEndOfSpeech#说话内容----->" + robotBean.getContent());
            if (GIStringUtil.isBlank(robotBean.getContent())) {
                adapter.getRobotBeans().remove(robotBean);
                adapter.notifyDataSetChanged();
            } else {
                adapter.getRobotBeans().remove(robotBean);
                adapter.notifyDataSetChanged();
                setRequestData(robotBean.getContent());
            }
            binding.ivCenter.setImageResource(R.mipmap.icon_center_press);
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            String text = XfJsonParserUtil.parseIatResult(results.getResultString());
            buffer.append(text);
            robotBean = new RobotBean(1, buffer.toString());
            adapter.getRobotBeans().set(adapter.getCount() - 1, robotBean);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
//            GILogUtil.i("当前正在说话，音量大小：" + volume);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }
    };

    public SimpleTextAdapter getSimpleTextAdapter() {
        return simpleTextAdapter;
    }

    /**
     * 猜你想问adapter
     */
    class SimpleTextAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            if (leaderList == null) {
                return 0;
            }
            return leaderList.size();
        }


        @Override
        public Object getItem(int position) {
            return leaderList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_simple_text, null);
            TextView textView = convertView.findViewById(R.id.tv_text);
            TextView tvIcon = convertView.findViewById(R.id.tv_icon);
            textView.setText(leaderList.get(position).getStrTitle());
            tvIcon.setVisibility(View.GONE);
            return convertView;
        }
    }

    private AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    strCityName = aMapLocation.getProvince() + aMapLocation.getCity() + aMapLocation.getDistrict();
                    strCityCode = aMapLocation.getAdCode();
                } else {
                    GILogUtil.e("定位失败=======" + aMapLocation.getErrorInfo());
                    GILogUtil.e("定位失败=======" + aMapLocation.getErrorCode());
                }
            }
        }
    };

}
