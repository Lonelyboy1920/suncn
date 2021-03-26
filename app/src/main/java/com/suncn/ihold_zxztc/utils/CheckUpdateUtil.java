package com.suncn.ihold_zxztc.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceEnter;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.ZoomExit.ZoomOutExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.gavin.giframe.service.GIDownloadService;
import com.gavin.giframe.utils.GIPhoneUtils;
import com.gavin.giframe.utils.GIToastUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.AutoUpdateBean;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;

/**
 * 检查更新Util
 */
public class CheckUpdateUtil {
    private Activity mContext;

    public CheckUpdateUtil(Activity context, AutoUpdateBean autoUpdate, int sign) {
        mContext = context;

        String versionName = autoUpdate.getStrVersionName();
        int nativeCode = GIPhoneUtils.getAppVersionCode(context);
        int versionCode = autoUpdate.getIntVersionCode();
        String filePath = autoUpdate.getStrFilePath();
        String strInfo = autoUpdate.getStrInfo();

        if (filePath != null) {
            if (versionCode > nativeCode) {
                doNewVersionUpdate(filePath, versionName, strInfo);
//                if (sign == 0) // 登录时自动检测更新
//                    GISharedPreUtil.setValue(mContext, "AUTOLOGIN", false);
            } else {
                if (sign == 1) // 用户手动触发的更新事件
                    notNewVersionShow();
            }
        }
    }

    /**
     * 没有新版本
     */
    private void notNewVersionShow() {
        GIToastUtil.showMessage(mContext, "当前已是最新版本");
    }

    /**
     * 检测到新版本
     */
    private void doNewVersionUpdate(final String url, final String versionName, String strInfo) {
        BaseAnimatorSet mBasIn = new BounceTopEnter();
        BaseAnimatorSet mBasOut = new BounceTopEnter();
        try {
            mBasIn = BounceEnter.class.newInstance();
            mBasOut = ZoomOutExit.class.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final MaterialDialog dialog = new MaterialDialog(mContext);
        dialog.title("检查更新").content("发现新版本，更新内容如下：\n" + strInfo + "\n是否更新？").btnText("暂不更新", "更新").showAnim(mBasIn).dismissAnim(mBasOut).show();
        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        DefineUtil.isUpdate = false;
                        dialog.dismiss();
                    }
                }, new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        //请求授权
                        HiPermission.create(mContext).checkSinglePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
                            @Override
                            public void onGuarantee(String permisson, int position) { // 同意/已授权
                                DefineUtil.isUpdate = true;
                                dialog.dismiss();
                                Intent intent = new Intent(mContext, GIDownloadService.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("url", Utils.formatFileUrl(mContext, url));
                                bundle.putString("filename", mContext.getText(R.string.app_name) + "_" + versionName + ".apk");
                                bundle.putInt("smallIcon", R.mipmap.ic_launcher);
                                intent.putExtras(bundle);
                                mContext.startService(intent);
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
                    }
                }
        );
    }
}
