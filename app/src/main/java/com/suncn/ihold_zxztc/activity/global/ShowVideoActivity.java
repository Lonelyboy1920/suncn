package com.suncn.ihold_zxztc.activity.global;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GITextView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.MyVideoPlayer;
import com.suncn.ihold_zxztc.view.jzvd.JZMediaExo;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

/**
 * 播放视频(饺子播放器)
 */
public class ShowVideoActivity extends BaseActivity {
    @BindView(id = R.id.video_player)
    private MyVideoPlayer myVideoPlayer;
    @BindView(id = R.id.tv_back, click = true)
    private GITextView tvBack;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_show_video);
    }

    @Override
    public void initData() {
        super.initData();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String url = bundle.getString("url", "");
            String imgPath = bundle.getString("imgPath", "");
            if (!url.contains(Environment.getExternalStorageDirectory().getPath())) {
                url = Utils.formatFileUrl(activity, url);
            }
            if (!imgPath.contains(Environment.getExternalStorageDirectory().getPath())) {
                imgPath = Utils.formatFileUrl(activity, imgPath);
            }
            GIImageUtil.loadImg(activity, myVideoPlayer.posterImageView, imgPath, 0);
            GILogUtil.e("videoUrl----->" + url);
//        Jzvd.WIFI_TIP_DIALOG_SHOWED = true;
            myVideoPlayer.setUp(url, "", JzvdStd.SCREEN_NORMAL, JZMediaExo.class);
            myVideoPlayer.startButton.performClick();
        }

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onPause() {
        super.onPause();
        JzvdStd.goOnPlayOnPause();
        Jzvd.releaseAllVideos();
    }

    @Override
    public void onResume() {
        super.onResume();
        JzvdStd.goOnPlayOnResume();
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        switch (config.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                tvBack.setVisibility(View.VISIBLE);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                tvBack.setVisibility(View.VISIBLE);
                break;
        }
    }
}
