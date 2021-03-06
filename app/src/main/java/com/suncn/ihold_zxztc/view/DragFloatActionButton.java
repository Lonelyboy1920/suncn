package com.suncn.ihold_zxztc.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.app.hubert.guide.util.ScreenUtils;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GITextView;
import com.suncn.ihold_zxztc.R;


public class DragFloatActionButton extends LinearLayout {
    private float mLastRawX;
    private float mLastRawY;
    private final String TAG = "AttachButton";
    private boolean isDrug = false;
    private int mRootMeasuredWidth;
    private Drawable leftDrawable;
    private Drawable centerDrawable;
    private Drawable rigthDrawable;
    ImageView tv;
    ImageView ivFull;
    GITextView tvMessage;
    GITextView tvClose;
    LinearLayout llMessage;
    private Context context;
    private OnClickListener ananClick;
    private OnClickListener noticeClick;

    public DragFloatActionButton(Context context) {
        this(context, null);
    }

    public DragFloatActionButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void setAnanClick(OnClickListener ananClick) {
        this.ananClick = ananClick;
    }

    public void setNoticeClick(OnClickListener noticeClick) {
        this.noticeClick = noticeClick;
        tvMessage.setOnClickListener(noticeClick);
    }

    public LinearLayout getLlMessage() {
        return llMessage;
    }

    public DragFloatActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.popupwindow, this);
        renderView(view);
    }

    public void renderView(View mView) {
        //?????????????????????
        tv = mView.findViewById(R.id.left);
        ivFull = mView.findViewById(R.id.ivFull);
        tvMessage = mView.findViewById(R.id.tv_message);
        tvClose = mView.findViewById(R.id.tv_close);
        llMessage = mView.findViewById(R.id.ll_message);
        tvMessage.setOnClickListener(noticeClick);
        tvClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                llMessage.setVisibility(GONE);
            }
        });
        if (GIStringUtil.isNotBlank(GISharedPreUtil.getString(context, "strAnAnTitle"))) {
            llMessage.setVisibility(View.VISIBLE);
            tvMessage.setText(GISharedPreUtil.getString(context, "strAnAnTitle"));
        } else {
            llMessage.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //?????????????????????
        float mRawX = ev.getRawX();
        float mRawY = ev.getRawY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN://????????????
                isDrug = false;
                //?????????????????????
                mLastRawX = mRawX;
                mLastRawY = mRawY;
                break;
            case MotionEvent.ACTION_MOVE://????????????
                llMessage.setVisibility(GONE);
                ViewGroup mViewGroup = (ViewGroup) getParent();
                tv.setVisibility(GONE);
                ivFull.setVisibility(VISIBLE);
                if (mViewGroup != null) {
                    tv.setImageResource(R.mipmap.icon_robot_full);
                    int[] location = new int[2];
                    mViewGroup.getLocationInWindow(location);
                    //????????????????????????
                    int mRootMeasuredHeight = mViewGroup.getMeasuredHeight();
                    mRootMeasuredWidth = mViewGroup.getMeasuredWidth();
                    //??????????????????????????????
                    int mRootTopY = location[1];
                    if (mRawX >= 0 && mRawX <= mRootMeasuredWidth && mRawY >= mRootTopY && mRawY <= (mRootMeasuredHeight + mRootTopY)) {
                        //??????X???????????????
                        float differenceValueX = mRawX - mLastRawX;
                        //??????Y???????????????
                        float differenceValueY = mRawY - mLastRawY;
                        //???????????????????????????
                        if (!isDrug) {
                            if (Math.sqrt(differenceValueX * differenceValueX + differenceValueY * differenceValueY) < 2) {
                                isDrug = false;
                            } else {
                                isDrug = true;
                            }
                        }
                        //??????????????????????????????????????????X????????????
                        float ownX = getX();
                        //??????????????????????????????????????????Y????????????
                        float ownY = getY();
                        //?????????X??????????????????
                        float endX = ownX + differenceValueX;
                        //?????????Y??????????????????
                        float endY = ownY + differenceValueY;
                        //X??????????????????????????????
                        float maxX = mRootMeasuredWidth - getWidth();
                        //Y??????????????????????????????
                        float maxY = mRootMeasuredHeight - getHeight();
                        //X???????????????
                        endX = endX < 0 ? 0 : endX > maxX ? maxX : endX;
                        //Y???????????????
                        endY = endY < 0 ? 0 : endY > maxY ? maxY : endY;
                        //????????????
                        setX(endX);
                        setY(endY);
                        //????????????
                        mLastRawX = mRawX;
                        mLastRawY = mRawY;
                    }
                }
                break;
            case MotionEvent.ACTION_UP://????????????
                float center = ScreenUtils.getScreenWidth(context) / 2;
                //????????????
                if (mLastRawX <= center) {
                    //????????????
                    DragFloatActionButton.this.animate()
                            .setInterpolator(new BounceInterpolator())
                            .setDuration(500)
                            .x(0)
                            .start();
                    tv.setImageResource(R.mipmap.icon_robot_left);
                    tv.setVisibility(VISIBLE);
                    ivFull.setVisibility(GONE);
                } else {
                    //????????????
                    DragFloatActionButton.this.animate()
                            .setInterpolator(new BounceInterpolator())
                            .setDuration(500)
                            .x(ScreenUtils.getScreenWidth(context) - tv.getWidth())
                            .start();
                    tv.setImageResource(R.mipmap.icon_robot_right);
                    tv.setVisibility(VISIBLE);
                    ivFull.setVisibility(GONE);
                }
                break;
        }
        //??????????????????
        return isDrug ? isDrug : super.onTouchEvent(ev);
    }
}