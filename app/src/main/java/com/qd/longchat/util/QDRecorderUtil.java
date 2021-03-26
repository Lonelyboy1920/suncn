package com.qd.longchat.util;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.longchat.base.util.QDLog;
import com.qd.longchat.R;
import com.qd.longchat.config.QDStorePath;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/6/25 下午1:44
 */
public class QDRecorderUtil {

    private final static int MAX_LENGTH = 60000;

    private MediaRecorder recorder;

    private ExecutorService executorService;

    private Context context;

    private String filePath;

    private Dialog dialog;

    private ImageView dialogImg;

    private TextView dialogTextView;

    private static MediaPlayer player;

    private long time;

    private boolean isStop;

    private Handler handler = new Handler(Looper.getMainLooper());

    public QDRecorderUtil(Context context) {
        this.context = context;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            startRecord();
        }
    };

    public void start() {
        executorService.submit(runnable);
        showDialog();
    }

    public void startRecord() {
        isStop = false;
        if (recorder == null) {
            recorder = new MediaRecorder();
        } else {
            try {
                recorder.stop();
                recorder.release();
                recorder.reset();
                recorder = null;
            } catch (Exception e) {
                e.printStackTrace();
                recorder.reset();
                recorder = null;
            }
            recorder = new MediaRecorder();
        }
        try {
            filePath = QDStorePath.MSG_VOICE_PATH + System.currentTimeMillis() + ".m4a";

            //从麦克风中采集声音数据
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //设置保存文件格式为MP4
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            //设置采样频率,44100是所有安卓设备都支持的频率,频率越高，音质越好，当然文件越大
            recorder.setAudioSamplingRate(44100);
            //设置声音数据编码格式,音频通用格式是AAC
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            //设置编码频率
            recorder.setAudioEncodingBitRate(96000);
            new File(filePath);
            recorder.setOutputFile(filePath);
            recorder.setMaxDuration(MAX_LENGTH);
            recorder.prepare();
            recorder.start();
            time = System.currentTimeMillis();
            handler.postDelayed(updateRunnable, 100);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateVoiceValue() {
        if (recorder != null) {
            long currentTime = System.currentTimeMillis();

            int offset = (int) ((currentTime - time) / 1000);

            int value = recorder.getMaxAmplitude();
            dialogImg.getDrawable().setLevel(value);
            handler.postDelayed(updateRunnable, 100);

            if (offset < 10) {
                dialogTextView.setText("00:0" + offset);
            } else if (offset >= 60) {
                stopRecord();
                EventBus.getDefault().post(new File(getFilePath()));
            } else {
                dialogTextView.setText("00:" + offset);
            }
        }
    }

    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            updateVoiceValue();
        }
    };

    private void showDialog() {
        if (dialog == null) {
            dialog = new Dialog(context, R.style.DialogStyle);
            dialog.setContentView(R.layout.dialog_record);
            dialogImg = dialog.findViewById(R.id.record_dialog_img);
            dialogTextView = dialog.findViewById(R.id.record_dialog_txt);
            dialogImg.setImageResource(R.drawable.ic_voice_record);
        }
        dialogTextView.setText("00:00");
        dialog.show();
    }

    public void updateImage(boolean isPause) {
        if (dialog == null) {
            dialog = new Dialog(context, R.style.DialogStyle);
            dialog.setContentView(R.layout.dialog_record);
            dialogImg = dialog.findViewById(R.id.record_dialog_img);
            dialogTextView = dialog.findViewById(R.id.record_dialog_txt);
            dialogImg.setImageResource(R.drawable.ic_voice_record);
        }
        if (isPause) {
            dialogImg.setImageResource(R.mipmap.im_record_cancel);
            dialogTextView.setVisibility(View.GONE);
        } else {
            dialogImg.setImageResource(R.drawable.ic_voice_record);
            dialogTextView.setVisibility(View.VISIBLE);
//            handler.removeCallbacks(updateRunnable);
//            handler.postDelayed(updateRunnable, 100);
        }

//        File file = new File(filePath);
//        file.delete();
    }

    public void deleteRecordFile() {
        File file = new File(filePath);
        file.delete();
    }

    public void stopRecord() {
        isStop = true;
        handler.removeCallbacks(updateRunnable);
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (recorder != null) {
            try {
                recorder.setOnErrorListener(null);
                recorder.setOnInfoListener(null);
                recorder.setPreviewDisplay(null);
                recorder.stop();
                recorder.reset();
                recorder.release();
                recorder = null;
            } catch (Exception e) {
                e.printStackTrace();
                QDLog.e("1111", e.toString());
                recorder.reset();
                recorder.release();
                recorder = null;
            }
        }
    }

    private static OnCompleteListener completeListener;

    public static void play(String filePath, final OnCompleteListener listener) {
        if (completeListener != null) {
            completeListener.onComplete();
        }
        completeListener = listener;
        if (player == null) {
            player = new MediaPlayer();
        } else {
            player.stop();
            player.reset();
            player.release();
            player = new MediaPlayer();
        }
        try {
            player.setDataSource(filePath);
            player.prepareAsync();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    player.start();
                }
            });

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    listener.onComplete();
                    completeListener = null;
                    player.stop();
                    player.release();
                    player = null;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isStop() {
        return this.isStop;
    }

    public static void stopPlay() {
        try {
            if (completeListener != null) {
                completeListener = null;
            }
            if (player != null) {
                player.stop();
                player.release();
                player = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getFilePath() {
        return filePath;
    }

    public interface OnCompleteListener {
        void onComplete();
    }
}
