package com.share.util;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.share.R;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.lang.ref.WeakReference;
import java.util.List;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;

public class ShareDialog extends Dialog {

    private Activity activity;
    private TextView tvTitle;
    private TextView btnCancel;
    private GridView gridView;
    private ShareAdapter mAdapter;
    private String mDialogTitle = "选择要分享的平台";
    private int mVisibility = View.VISIBLE;
    private CustomShareListener mShareListener;
    private OnShareClickListener mOnShareListener;


    private String withText = ""; //分享文字
    private UMImage umImage = null; //分享图片
    private UMWeb umWeb = null; //分享链接
    private String[] strList = new String[]{};//分享平台列表


    public ShareDialog(Activity activity) {
        this(activity, R.style.shareDialog);
    }


    public ShareDialog(Activity activity, int themeResId) {
        super(activity, R.style.shareDialog);
        this.activity = activity;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_share_layout);
        setCanceledOnTouchOutside(true);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        btnCancel = (TextView) findViewById(R.id.btn_cancel);
        gridView = (GridView) findViewById(R.id.grid_view);
        btnCancel.setVisibility(View.VISIBLE);
        mShareListener = new CustomShareListener(activity, ShareDialog.this);
        btnCancel.setOnClickListener(v -> this.dismiss());
        mAdapter = new ShareAdapter(activity);
        gridView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((id) -> onShareItemClick(id));
        gridView.setNumColumns(strList.length);
    }


    /**
     * 调用完Builder类的create()方法后显示该对话框的方法
     */
    @Override
    public void show() {
        super.show();
        show(this);
    }

    private void show(ShareDialog mDialog) {
        if (mDialog != null) {
            if (mDialogTitle != null)
                mDialog.tvTitle.setText(mDialogTitle);
            mAdapter.setTitles(strList);
            mAdapter.notifyDataSetChanged();
            mDialog.btnCancel.setVisibility(mVisibility);
            Window window = mDialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;//如果不设置,可能部分机型出现左右有空隙,也就是产生margin的感觉
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);  //将设置好的属性set回去
        }
    }


    /**
     * 分享按钮-点击事件
     */
    public void onShareItemClick(SHARE_MEDIA platform) {

        if (isWeixinAvilible(platform, activity)) {
            applyPermission(platform);
        }
    }


    public static class Builder {

        private ShareDialog mDialog;

        public Builder(Activity activity) {
            mDialog = new ShareDialog(activity);
        }

        /**
         * 设置对话框标题
         */
        public Builder setDialogTitle(String title) {
            mDialog.mDialogTitle = title;
            return this;
        }

        public Builder setDialogList(String[] list) {
            mDialog.strList = list;
            return this;
        }

        /**
         * 设置取消按钮是否显示
         */
        public Builder setCancelVisibility(int visibility) {
            mDialog.mVisibility = visibility;
            return this;
        }

        public Builder setOnShareClickListener(OnShareClickListener listener) {
            mDialog.mOnShareListener = listener;
            return this;
        }

        /**
         * 通过Builder类设置完属性后构造对话框的方法 --- 设置分享数据
         */
        public ShareDialog createOrShareData(String withText) {
            mDialog.withText = withText;
            return mDialog;
        }

        public ShareDialog createOrShareData(UMImage umImage) {
            mDialog.umImage = umImage;
            return mDialog;
        }

        public ShareDialog createOrShareData(String withText, UMImage umImage) {
            mDialog.withText = withText;
            mDialog.umImage = umImage;
            return mDialog;
        }

        public ShareDialog createOrShareData(UMWeb umWeb) {
            mDialog.umWeb = umWeb;
            return mDialog;
        }
    }


    private static class CustomShareListener implements UMShareListener {

        private WeakReference<Activity> mActivity;
        private ShareDialog mDialog;

        private CustomShareListener(Activity activity, ShareDialog dialog) {
            mActivity = new WeakReference(activity);
            mDialog = dialog;
        }

        @Override
        public void onStart(SHARE_MEDIA platform) {
            if (mDialog.mOnShareListener != null) {
                mDialog.mOnShareListener.onShareClick(0, platform, null);
            }
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            if (platform.name().equals("WEIXIN_FAVORITE")) {
                if (mDialog.mOnShareListener != null) {
                    mDialog.mOnShareListener.onShareClick(4, platform, null);
                } else {
                    Toast.makeText(mActivity.get(), platform + " 收藏成功啦", Toast.LENGTH_SHORT).show();
                }
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
                    if (mDialog.mOnShareListener != null) {
                        mDialog.mOnShareListener.onShareClick(1, platform, null);
                    } else {
                        Toast.makeText(mActivity.get(), platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
                    }
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
                if (mDialog.mOnShareListener != null) {
                    mDialog.mOnShareListener.onShareClick(2, platform, null);
                } else {
                    Toast.makeText(mActivity.get(), platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            if (mDialog.mOnShareListener != null) {
                mDialog.mOnShareListener.onShareClick(3, platform, null);
            } else {
                Toast.makeText(mActivity.get(), platform + " 分享取消了", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * resultType 0:开始分享 1：分享成功 2：分享失败 3：取消分享 4：收藏成功
     */
    public interface OnShareClickListener {
        void onShareClick(int resultType, SHARE_MEDIA platform, Throwable t);
    }


    /**
     * 申请权限
     */
    private void applyPermission(SHARE_MEDIA platform) {
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
                onShareFun(platform);
            }
        });
    }


    /**
     * 分享
     */
    public void onShareFun(SHARE_MEDIA platform) {

        if (umWeb != null) {
            if (platform == SHARE_MEDIA.WEIXIN_CIRCLE) {
                umWeb.setTitle(umWeb.getDescription());
            }
            if (platform == SHARE_MEDIA.SINA) {
                umWeb.setDescription(umWeb.getDescription() + umWeb.toUrl());
            }
            new ShareAction(activity).withMedia(umWeb).setPlatform(platform).setCallback(mShareListener).share();
        } else if (umImage != null) {
            if (!TextUtils.isEmpty(withText)) {
                new ShareAction(activity).withText(withText).withMedia(umImage).setPlatform(platform).setCallback(mShareListener).share();
            } else {
                new ShareAction(activity).withMedia(umImage).setPlatform(platform).setCallback(mShareListener).share();
            }
        }
        ShareDialog.this.dismiss();
    }

    public boolean isWeixinAvilible(SHARE_MEDIA platform, Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (platform == SHARE_MEDIA.SINA) {
                    if (pn.equals("com.sina.weibo")) {
                        return true;
                    }
                } else if (platform == SHARE_MEDIA.WEIXIN || platform == SHARE_MEDIA.WEIXIN_CIRCLE) {
                    if (pn.equals("com.tencent.mm")) {
                        return true;
                    }
                } else if (platform == SHARE_MEDIA.QQ) {
                    if (pn.equals("com.tencent.mobileqq")) {
                        return true;
                    }
                } else if (platform == SHARE_MEDIA.QZONE) {
                    if (pn.equals("com.qzone")) {
                        return true;
                    }
                } else if (platform == SHARE_MEDIA.DINGTALK) {
                    if (pn.equals("com.alibaba.android.rimet")) {
                        return true;
                    }
                }
            }
        }
        if (platform == SHARE_MEDIA.SINA) {
            Toast.makeText(activity, "您尚未安装微博，无法分享", Toast.LENGTH_SHORT).show();
        } else if (platform == SHARE_MEDIA.WEIXIN || platform == SHARE_MEDIA.WEIXIN_CIRCLE) {
            Toast.makeText(activity, "您尚未安装微信，无法分享", Toast.LENGTH_SHORT).show();
        } else if (platform == SHARE_MEDIA.QQ) {
            Toast.makeText(activity, "您尚未安装QQ，无法分享", Toast.LENGTH_SHORT).show();
        } else if (platform == SHARE_MEDIA.QZONE) {
            Toast.makeText(activity, "您尚未安装QQ空间，无法分享", Toast.LENGTH_SHORT).show();
        } else if (platform == SHARE_MEDIA.DINGTALK) {
            Toast.makeText(activity, "您尚未安装钉钉，无法分享", Toast.LENGTH_SHORT).show();

        }
        return false;
    }

}

