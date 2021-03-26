package com.suncn.ihold_zxztc.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gavin.giframe.utils.GIDensityUtil;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIToastUtil;
import com.gavin.giframe.utils.GIUtil;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.adapter.MeetAct_Main_RVAdapter;
import com.suncn.ihold_zxztc.bean.LoginBean;
import com.suncn.ihold_zxztc.hst.FspPreferenceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class Utils {
    /**
     * 获取用于显示的Address
     * 不带手机号
     */
    public static String showAddress(String realAddress) {
        if (GIStringUtil.isNotBlank(realAddress)) {
            String result = "";
            String[] s = realAddress.split(",");
            for (String value : s) {
                value = Utils.getMailContactName(value);
//            if (!result.contains(value)) {
                result = result + value + ",";
//            }
            }
            return result.substring(0, result.length() - 1);
        }
        return "";
    }

    /**
     * 获取用于显示的Address
     * u/id/name/tel
     */
    public static String getShowAddress(String realAddress, boolean isHasPhoto) {
        if (GIStringUtil.isNotBlank(realAddress)) {
            String result;
            String[] s = realAddress.split(",");
            StringBuffer buf = new StringBuffer();
            for (String value : s) {
                //去掉最后面的手机号此时为u/id/name
                if (isHasPhoto) {
                    value = value.substring(0, value.lastIndexOf("/"));
                }
                value = Utils.getMailContactName(value);
                // result = result + value + "，";
                buf.append(value + "，");
            }
            result = buf.toString();
            if (GIStringUtil.isLastDot(result.replaceAll("，", ","))) {
                result = result;
            }
            return result.substring(0, result.length() - 1);
        }
        return "";
    }

    /**
     * 参与情况
     */
    public static String getAttendST(int intAttend) {
        switch (intAttend) {
            case 0:
                return "请假";
            case 1:
                return "出席";
            case 2:
                return "缺席";
            default:
                return "";
        }
    }

    /**
     * 根据参与情况返回固定的值
     */
    public static int getAttendSTValue(String attendState) {
        if (GIStringUtil.isNotBlank(attendState)) {
            if (attendState.equals("请假")) {
                return 0;
            } else if (attendState.equals("出席")) {
                return 1;
            } else {
                return 2;
            }
        }
            return -1;
    }

    /**
     * 格式化url，如果是相对路径则拼上服务器地址
     */
    public static String formatFileUrl(Context context, String url) {
        if (GIStringUtil.isBlank(url))
            return "";
        else if (url.contains("http://") || url.contains("https://"))
            return url;
        return getFileDomain(context.getApplicationContext()) + url;
    }


    /**
     * 获取附件路径Domain
     */
    public static String getFileDomain(Context context) {
        String server = GISharedPreUtil.getString(context, "server"); // 服务端ip
        String port = GISharedPreUtil.getString(context, "port"); // 服务端端口
        if (GIStringUtil.isBlank(server)) {
            server = context.getResources().getString(R.string.default_server);
        }
        if (GIStringUtil.isBlank(port)) {
            port = context.getResources().getString(R.string.default_port);
        }
        if (port.contains("/")) {
            if (port.indexOf("/") != 0) {
                port = ":" + port;
            }
        } else if (GIStringUtil.isNumeric(port)) {
            port = ":" + port;
        } else {
            port = "/" + port;
        }
        return DefineUtil.PROTOCOL + server + port + "/";
    }

    /**
     * 从最后一个“/”字符截取字符串
     */
    public static String getMailContactName(String str) {
        if (str != null)
            return str.substring(str.lastIndexOf("/") + 1, str.length());
        return "";
    }

    /**
     * DEBUG模式切换
     */
    public static void switchDebugMode(Context context, boolean isTrue) {
        if (DefineUtil.IS_OPEN_DEBUG) {
            isTrue = true;
        }
        GILogUtil.init(isTrue); //是否开启打印日志
        GISharedPreUtil.setValue(context, "isDebugMode", isTrue);
        ApiManager.refreshRetrofit();
    }

    public static List<MultipartBody.Part> filesToMultipartBodyParts(List<File> files) {
        List<MultipartBody.Part> parts = new LinkedList<>();
        if (files.size() > 0) {
            for (File file : files) {
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
                parts.add(part);
            }
        } else {
            parts.add(MultipartBody.Part.createFormData("", ""));
        }
        return parts;
    }

    public static String getShowContent(String a, String b) {
        String c = GIStringUtil.isNotBlank(a) ? a : GIStringUtil.isNotBlank(b) ? b : "";
        return c;
    }

    /**
     * 设置语音参数
     */
    public static void setMikeParam(SpeechRecognizer mIat, boolean isShowDot) {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);
        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        // 设置语言
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");
        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        if (isShowDot) {
            mIat.setParameter(SpeechConstant.ASR_PTT, "0");
        } else {
            mIat.setParameter(SpeechConstant.ASR_PTT, "1");
        }
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
        // 设置听写结果是否结果动态修正，为“1”则在听写过程中动态递增地返回结果，否则只在听写结束之后返回最终结果
        // 注：该参数暂时只对在线听写有效
        mIat.setParameter(SpeechConstant.ASR_DWA, "0");
    }

    /**
     * 参数设置
     */
    public static void setPlayParam(SpeechSynthesizer mTts) {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        //支持实时音频返回，仅在synthesizeToUri条件下支持
        //mTts.setParameter(SpeechConstant.TTS_DATA_NOTIFY, "1");
        // 设置在线合成发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, "vixy");
        //设置合成语速
        mTts.setParameter(SpeechConstant.SPEED, "50");
        //设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, "50");
        //设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, "50");
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
//        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "pcm");
//        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.pcm");
    }


    /**
     * 初始化EasyRecyclerView相关配置
     */
    public static void initEasyRecyclerView(Context context, RecyclerView recyclerView, boolean isDrawLastItem, boolean isDrawHeaderFooter, int lineColor, float lineHeight, float padding) {
        int myPadding = GIDensityUtil.dip2px(context, padding);
        //颜色 & 高度 & 左边距 & 右边距
        DividerDecoration itemDecoration = new DividerDecoration(context.getResources().getColor(lineColor), GIDensityUtil.dip2px(context, lineHeight), myPadding, myPadding);
        itemDecoration.setDrawLastItem(isDrawLastItem);//设置最后一个item有分割线,默认true.
        itemDecoration.setDrawHeaderFooter(isDrawHeaderFooter);//设置分割线是否对Header和Footer有效,默认false.
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(context)); //给ERV添加布局管理器
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER); // 去掉滚动时头部/底部的边界阴影
    }

    /**
     * 设置列表数据返回刷新控件显示
     */
    public static void setLoadMoreViewState(int AllCount, int curCount, String strKeyValue, SmartRefreshLayout refreshLayout, BaseQuickAdapter adapter) {
        refreshLayout.finishLoadMore();
        refreshLayout.finishRefresh();
        /* 有搜索功能时需添加如下代码 */
        if (GIStringUtil.isNotBlank(strKeyValue)) {
            adapter.setEmptyView(R.layout.view_erv_empty_search); // 设置无数据时的view（搜索）
        } else {
            adapter.setEmptyView(R.layout.view_erv_empty); // 设置无数据时的view
        }
        if (AllCount > curCount) {
            refreshLayout.setNoMoreData(false);
        } else {
            refreshLayout.setNoMoreData(true);
        }
        if (AllCount == 0) {
            refreshLayout.setEnableLoadMore(false);
        } else {
            refreshLayout.setEnableLoadMore(true);
        }
    }

    /**
     * 设置列表数据返回刷新控件显示
     */
    public static void setLoadMoreViewStateHasHeader(int AllCount, int curCount, String strKeyValue, SmartRefreshLayout refreshLayout, BaseQuickAdapter adapter) {
        refreshLayout.finishLoadMore();
        refreshLayout.finishRefresh();
        if (AllCount > curCount) {
            refreshLayout.setNoMoreData(false);
        } else {
            refreshLayout.setNoMoreData(true);
        }
        if (AllCount == 0) {
            refreshLayout.setEnableLoadMore(false);
        } else {
            refreshLayout.setEnableLoadMore(true);
        }
    }

    /**
     * 是否安装某app
     *
     * @param packageName 指定app包名
     */
    public static boolean isAvilible(Context context, String packageName) {
        // 获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        // 用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        // 从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        // 判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }

    /**
     * 打开第三方地图
     *
     * @param address 地址
     */
    public static void openLocalMap(Context context, String address) {
        if (Utils.isAvilible(context, "com.baidu.BaiduMap")) {
            try {
                Intent i1 = new Intent();
                i1.setData(Uri.parse("baidumap://map/geocoder?src=openApiDemo&address=" + address));
                context.startActivity(i1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (Utils.isAvilible(context, "com.autonavi.minimap")) {
            try {
                String act = "android.intent.action.VIEW";
                String dat = "androidamap://keywordNavi?sourceApplication=softname&keyword=" + address + " &style=2";
                String pkg = "com.autonavi.minimap";
                Intent intent = new Intent(act, Uri.parse(dat));
                intent.setPackage(pkg);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            GIToastUtil.showMessage(context, "您尚未安装百度地图或高德地图");
        }
    }

    /**
     * 设置注销登录后的存储信息
     */
    public static void sp_loginOut(Activity activity) {
        // GISharedPreUtil.setValue(activity, "RMBPWD", false);
        GISharedPreUtil.setValue(activity, "isOpenFingerLogin_" + GISharedPreUtil.getString(activity, "strUserId"), false);
        //GISharedPreUtil.setValue(activity, "Checked", false);

        LoginBean loginBean = new LoginBean();
        loginBean.setStrUserId(GISharedPreUtil.getString(activity, "strVisitorUserId")); // 替换成游客模式的strUserId
        loginBean.setStrUserName(GISharedPreUtil.getString(activity, "strVisitorName")); // 替换成游客模式的strName
        loginBean.setStrPathUrl(GISharedPreUtil.getString(activity, "strVisitorPhotoUrl")); // 替换成游客模式的strPhotoUrl
        LoginBean.AppInitData appInitData = new LoginBean().new AppInitData();
        appInitData.setStrAppHomeCentreFile(GISharedPreUtil.getString(activity, "strAppHomeCentreFile"));
        appInitData.setStrAppHomeCentreFileUrl(GISharedPreUtil.getString(activity, "strAppHomeCentreFileUrl"));
        appInitData.setStrAppHomeTop(GISharedPreUtil.getString(activity, "strAppHomeTop"));
        appInitData.setStrAppName(GISharedPreUtil.getString(activity, "strAppName"));
        loginBean.setStrAppStartFile(GISharedPreUtil.getString(activity, "strSplashUrl"));
        GISharedPreUtil.setValue(activity, "QDInitSuccess", false);
        loginBean.setAppInit(appInitData); // 游客模式也读取运营配置数据
        Utils.sp_loginIn(activity, loginBean);
    }

    /**
     * 设置登录后的存储信息
     */
    public static void sp_loginIn(Activity activity, LoginBean loginBean) {
        GISharedPreUtil.setValue(activity, "isCheckUpdate", true);
        GISharedPreUtil.setValue(activity, "strUserId", loginBean.getStrUserId()); // 用户唯一ID
        GISharedPreUtil.setValue(activity, "strPhotoUrl", Utils.formatFileUrl(activity, loginBean.getStrPhotoUrl())); // 登录用户头像地址
        GISharedPreUtil.setValue(activity, "strWebUrl", loginBean.getStrWebUrl()); // 门户信息的服务地址
        GISharedPreUtil.setValue(activity, "strLoginUserId", loginBean.getStrLoginUserId()); // 如：dinghong
        GISharedPreUtil.setValue(activity, "strDefalutUserId", loginBean.getStrDefalutUserId());
        GISharedPreUtil.setValue(activity, "strShowLzIconState", loginBean.getStrShowLzIconState());
        GISharedPreUtil.setValue(activity, "strLoginHaiBiAccessToken", loginBean.getStrLoginHaiBiAccessToken());
        GISharedPreUtil.setValue(activity, "strIsCollective", loginBean.getStrIsCollective());
        GISharedPreUtil.setValue(activity, "strTitle", loginBean.getStrTitle());
        String strSid = loginBean.getStrSid();
        GISharedPreUtil.setValue(activity, "strSid", strSid);
        GISharedPreUtil.setValue(activity, "strName", loginBean.getStrName());
        if (GIStringUtil.isBlank(strSid)) { // 游客
            GISharedPreUtil.setValue(activity, "isHasLogin", false);
            GISharedPreUtil.setValue(activity, "strVisitorName", loginBean.getStrUserName());
            GISharedPreUtil.setValue(activity, "strVisitorUserId", loginBean.getStrUserId());
            GISharedPreUtil.setValue(activity, "strVisitorPhotoUrl", Utils.formatFileUrl(activity, loginBean.getStrPhotoUrl()));
        } else {
            GISharedPreUtil.setValue(activity, "isHasLogin", true);
        }
        if (GIStringUtil.isNotBlank(loginBean.getStrRootIp())) {
            GISharedPreUtil.setValue(activity, "server", loginBean.getStrRootIp());
            GISharedPreUtil.setValue(activity, "port", loginBean.getStrRootPort());
            ApiManager.refreshRetrofit();
        }
        GISharedPreUtil.setValue(activity, "strMsg", loginBean.getStrMsg());
        GISharedPreUtil.setValue(activity, "strPubUnitId", loginBean.getStrPubUnitId());
        GISharedPreUtil.setValue(activity, "intUserRole", loginBean.getIntUserRole());
        GISharedPreUtil.setValue(activity, "strUnitId", loginBean.getStrUnitId());
        GISharedPreUtil.setValue(activity, "strMobile", loginBean.getStrMobile());
        GISharedPreUtil.setValue(activity, "strSector", loginBean.getStrSector());
        GISharedPreUtil.setValue(activity, "strFaction", loginBean.getStrFaction());
        GISharedPreUtil.setValue(activity, "strDuty", loginBean.getStrDuty());
        GISharedPreUtil.setValue(activity, "intContactRole", loginBean.getIntContactRole());
        GISharedPreUtil.setValue(activity, "strLoginUserType", GIStringUtil.nullToEmpty(loginBean.getStrLoginUserType()));
        GISharedPreUtil.setValue(activity, "intIdentity", loginBean.getIntIdentity());
        GISharedPreUtil.setValue(activity, "strProjectName", loginBean.getStrProjectName());
        GISharedPreUtil.setValue(activity, "strLoginUserId", loginBean.getStrLoginUserId());
        GISharedPreUtil.setValue(activity, "intAdmin", loginBean.getIntAdmin());
        GISharedPreUtil.setValue(activity, "intGroup", loginBean.getIntGroup());
        GISharedPreUtil.setValue(activity, "strShowRootUnit", loginBean.getStrShowRootUnit());
        GISharedPreUtil.setValue(activity, "strDefalutRootUnitName", loginBean.getStrDefalutRootUnitName());
        GISharedPreUtil.setValue(activity, "strDefalutRootUnitId", loginBean.getStrDefalutRootUnitId());
        GISharedPreUtil.setValue(activity, "strMotionTitle", loginBean.getStrMotionTitle());
        GISharedPreUtil.setValue(activity, "strNewsDelStateByAdmin", loginBean.getStrNewsDelStateByAdmin());
        GISharedPreUtil.setValue(activity, "strCircleDelStateByAdmin", loginBean.getStrCircleDelStateByAdmin());
        GISharedPreUtil.setValue(activity, "isExistUserByCppccSession", loginBean.getIsExistUserByCppccSession());

        /* APP系统功能配置信息 */
        LoginBean.AppConfig appConfig = loginBean.getCppccAppConfig();
        if (appConfig == null) {
            appConfig = new LoginBean().new AppConfig(); // 用来清除之前存储的信息
        }
        GISharedPreUtil.setValue(activity, "strContentType", appConfig.getStrContentType());
        GISharedPreUtil.setValue(activity, "strContentTitle1", appConfig.getStrContentTitle1());
        GISharedPreUtil.setValue(activity, "strContentTitle2", appConfig.getStrContentTitle2());
        GISharedPreUtil.setValue(activity, "strFlowType", appConfig.getStrFlowType());
        GISharedPreUtil.setValue(activity, "strUseCaseNo", appConfig.getStrUseCaseNo());
        GISharedPreUtil.setValue(activity, "strUseCbdw", appConfig.getStrUseCbdw());
        GISharedPreUtil.setValue(activity, "strUseCbxt", appConfig.getStrUseCbxt());
        GISharedPreUtil.setValue(activity, "strShowDygc", appConfig.getStrShowDygc());
        GISharedPreUtil.setValue(activity, "strShowXsfs", appConfig.getStrShowXsfs());
        GISharedPreUtil.setValue(activity, "strShowTjcbdw", appConfig.getStrShowTjcbdw());
        GISharedPreUtil.setValue(activity, "strFlowShowCbdwPre", appConfig.getStrFlowShowCbdwPre());
        GISharedPreUtil.setValue(activity, "strFlowCaseDistType", appConfig.getStrFlowCaseDistType());
        GISharedPreUtil.setValue(activity, "strUseInfoDist", appConfig.getStrUseInfoDist());
        GISharedPreUtil.setValue(activity, "strUseQDVideo", appConfig.getStrUseQDVideo());
        GISharedPreUtil.setValue(activity, "strUseQDIM", appConfig.getStrUseQDIM());
        GISharedPreUtil.setValue(activity, "strUseMobileBook", appConfig.getStrUseMobileBook());
        GISharedPreUtil.setValue(activity, "strUseMemberExam", appConfig.getStrUseMemberExam());
        GISharedPreUtil.setValue(activity, "strUseSupportMotion", appConfig.getStrUseSupportMotion());
        GISharedPreUtil.setValue(activity, "strUseAllyMotion", appConfig.getStrUseAllyMotion());
        GISharedPreUtil.setValue(activity, "strSubmitFlow", appConfig.getStrSubmitFlow());
        GISharedPreUtil.setValue(activity, "strUseSaoMQianD", appConfig.getStrUseSaoMQianD());
        GISharedPreUtil.setValue(activity, "strUseCbdwBack", appConfig.getStrUseCbdwBack());
        GISharedPreUtil.setValue(activity, "strUseYouMeng", appConfig.getStrUseYouMeng());
        GISharedPreUtil.setValue(activity, "strShowDutyCard", appConfig.getStrShowDutyCard());
        GISharedPreUtil.setValue(activity, "strShowAnAn", appConfig.getStrShowAnAn());
        GISharedPreUtil.setValue(activity, "strUseHST", appConfig.getStrUseHST());
        /* APP个性化配置信息 */
        LoginBean.AppInitData objAppInit = loginBean.getAppInit();
        if (objAppInit == null) {
            objAppInit = new LoginBean().new AppInitData(); // 用来清除之前存储的信息
        }
        GISharedPreUtil.setValue(activity, "strAppHomeCentreFileUrl", objAppInit.getStrAppHomeCentreFileUrl());
        GISharedPreUtil.setValue(activity, "strAppHomeCentreFile", objAppInit.getStrAppHomeCentreFile());

        if ("1".equals(loginBean.getHasHeadConfig())) {
            GISharedPreUtil.setValue(activity, "strAppHomeTop", loginBean.getStrBackUrl());
            GISharedPreUtil.setValue(activity, "strAppName", loginBean.getStrTitleUrl());
        } else {
            GISharedPreUtil.setValue(activity, "strAppHomeTop", "");
            GISharedPreUtil.setValue(activity, "strAppName", "");
        }
        GISharedPreUtil.setValue(activity, "strSplashUrl", loginBean.getStrAppStartFile());
        /* 企达数据 */
        GISharedPreUtil.setValue(activity, "QDInitSuccess", false); // 每次启动APP需重置企达的激活状态
        LoginBean.OpenfireConfigBean objQDData = loginBean.getObjQDData();
        if (objQDData == null) {
            objQDData = new LoginBean().new OpenfireConfigBean(); // 用来清除之前存储的信息
        }
        GISharedPreUtil.setValue(activity, "QDUserName", objQDData.getStrQDUserName());
        GISharedPreUtil.setValue(activity, "QDPWD", objQDData.getStrQDUserPassword());
        GISharedPreUtil.setValue(activity, "QDMAINSERVER", objQDData.getStrQDMAINSERVER());
        GISharedPreUtil.setValue(activity, "QDMAINSERVERPORT", objQDData.getStrQDMAINSERVERPORT());

        /* 安安 */
        LoginBean.AnData objAnData = loginBean.getObjAn();
        if (objAnData == null) {
            objAnData = new LoginBean().new AnData(); // 用来清除之前存储的信息
        }
        GISharedPreUtil.setValue(activity, "strAnAnTitle", objAnData.getStrTitle());
        GISharedPreUtil.setValue(activity, "strAnAnIds", objAnData.getStrIds());
        /*好视通数据*/
        LoginBean.objHst objHst = loginBean.getObjHSTData();
        if (objHst == null) {
            objHst = new LoginBean().new objHst();
        }
        FspPreferenceManager.getInstance().setAppId(objHst.getStrHSTAppId()).commit();
        FspPreferenceManager.getInstance().setAppSecret(objHst.getStrHSTAppSecret()).commit();
        FspPreferenceManager.getInstance().setAppServerAddr(objHst.getStrHSTAppServerUrl()).commit();
    }

    /**
     * 获取string.xml中的字符串
     *
     * @param type     1-追加“请输入”字样；1-追加“请选择”字样
     * @param stringId 资源ID
     */
    public static String getMyString(int type, int stringId) {
        String s = "";
        switch (type) {
            case 1: // 请输入
                s = GIUtil.getContext().getString(R.string.string_please_input);
                break;
            case 2: // 请选择
                s = GIUtil.getContext().getString(R.string.string_please_select);
                break;
            default:
                break;
        }
        return s + GIUtil.getContext().getString(stringId);
    }

    /**
     * 获取string.xml中的字符串
     *
     * @param stringId 资源ID
     */
    public static String getMyString(int stringId) {
        return getMyString(0, stringId);
    }
}