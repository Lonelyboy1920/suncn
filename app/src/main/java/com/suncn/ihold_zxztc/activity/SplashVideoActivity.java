package com.suncn.ihold_zxztc.activity;

import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.suncn.ihold_zxztc.MyApplication;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.MyVideoPlayer;

import cn.jzvd.JzvdStd;

/**
 * 视频闪屏
 */
public class SplashVideoActivity extends BaseActivity {
    @BindView(id = R.id.tv_gotoApp)
    private TextView gotoApp_TextView;
    @BindView(id = R.id.videoview)
    private MyVideoPlayer videoView;

    private boolean isClickJumpBtn;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_splash_video);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            // 始终允许窗口延伸到屏幕短边上的刘海区域
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
    }

    @Override
    public void initData() {
        super.initData();
        String proxyUrl = MyApplication.getProxy(this).getProxyUrl(Utils.formatFileUrl(activity, GISharedPreUtil.getString(activity, "strSplashUrl")));
        GILogUtil.e("proxyUrl=======",proxyUrl);
        videoView.setUp(proxyUrl, "", JzvdStd.SCREEN_NORMAL);
        videoView.startButton.performClick();
        videoView.posterImageView.setBackgroundResource(R.mipmap.splash_bg);
        videoView.setCanShowTools(false);
        videoView.videoStateListener = () -> {
            if (!isClickJumpBtn) {
                jumpActivity();
            }
        };
        JzvdStd.setVideoImageDisplayType(JzvdStd.VIDEO_IMAGE_DISPLAY_TYPE_FILL_PARENT);//设置容器内播放器高,解决黑边（视频全屏）
    }

    @Override
    public void onResume() {
        super.onResume();
        JzvdStd.goOnPlayOnResume();
    }

    @Override
    public void setListener() {
        super.setListener();
        gotoApp_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClickJumpBtn = true;
                jumpActivity();
            }
        });
    }

    private void jumpActivity() {
        if (getResources().getBoolean(R.bool.IS_OPEN_VISITOR) || GISharedPreUtil.getBoolean(activity, "isHasLogin")) {
            skipActivity(activity, MainActivity.class);
        } else {
            skipActivity(activity, LoginActivity.class);
        }
    }
}
