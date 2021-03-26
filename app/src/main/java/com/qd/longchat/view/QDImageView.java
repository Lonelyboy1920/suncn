package com.qd.longchat.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.qd.longchat.util.QDUtil;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2019/4/11 下午4:01
 */
public class QDImageView extends RoundedImageView {

    private Context context;

    public QDImageView(Context context) {
        super(context);
        this.context = context;
    }

    public QDImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public QDImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    public void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            super.onDraw(canvas);
        } else {
            int width = getWidth();
            int height = getHeight();

            int mWidth = QDUtil.dp2px(context, 80);
            int mHeight = QDUtil.dp2px(context, 150);
            int minSize = QDUtil.dp2px(context, 40);

            ViewGroup.LayoutParams params = getLayoutParams();

            if (width > mWidth) {
                params.width = mWidth;
            } else if (width < minSize) {
                params.width = minSize;
            }

            if (height > mHeight) {
                params.height = mHeight;
            } else if (height < minSize) {
                params.height = minSize;
            }

            setLayoutParams(params);
            super.onDraw(canvas);
        }
    }

//    private Matrix getMatrix(int min, int max, boolean isWidthMin) {
//        int MIN_SIZE = QDUtils.dp2px(context, 80);
//        int MAX_SIZE = QDUtils.dp2px(context, 150);
//
//        Matrix matrix = new Matrix();
//        matrix.postScale(1, 1);
//        if (max <= MIN_SIZE) {
//            float scale = (float) MIN_SIZE / min;
//            matrix.postScale(scale, scale);
//        } else if (min >= MAX_SIZE) {
//            float scale = (float) MAX_SIZE / max;
//            matrix.postScale(scale, scale);
//        } else {
//            if (max < MAX_SIZE && min > MIN_SIZE) {
//                matrix.postScale(1, 1);
//            } else if (max > MAX_SIZE && min > MIN_SIZE) {
//                float scale = (float) MAX_SIZE / max;
//                if (isWidthMin) {
//                    matrix.postScale(1, scale);
//                } else {
//                    matrix.postScale(scale, 1);
//                }
//            } else if (max < MAX_SIZE && min < MIN_SIZE) {
//                float scale = (float) MIN_SIZE / min;
//                if (isWidthMin) {
//                    matrix.postScale(scale, 1);
//                } else {
//                    matrix.postScale(1, scale);
//                }
//            } else if (max > MAX_SIZE && min < MIN_SIZE) {
//                float scaleMin = (float) MIN_SIZE / min;
//                float scaleMax = (float) MAX_SIZE / max;
//                if (isWidthMin) {
//                    matrix.postScale(scaleMin, scaleMax);
//                } else {
//                    matrix.postScale(scaleMax, scaleMin);
//                }
//            }
//        }
//        return matrix;
//    }
}
