package com.qd.longchat.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.qd.longchat.R;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.qd.longchat.R2;
/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2019/2/20 上午9:42
 */
public class QDPlayMusicActivity extends QDBaseActivity {

    @BindView(R2.id.tv_title_back)
    TextView tvBlack;
    @BindView(R2.id.iv_play)
    ImageView ivPlay;
    @BindView(R2.id.tv_time)
    TextView tvTime;
    @BindView(R2.id.seek_bar)
    SeekBar seekBar;
    @BindView(R2.id.tv_time_remain)
    TextView tvTimeRemain;

    private String path;
    private MediaPlayer mediaPlayer;
    private Handler handler;
    private int totalSecond;
    private float index;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer.isPlaying()) {
                seekBar.setProgress((int) index);
                handler.postDelayed(runnable, 10);
                index += 0.01;
                tvTime.setText(QDUtil.secondToMinutes(index));
                tvTimeRemain.setText("- " + QDUtil.secondToMinutes(totalSecond - index));
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_play_music);
        ButterKnife.bind(this);

        path = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_FILE_PATH);
        totalSecond = QDUtil.getRecordFileDuration(path);
        tvTime.setText("0:00");
        tvTimeRemain.setText("- " + QDUtil.secondToMinutes(totalSecond));

        seekBar.setMax(totalSecond);
        seekBar.setProgress(0);

        handler = new Handler();

        playMusic();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(runnable);
                int second = seekBar.getProgress();
                index = second;
                handler.postDelayed(runnable, 10);
                mediaPlayer.seekTo(second * 1000);
            }
        });

    }

    @OnClick({R2.id.iv_play, R2.id.tv_title_back})
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.iv_play) {
            if (mediaPlayer.isPlaying()) {
                handler.removeCallbacks(runnable);
                ivPlay.setImageResource(R.drawable.ic_play);
                mediaPlayer.pause();
            } else {
                ivPlay.setImageResource(R.drawable.ic_pause);
                mediaPlayer.start();
                handler.postDelayed(runnable, 10);
            }

        } else if (i == R.id.tv_title_back) {
            onBackPressed();

        }
    }

    private void playMusic() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                    handler.postDelayed(runnable, 10);
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    ivPlay.setImageResource(R.drawable.ic_play);
                    seekBar.setProgress(0);
                    handler.removeCallbacks(runnable);
                    try {
                        mediaPlayer.stop();
                        mediaPlayer.prepareAsync();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopPlay() {
        handler.removeCallbacks(runnable);
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mediaPlayer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPlay();
    }
}
