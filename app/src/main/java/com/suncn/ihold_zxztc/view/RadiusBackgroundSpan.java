package com.suncn.ihold_zxztc.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.style.ReplacementSpan;
import android.util.Log;

import com.gavin.giframe.utils.GILogUtil;

public class RadiusBackgroundSpan extends ReplacementSpan {

    private int mSize;
    private int mColors;
    private int mColore;
    private int mRadius;

    /**
     * @param radius 圆角半径
     */
    public RadiusBackgroundSpan(int colors, int colore, int radius) {
        mColors = colors;
        mColore = colore;
        mRadius = radius;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        mSize = (int) (paint.measureText(text, start, end) + 2 * mRadius);
        //mSize就是span的宽度，span有多宽，开发者可以在这里随便定义规则
        //我的规则：这里text传入的是SpannableString，start，end对应setSpan方法相关参数
        //可以根据传入起始截至位置获得截取文字的宽度，最后加上左右两个圆角的半径得到span宽度
        return mSize;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        int color = paint.getColor();//保存文字颜色
        GILogUtil.e("text=====",text);
//        /**
//         * 两色渐变
//         */
        Shader shader = new LinearGradient(0, 0, x, y, mColors, mColore, Shader.TileMode.CLAMP);
//       Shader shader = new LinearGradient(100,100,getWidth()-100,300, Color.parseColor("#E91E63"),Color.parseColor("#E91E63"),Shader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);// 设置画笔的锯齿效果
        RectF oval = new RectF(x, y + paint.ascent(), x + mSize, y + paint.descent());
        //设置文字背景矩形，x为span其实左上角相对整个TextView的x值，y为span左上角相对整个View的y值。paint.ascent()获得文字上边缘，paint.descent()获得文字下边缘
        canvas.drawRoundRect(oval, mRadius, mRadius, paint);//绘制圆角矩形，第二个参数是x半径，第三个参数是y半径
        paint.setColor(color);//恢复画笔的文字颜色
        canvas.drawText(text, start, end, x + mRadius, y, paint);//绘制文字
//        paint.setColor(mColore);//设置背景颜色
//        paint.setAntiAlias(true);// 设置画笔的锯齿效果
//        Log.i("pyt", y + "");
//        RectF oval = new RectF(x, y + paint.ascent(), x + mSize, y + paint.descent());
//        //设置文字背景矩形，x为span其实左上角相对整个TextView的x值，y为span左上角相对整个View的y值。paint.ascent()获得文字上边缘，paint.descent()获得文字下边缘
//        canvas.drawRoundRect(oval, mRadius, mRadius, paint);//绘制圆角矩形，第二个参数是x半径，第三个参数是y半径
//        paint.setColor(color);//恢复画笔的文字颜色
//        canvas.drawText(text, start, end, x + mRadius, y, paint);//绘制文字
    }
}