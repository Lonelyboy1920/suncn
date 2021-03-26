package com.suncn.ihold_zxztc.rxhttp.update;

import android.content.Context;

import com.gavin.giframe.http.BaseResponse;
import com.gavin.giframe.utils.GIFileCst;
import com.gavin.giframe.utils.GIFileUtil;
import com.gavin.giframe.utils.GIPhoneUtils;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suncn.ihold_zxztc.bean.AutoUpdateBean;
import com.suncn.ihold_zxztc.utils.Utils;
import com.xuexiang.xupdate.entity.DownloadEntity;
import com.xuexiang.xupdate.entity.UpdateEntity;
import com.xuexiang.xupdate.proxy.IUpdateParser;

public class MyUpdateParser implements IUpdateParser {
    private Context context;

    public MyUpdateParser(Context context) {
        this.context = context;
    }

    @Override
    public UpdateEntity parseJson(String json) {
        BaseResponse<AutoUpdateBean> baseResponse = new Gson().fromJson(json, new TypeToken<BaseResponse<AutoUpdateBean>>() {
        }.getType());
        if (baseResponse != null) {
            AutoUpdateBean result = baseResponse.getData();
            GISharedPreUtil.setValue(context, "isAppHasUpdate", result.isBlHasUpdate());
            DownloadEntity downloadEntity = new DownloadEntity();
            downloadEntity.setDownloadUrl(Utils.formatFileUrl(context, result.getStrFilePath()));// 下载地址
            downloadEntity.setCacheDir(GIFileUtil.getFileDir(context, GIFileCst.TYPE_APK)); // 文件下载的目录
            downloadEntity.setMd5(result.getStrFileMd5()); //md5值没有的话，就无法保证apk是否完整，每次都会重新下载。
            downloadEntity.setSize(result.getIntFileSize() / 1024); // 下载文件的大小
            downloadEntity.setShowNotification(true); // 是否在通知栏上显示下载进度

            if (result.getIntFileSize() == 0) {
                int nativeCode = GIPhoneUtils.getAppVersionCode(context);
                if (result.getIntVersionCode() > nativeCode) {
                    result.setBlHasUpdate(true);
                }
            }
            return new UpdateEntity()
                    .setHasUpdate(result.isBlHasUpdate()) // 是否有新版本
                    .setForce("1".equals(result.getStrForceUpdate())) // 是否强制安装：不安装无法使用app
                    .setIsIgnorable(false) // 是否可忽略该版本
                    .setVersionCode(result.getIntVersionCode()) // 最新版本code
                    .setVersionName(result.getStrVersionName()) // 最新版本名称
                    .setUpdateContent(result.getStrInfo()) // 更新内容
                    .setIsSilent(false) // 是否静默下载：有新版本时不提示直接下载
                    .setIsAutoInstall(true) // 是否下载完成后自动安装
                    .setDownLoadEntity(downloadEntity); // 下载信息实体
        }
        return null;
    }
}