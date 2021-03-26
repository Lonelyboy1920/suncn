package com.qd.longchat.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.longchat.base.btf.IMBTFFile;
import com.longchat.base.btf.IMBTFText;
import com.longchat.base.dao.QDMessage;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDUserHelper;
import com.longchat.base.model.QDCollectMessage;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.config.QDStorePath;
import com.qd.longchat.util.QDBitmapUtil;
import com.qd.longchat.util.QDDateUtil;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;
import com.qd.longchat.view.QDVideoView;
import com.qd.longchat.widget.QDChatSmiley;

import java.io.IOException;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/7/12 下午1:32
 */
public class QDCollectDetailActivity extends QDBaseActivity {

    @BindView(R2.id.view_cd_title)
    View viewTitle;
    @BindView(R2.id.iv_cd_icon)
    ImageView ivIcon;
    @BindView(R2.id.tv_cd_name)
    TextView tvName;
    @BindView(R2.id.fl_cd_container)
    FrameLayout flContainer;
    @BindView(R2.id.tv_cd_info)
    TextView tvInfo;

    @BindString(R2.string.acc_detail)
    String strDetail;
    @BindString(R2.string.collect_at)
    String strAt;

    private QDCollectMessage message;
    private QDUser user;
    private ProgressBar progressBar;
    private MediaPlayer mediaPlayer;

    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer.isPlaying()) {
                progressBar.setProgress(mediaPlayer.getCurrentPosition());
                handler.postDelayed(runnable, 10);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_detail);
        ButterKnife.bind(this);

        initTitleView(viewTitle);
        tvTitleName.setText(strDetail);

        message = getIntent().getParcelableExtra(QDIntentKeyUtil.INTENT_KEY_COLLECT_MESSAGE);
        user = QDUserHelper.getUserById(message.getSenderId());

        if (user == null) {
            Bitmap bitmap = QDBitmapUtil.getInstance().createNameAvatar(context, message.getSenderId(), message.getSenderName());
            ivIcon.setImageBitmap(bitmap);
        } else {
            QDBitmapUtil.getInstance().createAvatar(context, user.getId(), user.getName(), user.getPic(), ivIcon);
        }

        String senderName = message.getSenderName();
        String source = message.getSource();
        if (senderName.equalsIgnoreCase(source)) {
            tvName.setText(senderName);
        } else {
            tvName.setText(senderName + "-" + source);
        }

        tvInfo.setText(strAt + QDDateUtil.getMsgTime(message.getCreateDate() * 1000));

        switch (message.getMsgType()) {
            case QDMessage.MSG_TYPE_TEXT:
                flContainer.addView(createTextView());
                break;
            case QDMessage.MSG_TYPE_SHOOT:
                flContainer.addView(createVideoView());
                break;
            case QDMessage.MSG_TYPE_IMAGE:
                flContainer.addView(createImageView());
                break;
            case QDMessage.MSG_TYPE_VOICE:
                flContainer.addView(createVoiceView());
                break;
        }
    }

    private TextView createTextView() {
        TextView textView = new TextView(context);
        IMBTFText text = IMBTFText.fromBTFXml(message.getContent());
        if (text != null)
            textView.setText(QDChatSmiley.getInstance(context).strToSmiley(text.getContent()));
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(18);
        return textView;
    }

    private VideoView createVideoView() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, QDUtil.dp2px(context, 600));
        IMBTFFile file = IMBTFFile.fromBTFXml(message.getContent());
        final String path = QDStorePath.COLLECT_PATH + message.getGid() + QDUtil.getSuffix(file.getName());
        final QDVideoView videoView = new QDVideoView(context);
        params.gravity = Gravity.CENTER;
        videoView.setLayoutParams(params);
        MediaController mc = new MediaController(this);
        mc.setVisibility(View.INVISIBLE);  //隐藏VideoView自带的进度条
        videoView.setMediaController(mc);
        videoView.setVideoPath(path);
        videoView.requestFocus();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                videoView.start();//自动开始播放
            }
        });

        /**
         * 播放结束监听
         */
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoView.setVideoPath(path);
                videoView.start();
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

        return videoView;
    }

    private ImageView createImageView() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(params);
        imageView.setAdjustViewBounds(true);
        imageView.setBackgroundColor(getResources().getColor(R.color.colorRed));
        String path = QDStorePath.COLLECT_PATH + message.getGid() + ".png";
        decodeBitmap(path, imageView);
        return imageView;
    }

    private View createVoiceView() {
        View view = LayoutInflater.from(context).inflate(R.layout.item_collect_detail_voice, null);
        final ImageView ivStart = view.findViewById(R.id.iv_voice_start);
        TextView tvTime = view.findViewById(R.id.tv_voice_time);
        progressBar = view.findViewById(R.id.pb_voice_progressbar);
        IMBTFFile file = IMBTFFile.fromBTFXml(message.getContent());
        String path = QDStorePath.COLLECT_PATH + message.getGid() + QDUtil.getSuffix(file.getName());
        int seconds = QDUtil.getRecordFileDuration(path);
        tvTime.setText(QDUtil.secondToMinutes(seconds));

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            progressBar.setMax(mediaPlayer.getDuration());

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    ivStart.setImageResource(R.drawable.ic_play);
                    progressBar.setProgress(0);
                    handler.removeCallbacks(runnable);
                    try {
                        mediaPlayer.stop();
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        ivStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isPlaying()) {
                    ivStart.setImageResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                    handler.removeCallbacks(runnable);
                    handler.postDelayed(runnable, 100);
                } else {
                    ivStart.setImageResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                }
            }
        });
        return view;
    }


    private void decodeBitmap(String path, ImageView imageView) {
        Bitmap bitmap;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);

        opts.inSampleSize = 1;

        opts.inJustDecodeBounds = false;

        bitmap = BitmapFactory.decodeFile(path, opts);

        imageView.setImageBitmap(bitmap);
    }

}
