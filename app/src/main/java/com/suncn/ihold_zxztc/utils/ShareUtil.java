package com.suncn.ihold_zxztc.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.gavin.giframe.utils.GIToastUtil;
import com.share.util.ShareDialog;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.io.File;

/**
 * 分享工具类
 */
public class ShareUtil {

    public static void showDialog(Activity activity, String title, String description, String thumb, String webUrl, String[] titles) {
        new ShareDialog.Builder(activity).setDialogList(titles).setDialogTitle("分享到").createOrShareData(shareUrl(activity, title, description, thumb, webUrl)).show();
    }


    /**
     * 分享文字
     */
    public static void showLoadText(Activity activity, String withText) {
        new ShareDialog.Builder(activity).setOnShareClickListener(new ShareDialog.OnShareClickListener() {
            @Override
            public void onShareClick(int resultType, com.umeng.socialize.bean.SHARE_MEDIA platform, Throwable t) {
                onShareClickResult(activity, resultType, platform, t);
            }
        }).createOrShareData(withText).show();
    }


    /**
     * 分享图片
     */
    public static void showLoadImg(Activity activity, String imgUrl) {
        new ShareDialog.Builder(activity).setOnShareClickListener(new ShareDialog.OnShareClickListener() {
            @Override
            public void onShareClick(int resultType, com.umeng.socialize.bean.SHARE_MEDIA platform, Throwable t) {
                onShareClickResult(activity, resultType, platform, t);
            }
        }).createOrShareData(shareImg(activity, imgUrl)).show();
    }

    /**
     * 分享图片
     */
    public static void showLoadImg(Activity activity, File file) {
        new ShareDialog.Builder(activity).setOnShareClickListener(new ShareDialog.OnShareClickListener() {
            @Override
            public void onShareClick(int resultType, com.umeng.socialize.bean.SHARE_MEDIA platform, Throwable t) {
                onShareClickResult(activity, resultType, platform, t);
            }
        }).createOrShareData(shareImg(activity, file)).show();
    }

    /**
     * 分享图片
     */
    public static void showLoadImg(Activity activity, Bitmap bitmap) {
        new ShareDialog.Builder(activity).setOnShareClickListener(new ShareDialog.OnShareClickListener() {
            @Override
            public void onShareClick(int resultType, com.umeng.socialize.bean.SHARE_MEDIA platform, Throwable t) {
                onShareClickResult(activity, resultType, platform, t);
            }
        }).createOrShareData(shareImg(activity, bitmap)).show();
    }


    /**
     * 分享图文
     */
    public static void showLoadTextImg(Activity activity, String text, String imgUrl) {
        new ShareDialog.Builder(activity).setOnShareClickListener(new ShareDialog.OnShareClickListener() {
            @Override
            public void onShareClick(int resultType, com.umeng.socialize.bean.SHARE_MEDIA platform, Throwable t) {
                onShareClickResult(activity, resultType, platform, t);
            }
        }).createOrShareData(text, shareImg(activity, imgUrl)).show();
    }

    /**
     * 分享图文
     */
    public static void showLoadTextImg(Activity activity, String text, File file) {
        new ShareDialog.Builder(activity).setOnShareClickListener(new ShareDialog.OnShareClickListener() {
            @Override
            public void onShareClick(int resultType, com.umeng.socialize.bean.SHARE_MEDIA platform, Throwable t) {
                onShareClickResult(activity, resultType, platform, t);
            }
        }).createOrShareData(text, shareImg(activity, file)).show();
    }

    /**
     * 分享图文
     */
    public static void showLoadTextImg(Activity activity, String text, Bitmap bitmap) {
        new ShareDialog.Builder(activity).setOnShareClickListener(new ShareDialog.OnShareClickListener() {
            @Override
            public void onShareClick(int resultType, com.umeng.socialize.bean.SHARE_MEDIA platform, Throwable t) {
                onShareClickResult(activity, resultType, platform, t);
            }
        }).createOrShareData(text, shareImg(activity, bitmap)).show();
    }


    /**
     * 分享链接
     */
    public static void showLoadUrl(Activity activity, String title, String description, String thumb, String webUrl) {
        new ShareDialog.Builder(activity).setOnShareClickListener(new ShareDialog.OnShareClickListener() {
            @Override
            public void onShareClick(int resultType, com.umeng.socialize.bean.SHARE_MEDIA platform, Throwable t) {
                onShareClickResult(activity, resultType, platform, t);
            }
        }).createOrShareData(shareUrl(activity, title, description, thumb, webUrl)).show();
    }


    /**
     * 分享回调监听方法
     */
    private static void onShareClickResult(Activity activity, int resultType, com.umeng.socialize.bean.SHARE_MEDIA platform, Throwable t) {
        if (resultType == 0) { //开始分享
            showDialog(activity, true);
        } else if (resultType == 1) { //分享成功
            showDialog(activity, false);
            GIToastUtil.showMessage(activity, platform + " 分享成功啦");
        } else if (resultType == 2) {
            showDialog(activity, false);
            GIToastUtil.showMessage(activity, platform + " 分享失败啦");
        } else if (resultType == 3) {
            showDialog(activity, false);
            GIToastUtil.showMessage(activity, platform + " 分享取消了");
        } else {
            showDialog(activity, false);
        }
    }


    /**
     * 分享链接--- UMImage
     */
    private static UMImage shareImg(Activity activity, String imgUrl) {
        UMImage image = new UMImage(activity, imgUrl);//网络图片
        return image;
    }

    /**
     * 分享链接--- UMImage
     */
    private static UMImage shareImg(Activity activity, File file) {
        UMImage image = new UMImage(activity, file);//本地文件
        return image;
    }

    /**
     * 分享链接--- UMImage
     */
    private static UMImage shareImg(Activity activity, Bitmap bitmap) {
        UMImage image = new UMImage(activity, bitmap);//bitmap文件
        return image;
    }


    /**
     * 分享链接--- UMWeb
     */
    private static UMWeb shareUrl(Activity activity, String title, String description, String thumb, String webUrl) {
        UMWeb web = new UMWeb(webUrl);
        web.setTitle(title);
        web.setDescription(description);
        if (TextUtils.isEmpty(thumb)) {
            web.setThumb(new UMImage(activity, R.mipmap.ic_launcher)); //默认图片为应用图标
        } else {
            web.setThumb(new UMImage(activity, thumb));
        }
        return web;
    }


    /**
     * 调用BaseActivity 里的加载框
     */
    private static void showDialog(Activity activity, boolean b) {
        if (b) {
            if (activity instanceof BaseActivity) {
                ((BaseActivity) activity).showLoadDialog();
            }
        } else {
            if (activity instanceof BaseActivity) {
                ((BaseActivity) activity).closeLoadDialog();
            }
        }
    }


}
