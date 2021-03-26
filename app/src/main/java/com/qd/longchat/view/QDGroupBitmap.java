package com.qd.longchat.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.qd.longchat.util.QDUtil;

import java.util.List;

/**
 * author Gao
 * date 2017/5/3 0003
 * description
 */

public class QDGroupBitmap {
    private Bitmap bitmap;
    private int width, height;
    private Canvas canvas;
    private List<Bitmap> list;
    private Context context;

    public QDGroupBitmap(Context context, List<Bitmap> list) {
        this.list = list;
        this.context = context;
        init();
    }

    private void init() {
        width = QDUtil.dp2px(context, 50);//list.get(0).getWidth();
        height = QDUtil.dp2px(context, 50);//list.get(0).getHeight();
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#DBDFDE"));
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width / 2, height / 2, width, paint);
    }

    public Bitmap getGroupBitmap() {
        Bitmap bitmap;
        switch (list.size()) {
            case 2:
                bitmap = getGroupBitmapFrom2();
                break;
            case 3:
                bitmap = getGroupBitmapFrom3();
                break;
            default:
                bitmap = getGroupBitmapFrom4();
                break;
        }
        return bitmap;
    }

    private Bitmap getGroupBitmapFrom2() {
        for (int i = 0; i < list.size(); i++) {
            Bitmap b = list.get(i);
            float scale = ((float) width) / (b.getWidth() * 2f);
            Bitmap bitmap1Temp = scaleBitmap(b, scale);
            canvas.drawBitmap(bitmap1Temp, i * width / 2, height / 4, null);
            if (!bitmap1Temp.isRecycled()) {
                bitmap1Temp.recycle();
            }
        }
        canvas.save();
        canvas.restore();
        return bitmap;
    }

    private Bitmap getGroupBitmapFrom3() {
        //  上：(2/Math.sqrt(3),0)   左下：(1/Math.sqrt(3),Math.sqrt(3))  右下：(2+1/Math.sqrt(3),Math.sqrt(3))
        //  小圆r：a   大圆半径r：(2/Math.sqrt(3)+1)a
        for (int i = 0; i < list.size(); i++) {
            Bitmap b = list.get(i);
            float standardWidth = width / 2.15f;
            Bitmap bitmap1Temp = scaleBitmap(b, ((float) width) / (b.getWidth() * 2.15f));
            if (i == 0) {
                canvas.drawBitmap(bitmap1Temp, 1.15f * standardWidth / 2, 0, null);
            } else if (i == 1) {
                canvas.drawBitmap(bitmap1Temp, 0.155f * standardWidth / 2, 1.732f * standardWidth / 2, null);
            } else {
                canvas.drawBitmap(bitmap1Temp, 2.155f * standardWidth / 2, 1.732f * standardWidth / 2, null);
            }
            if (!bitmap1Temp.isRecycled()) {
                bitmap1Temp.recycle();
            }
        }
        canvas.save();
        canvas.restore();
        return bitmap;
    }

    private Bitmap getGroupBitmapFrom4() {
        // 小圆半径r：a 大圆半径r：(Math.sqrt(2)+1)a
        for (int i = 0; i < 4; i++) {
            Bitmap b = list.get(i);
            float standardWidth = width / 2.414f;
            Bitmap bitmap1Temp = scaleBitmap(b, ((float) width) / (b.getWidth() * 2.414f));
            if (i == 0) {
                canvas.drawBitmap(bitmap1Temp, 0.414f * standardWidth / 2, 0.414f * standardWidth / 2, null);
            } else if (i == 1) {
                canvas.drawBitmap(bitmap1Temp, 2.414f * standardWidth / 2, 0.414f * standardWidth / 2, null);
            } else if (i == 2) {
                canvas.drawBitmap(bitmap1Temp, 0.414f * standardWidth / 2, 2.414f * standardWidth / 2, null);
            } else {
                canvas.drawBitmap(bitmap1Temp, 2.414f * standardWidth / 2, 2.414f * standardWidth / 2, null);
            }
            if (!bitmap1Temp.isRecycled()) {
                bitmap1Temp.recycle();
            }
        }
        canvas.save();
        canvas.restore();
        return bitmap;
    }

    public void recycleBitmap() {
        if (bitmap != null && !bitmap.isRecycled()) {
            canvas = null;
            bitmap.recycle();
            bitmap = null;
        }
    }

    private Bitmap scaleBitmap(Bitmap bitmap, float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale); //长和宽放大缩小的比例
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

}
