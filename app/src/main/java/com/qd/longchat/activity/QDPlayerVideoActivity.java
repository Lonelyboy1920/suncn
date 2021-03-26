package com.qd.longchat.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.qd.longchat.R;
import com.qd.longchat.util.QDIntentKeyUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import com.qd.longchat.R2;
/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/6/27 下午2:44
 */
public class QDPlayerVideoActivity extends QDBaseActivity {

    @BindView(R2.id.video_view)
    VideoView videoView;
    @BindView(R2.id.tv_video_warn)
    TextView tvWarn;

    private String path;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_player_video);
        ButterKnife.bind(this);

        path = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_SHOOT_PATH);

        MediaController mc = new MediaController(this);
        mc.setVisibility(View.INVISIBLE);  //隐藏VideoView自带的进度条
        videoView.setMediaController(mc);
        videoView.setVideoPath(path);
        videoView.requestFocus();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();//自动开始播放
            }
        });

        /**
         * 播放结束监听
         */
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
//                videoToast.setVisibility(View.VISIBLE);
//                playVideo.setVideoPath(path);
//                playVideo.start();
            }
        });
        /**
         * 播放错误监听
         */
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
//                showErrorDialog();
                return true;
            }
        });
    }

    @OnClick(R2.id.tv_video_warn)
    public void onClick() {
        finish();
    }

    @OnTouch(R2.id.video_view)
    public boolean onTouch() {
        finish();
        return true;
    }

}
