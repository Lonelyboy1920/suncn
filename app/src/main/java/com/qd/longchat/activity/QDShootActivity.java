package com.qd.longchat.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.qd.longchat.R;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.view.QDMovieRecorderView;
import com.qd.longchat.view.QDRecordedButton;

/**
 * Created by skynight on 2016/12/5.
 */

public class QDShootActivity extends QDBaseActivity implements View.OnClickListener {

    private QDMovieRecorderView mRecorderView;
    private boolean isExistVedio = false;
    private boolean recordedOver;

    private QDRecordedButton shootBtn;
    private ImageView shootEsc;
    private ImageView shootSend;
    private ImageView shootCancel;
    private ImageView shootSC;
    private TextView shootHint;
    private TextView shootTime;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mRecorderView.stop();
                    isExistVedio = true;
                    break;
                case 2:
                    break;
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler myHandler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!recordedOver) {
                shootBtn.setProgress(mRecorderView.getTimeCount());
                myHandler1.sendEmptyMessageDelayed(0, 50);
//                tv_hint.setVisibility(View.GONE);
                final int s = (int) Math.floor(mRecorderView.getTimeCount() / 100);
                final int ms = (int) Math.floor(mRecorderView.getTimeCount() % 100 / 10);
                shootTime.setText(s + "." + ms + "s");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_activity_shoot);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initView();
    }

    private void initView() {
        mRecorderView = findViewById(R.id.ll_shoot_content);
        shootBtn = findViewById(R.id.btn_shooting);
        shootEsc = findViewById(R.id.tv_shoot_delete);
        shootSend = findViewById(R.id.tv_shoot_send);
        shootSC = findViewById(R.id.img_shoot_switch_camera);
        shootCancel = findViewById(R.id.shoot_back);
        shootHint = findViewById(R.id.shoot_hint);
        shootTime = findViewById(R.id.tv_shoot_time);

        shootBtn.setMax(800);
        shootBtn.setOnGestureListener(new QDRecordedButton.OnGestureListener() {
            @Override
            public void onLongClick() {
                mRecorderView.startRecorder(new QDMovieRecorderView.OnRecordFinishListener() {
                    @Override
                    public void onRecordFinish() {
                        handler.sendEmptyMessage(1);
                    }
                });
                shootHint.setVisibility(View.INVISIBLE);
                shootTime.setVisibility(View.VISIBLE);
                shootCancel.setVisibility(View.GONE);
                recordedOver = false;
                myHandler1.sendEmptyMessageDelayed(0, 50);
            }

            @Override
            public void onClick() {

            }

            @Override
            public void onLift() {
                recordedOver = true;
            }

            @Override
            public void onOver() {
                shootSC.setEnabled(true);
                if (mRecorderView.getTimeCount() > 100) {
                    handler.sendEmptyMessage(1);
                    shootBtn.setVisibility(View.INVISIBLE);
                    shootSend.setVisibility(View.VISIBLE);
                    shootEsc.setVisibility(View.VISIBLE);
                } else {
                    showTimeShortDialog();
                    handler.sendEmptyMessageDelayed(2, 500);
                    mRecorderView.stop();
                    if (mRecorderView.getmRecordFile() != null)
                        mRecorderView.getmRecordFile().delete();
                }
                recordedOver = true;
                shootBtn.closeButton();
            }
        });

        mRecorderView.setNoPermissionClickListener(new QDMovieRecorderView.OnNoPermissionClickListener() {
            @Override
            public void onNoPermissionClick() {
//                点击无录像权限提示的popup后，关闭当前界面
                finish();
            }
        });

        shootEsc.setOnClickListener(this);
        shootSend.setOnClickListener(this);
        shootSC.setOnClickListener(this);
        shootCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.tv_shoot_delete) {
            if (mRecorderView.getmRecordFile() != null)
                mRecorderView.getmRecordFile().delete();
            shootBtn.setVisibility(View.VISIBLE);
            shootHint.setVisibility(View.VISIBLE);
            shootTime.setVisibility(View.INVISIBLE);
            shootTime.setText("0.0s");
            shootCancel.setVisibility(View.VISIBLE);
            shootSend.setVisibility(View.INVISIBLE);
            shootEsc.setVisibility(View.GONE);

        } else if (i == R.id.tv_shoot_send) {// 返回到播放页面
            Intent intent = new Intent();
            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_SHOOT_PATH, mRecorderView.getmRecordFile().getAbsolutePath());
            setResult(RESULT_OK, intent);
            finish();
        } else if (i == R.id.img_shoot_switch_camera) {
            mRecorderView.switchCamera();

        } else if (i == R.id.shoot_back) {
            if (mRecorderView.getmRecordFile() != null)
                mRecorderView.getmRecordFile().delete();
            finish();

        }
    }

//    private void showBackDialog() {
//        new AlertDialog.Builder(this).setTitle(R.string.im_shoot_back_dialog)
//                .setPositiveButton(R.string.im_text_ensure, new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (mRecorderView.getmRecordFile() != null)
//                            mRecorderView.getmRecordFile().delete();
//                        dialog.dismiss();
//                        finish();
//                    }
//                })
//                .setNegativeButton(R.string.im_text_cancel, new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                }).show();
//    }

    private void showTimeShortDialog() {
        new AlertDialog.Builder(this).setTitle(R.string.im_toast_time_short)
                .setCancelable(false)
                .setPositiveButton(R.string.title_sure, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shootTime.setText("0.0s");
                        shootTime.setVisibility(View.INVISIBLE);
                        shootHint.setVisibility(View.VISIBLE);
                        shootCancel.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(null, null).show();
    }
}
