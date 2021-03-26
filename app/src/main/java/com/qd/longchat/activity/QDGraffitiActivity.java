package com.qd.longchat.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.util.QDBitmapUtil;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.view.QDGraffitiView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight@dingtalk.com)
 * @creatTime 2018/1/25 下午1:55
 */

public class QDGraffitiActivity extends QDBaseActivity {

    public static final int[] penColors =
            {
                    Color.argb(255, 221, 42, 28),
                    Color.argb(255, 255, 255, 255),
                    Color.argb(255, 239, 207, 5),
                    Color.argb(255, 27,166,26),
                    Color.argb(255, 21,75,229),
                    Color.argb(255,166,26,158),
                    Color.argb(255, 26,128,166),
            };
    private int index;

    @BindView(R2.id.layout)
    LinearLayout llColor;
    @BindView(R2.id.view_graffiti)
    QDGraffitiView graffitiView;
    @BindView(R2.id.btn_gra_undo)
    Button btnUndo;
    @BindView(R2.id.btn_gra_color)
    Button btnColor;
    @BindView(R2.id.btn_gra_save)
    Button btnSave;
    @BindView(R2.id.btn_gra_send)
    Button btnSend;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(true,false);
        setContentView(R.layout.activity_graffiti);
        ButterKnife.bind(this);

        initTitleView(findViewById(R.id.view_gra_title));
        tvTitleName.setText(R.string.contact_edit);

        String path = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_IMAGE_PATH);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        graffitiView.setImageBitmap(bitmap);
        graffitiView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        invalidate();
        setListener();
    }

    private void setListener() {
        btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graffitiView.undo();
            }
        });

        btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorSelect();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveResultImage();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResultImage();
            }
        });
    }

    public void colorSelect() {
        if (llColor.getVisibility() == View.VISIBLE) {
            llColor.setVisibility(View.GONE);
            btnColor.setBackgroundResource(R.mipmap.im_pen_btn_normal);
        } else if (llColor.getVisibility() == View.GONE) {
            llColor.setVisibility(View.VISIBLE);
            btnColor.setBackgroundResource(R.mipmap.im_pen_btn_pressed);
        }
    }

    public void saveResultImage() {
        graffitiView.setDrawingCacheEnabled(true);
        Bitmap skechResult = graffitiView.getDrawingCache();
        QDBitmapUtil.saveBitmap(skechResult, "save", getApplicationContext(), false);
    }

    public void sendResultImage() {
        graffitiView.setDrawingCacheEnabled(true);
        Bitmap skechResult = graffitiView.getDrawingCache();
        String path = QDBitmapUtil.saveBitmap(skechResult, "send", getApplicationContext(), true);
        Intent intent = new Intent();
        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_IMAGE_PATH, path);
        setResult(RESULT_OK, intent);
        finish();
    }

    //设置颜色选择布局
    private void invalidate() {
        int size = penColors.length;
        int with = 60;
        int height = 60;
        for (int i = 0; i < size; i++) {
            ImageButton imageButton = new ImageButton(this);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            Bitmap bitmap = Bitmap.createBitmap(with, height, Bitmap.Config.ARGB_4444);
            Canvas canvas = new Canvas(bitmap);
            if (index == i) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.argb(128, 0, 253, 77));
                paint.setStrokeWidth(2);
                canvas.drawCircle(with / 2, height / 2, 28, paint);
            }
            paint.setColor(penColors[i]);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(with / 2, height / 2, 25, paint);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            imageButton.setLayoutParams(params);
            imageButton.setImageBitmap(bitmap);
            imageButton.setBackgroundColor(0X525252);
            llColor.addView(imageButton);
            final int finalIndex = i;
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    index = finalIndex;
                    llColor.removeAllViews();
                    invalidate();
                    graffitiView.setPanitColor(penColors[index]);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle(R.string.im_text_reminder).setMessage(R.string.sure_loginout)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.title_sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //TODO:do nothing
                        Intent intent = new Intent();
                        setResult(RESULT_CANCELED, intent);
                        finish();
                    }
                }).show();
    }
}
