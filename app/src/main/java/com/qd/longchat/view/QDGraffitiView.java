package com.qd.longchat.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author skynight(skynight@dingtalk.com)
 * @creatTime 2018/1/25 上午10:34
 */

@SuppressLint("AppCompatCustomView")
public class QDGraffitiView extends ImageView {

    private int panitColor;
    private Bitmap mBitmap;
    private float downX, downY;
    private float tempX, tempY;
    private List<DrawPath> drawPathList;
    private Paint paint;
    private Path path;

    public QDGraffitiView(Context context) {
        super(context);
    }

    public QDGraffitiView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QDGraffitiView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        drawPathList = new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawPathList != null && drawPathList.size() != 0) {
            for (DrawPath drawPath : drawPathList) {
                canvas.drawPath(drawPath.path, drawPath.paint);
            }
        }
        canvas.save();

    }

    private void initValue() {
        paint = new Paint();
        path = new Path();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        if (panitColor == 0) {
            paint.setColor(Color.RED);
        } else {
            paint.setColor(panitColor);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initValue();
                downX = event.getX();
                downY = event.getY();
                path.moveTo(downX, downY);
                invalidate();
                tempX = downX;
                tempY = downY;
                DrawPath drawPath = new DrawPath();
                drawPath.paint = paint;
                drawPath.path = path;
                drawPathList.add(drawPath);
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();
                path.quadTo(tempX, tempY, moveX, moveY);
                invalidate();
                tempX = moveX;
                tempY = moveY;
                break;
        }
        return true;
    }

    public void setPanitColor(int panitColor) {
        this.panitColor = panitColor;
    }


    public void undo() {
        if (drawPathList.size() > 0) {
            drawPathList.remove(drawPathList.size() - 1);
            invalidate();
        }
    }

    class DrawPath {
        Paint paint;
        Path path;
    }
}
