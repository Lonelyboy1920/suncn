package com.share.util;

import android.Manifest;
import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import java.io.File;
import java.lang.ref.WeakReference;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;

public class ShareUtil {

    private static UMShareListener mShareListener;
    private static ShareAction mShareAction;

    /**
     * 分享 --- 调用此方法
     */
    public static void show(final Activity activity, final String title, final String description, final String thumb, final String webUrl) {
        applyPermission(activity, title, description, thumb, webUrl);
    }


    /**
     * 申请权限
     */
    private static void applyPermission(final Activity activity, final String title, final String description, final String thumb, final String webUrl) {
        HiPermission.create(activity).checkSinglePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
            @Override
            public void onClose() {
                Log.e("tag", "onClose");
            }

            @Override
            public void onFinish() {
                Log.e("tag", "onFinish");
            }

            @Override
            public void onDeny(String permission, int position) {
                Log.e("tag", "onDeny:" + permission);
            }

            @Override
            public void onGuarantee(String permission, int position) {
                Log.e("tag", "onGuarantee:" + permission);
                guaranteeShowUrl(activity, title, description, thumb, webUrl);
            }
        });
    }

    /**
     * 分享链接
     */
    private static void guaranteeShowUrl(final Activity activity, final String title, final String description, final String thumb, final String webUrl) {
        if (mShareListener == null) {
            mShareListener = new CustomShareListener(activity);
        }
        if (mShareAction == null) {
            /*增加自定义按钮的分享面板*/
            mShareAction = new ShareAction(activity).setDisplayList(
                    SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.DINGTALK, SHARE_MEDIA.SINA)
                    .setShareboardclickCallback(new ShareBoardlistener() {
                        @Override
                        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                            new ShareAction(activity).withMedia(shareUrl(activity,title,description,thumb,webUrl))
                                    .setPlatform(share_media).setCallback(mShareListener).share();
                        }
                    });
        }
        mShareAction.open();
    }


    private static class CustomShareListener implements UMShareListener {

        private WeakReference<Activity> mActivity;

        private CustomShareListener(Activity activity) {
            mActivity = new WeakReference(activity);
        }

        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            if (platform.name().equals("WEIXIN_FAVORITE")) {
                Toast.makeText(mActivity.get(), platform + " 收藏成功啦", Toast.LENGTH_SHORT).show();
            } else {
                if (platform != SHARE_MEDIA.MORE && platform != SHARE_MEDIA.SMS
                        && platform != SHARE_MEDIA.EMAIL
                        && platform != SHARE_MEDIA.FLICKR
                        && platform != SHARE_MEDIA.FOURSQUARE
                        && platform != SHARE_MEDIA.TUMBLR
                        && platform != SHARE_MEDIA.POCKET
                        && platform != SHARE_MEDIA.PINTEREST
                        && platform != SHARE_MEDIA.INSTAGRAM
                        && platform != SHARE_MEDIA.GOOGLEPLUS
                        && platform != SHARE_MEDIA.YNOTE
                        && platform != SHARE_MEDIA.EVERNOTE) {
                    Toast.makeText(mActivity.get(), platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            if (platform != SHARE_MEDIA.MORE && platform != SHARE_MEDIA.SMS
                    && platform != SHARE_MEDIA.EMAIL
                    && platform != SHARE_MEDIA.FLICKR
                    && platform != SHARE_MEDIA.FOURSQUARE
                    && platform != SHARE_MEDIA.TUMBLR
                    && platform != SHARE_MEDIA.POCKET
                    && platform != SHARE_MEDIA.PINTEREST

                    && platform != SHARE_MEDIA.INSTAGRAM
                    && platform != SHARE_MEDIA.GOOGLEPLUS
                    && platform != SHARE_MEDIA.YNOTE
                    && platform != SHARE_MEDIA.EVERNOTE) {
                Toast.makeText(mActivity.get(), platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(mActivity.get(), platform + " 分享取消了", Toast.LENGTH_SHORT).show();
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
        if (!TextUtils.isEmpty(thumb)) {
            web.setThumb(new UMImage(activity, thumb));
        }
        return web;
    }



}
