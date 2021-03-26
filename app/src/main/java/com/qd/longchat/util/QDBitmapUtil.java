package com.qd.longchat.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.Toast;

import com.longchat.base.QDClient;
import com.longchat.base.callback.QDFileDownLoadCallBack;
import com.longchat.base.manager.QDFileManager;
import com.qd.longchat.R;
import com.qd.longchat.config.QDStorePath;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/5/31 下午4:27
 */

public class QDBitmapUtil {

    private final static int BITMAP_SIZE = 50;
    private final static int TEXT_SIZE = 28;

    private static QDBitmapUtil instance;
    private LruCache<String, Bitmap> cache;

    public QDBitmapUtil() {
        long maxMemory = Runtime.getRuntime().maxMemory();
        int cacheSize = (int) (maxMemory / 8);
        cache = new LruCache<>(cacheSize);
    }

    public void logOut() {
        cache.evictAll();
        cache = null;
        instance = null;
    }

    public static QDBitmapUtil getInstance() {
        if (instance == null) {
            instance = new QDBitmapUtil();
        }
        return instance;
    }

    public Bitmap getBitmapFromCache(String id) {
        return cache.get(id);
    }

    /**
     * 创建头像
     *
     * @param context
     * @param id
     * @param name
     * @param imageView
     */
    public void createAvatar(Context context, String id, String name, ImageView imageView) {
        createAvatar(context, id, name, "", imageView);
    }

    public void createAvatar(final Context context, final String id, final String name, String icon, final ImageView imageView) {
        if (QDStorePath.ICON_PATH == null) {
            Bitmap bitmap = createNameAvatar(context, id, name);
            imageView.setImageBitmap(bitmap);
        } else {
            String tempPath;
            if (TextUtils.isEmpty(icon)) {
                tempPath = QDStorePath.ICON_PATH + id + "_" + name + ".png_temp";
            } else {
                String fileName = icon.substring(icon.lastIndexOf("/") + 1);
                if (fileName.contains(".")) {
                    fileName = fileName.substring(0, fileName.lastIndexOf("."));
                }
                tempPath = QDStorePath.ICON_PATH + id + "_" + name + "_" + fileName + ".png_temp";
            }
            final String path = tempPath;
            File file = new File(path);
            if (!file.exists()) {
                if (TextUtils.isEmpty(icon)) {
                    Bitmap bitmap = createNameAvatar(context, id, name);
                    imageView.setImageBitmap(bitmap);
                } else {
                    if (!icon.startsWith("http://") && !icon.startsWith("https://")) {
                        icon = QDUtil.getWebFileServer() + icon;
                    }
                    if (!com.longchat.base.util.QDUtil.isNetworkAvailable(context) || !QDClient.getInstance().isOnline()) {
                        Bitmap bitmap = createNameAvatar(context, id, name);
                        imageView.setImageBitmap(bitmap);
                    } else {
                       // imageView.setImageResource(R.mipmap.img_person);
                        QDFileManager.getInstance().downloadFile(path, icon, new QDFileDownLoadCallBack() {
                            @Override
                            public void onDownLoading(int per) {
                            }

                            @Override
                            public void onDownLoadSuccess() {
                                Bitmap bitmap = createAvatarBitmap(path);
                                if (bitmap == null) {
                                    bitmap = createNameAvatar(context, id, name);
                                }
                                cache.put(id, bitmap);
                                imageView.setImageBitmap(bitmap);
                            }

                            @Override
                            public void onDownLoadFailed(String msg) {
                                Bitmap bitmap = createNameAvatar(context, id, name);
                                imageView.setImageBitmap(bitmap);
                            }
                        });
                    }
                }
            } else {
                if (cache.get(id) == null) {
                    Bitmap bitmap = createAvatarBitmap(path);
                    if (bitmap == null) {
                        Bitmap b = createNameAvatar(context, id, name);
                        cache.put(id, b);
                        imageView.setImageBitmap(b);
                    } else {
                        cache.put(id, bitmap);
                        imageView.setImageBitmap(bitmap);
                    }
                } else {
                    imageView.setImageBitmap(cache.get(id));
                }
            }
        }
    }

    /**
     * @param path
     * @return
     */
    public Bitmap createAvatarBitmap(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    public Bitmap createNameAvatar(Context context, String id, String name) {
        String str;
        if (!TextUtils.isEmpty(name)) {
            str = name.substring(0, 1).toUpperCase();
        } else {
            str = "无";
        }
        int color = getColor(context, id);
        int bitmapSize = QDUtil.dp2px(context, BITMAP_SIZE);
        Bitmap bitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(color);
        int height = canvas.getHeight();
        int width = canvas.getWidth();
        Paint paint = new Paint();
        paint.setTextSize(QDUtil.sp2px(context, TEXT_SIZE));
        paint.setColor(Color.WHITE);
        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
        canvas.drawText(str,
                width / 2 - paint.measureText(str) / 2,
                height / 2 - fm.descent + (fm.bottom - fm.top) / 2, paint);
        return bitmap;
    }

    private int getColor(Context context, String id) {
        if (TextUtils.isEmpty(id) || !TextUtils.isDigitsOnly(id)) {
            return context.getResources().getColor(R.color.colorNavigationText);
        }
//        int i = Integer.valueOf(id) % 5;
//        switch (i) {
//            case 0:
//                return context.getResources().getColor(R.color.colorDefaultUserPhoto1);
//            case 1:
//                return context.getResources().getColor(R.color.colorDefaultUserPhoto2);
//            case 2:
//                return context.getResources().getColor(R.color.colorDefaultUserPhoto3);
//            case 3:
//                return context.getResources().getColor(R.color.colorDefaultUserPhoto4);
//            case 4:
//                return context.getResources().getColor(R.color.colorDefaultUserPhoto5);
//        }
        return context.getResources().getColor(R.color.colorNavigationText);
    }

    /**
     * 画圆形图片
     *
     * @param bitmap
     * @return
     */
    public static Bitmap makeRoundCorner(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int left = 0, top = 0, right = width, bottom = height;
        float roundPx = height / 2;
        if (width > height) {
            left = (width - height) / 2;
            top = 0;
            right = left + height;
            bottom = height;
        } else if (height > width) {
            left = 0;
            top = (height - width) / 2;
            right = width;
            bottom = top + width;
            roundPx = width / 2;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        Rect rect = new Rect(left, top, right, bottom);
        RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 保存图片
     *
     * @param bitmap
     * @return
     */
    public static String saveBitmap(Bitmap bitmap) {
        String path = QDStorePath.IMG_PATH;
        File avaterFile = new File(path, System.currentTimeMillis() + ".png");//设置文件名称
        if (avaterFile.exists()) {
            avaterFile.delete();
        }
        try {
            avaterFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(avaterFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return avaterFile.getAbsolutePath();
    }

    public static String saveBitmap(Bitmap bitmap, String fileName, Context context, boolean isSend) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String path;
        if (isSend) {
            path = QDStorePath.TEMP_PATH + File.separator + fileName + "_" + timeStamp + ".jpg";
        } else {
            path = QDStorePath.IMG_PATH + File.separator + fileName + "_" + timeStamp + ".jpg";
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            if (!isSend) {
                Toast.makeText(context, "已保存到本地", Toast.LENGTH_SHORT).show();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * 添加文字到图片左下角
     *
     * @param context
     * @param bitmap
     * @param strings
     * @return
     */
    public static Bitmap drawTextToBitmap(Context context, Bitmap bitmap, String... strings) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        int screenHeight = (metrics.heightPixels * 4) / 5;
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float widthScale = (float) screenWidth / bitmapWidth;
        float heightScale = (float) screenHeight / bitmapHeight;
        matrix.postScale(widthScale, heightScale);
        Bitmap src = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
        Bitmap result = src.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setTextSize(40);
        paint.setColor(Color.WHITE);
        int textHeight = 0;
        for (int i = 0; i < strings.length; i++) {
            String str = strings[i];
            Rect bounds = new Rect();
            paint.getTextBounds(str, 0, str.length(), bounds);
            if (i == 0) {
                textHeight = bounds.height();
            }
            canvas.drawText(str, 20, result.getHeight() - textHeight * (i + 1) - 40 * (i + 1), paint);
        }
        canvas.save();
        canvas.restore();
        bitmap.recycle();
        return result;
    }

    public static Bitmap writeStringToBitmap(Context context, Bitmap bitmap, String... strs) {
        Bitmap b = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(b);
        int height = b.getHeight();
        Paint paint = new Paint();
        paint.setTextSize(QDUtil.sp2px(context, 40));
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        for (int i = 0; i < strs.length; i++) {
            String str = strs[i];
            canvas.drawText(str, QDUtil.dp2px(context, 20), height - QDUtil.sp2px(context, 30) - i * QDUtil.sp2px(context, 20), paint);
        }
        return b;
    }

    /**
     * 添加水印
     *
     * @param context
     * @param str
     * @return
     */
    public static BitmapDrawable addWaterMark(Context context, String str, int color) {
        Paint paint = new Paint();
        paint.setTextSize(QDUtil.sp2px(context, 10));
        paint.setColor(context.getResources().getColor(R.color.colorWaterMark));
        paint.setAntiAlias(true);

        Rect bounds = new Rect();
        paint.getTextBounds(str, 0, str.length(), bounds);

        int textLength = bounds.width();

        int height = textLength / 2;

        int width = (int) Math.round(Math.sqrt(textLength * textLength - height * height));

        Bitmap bitmap = Bitmap.createBitmap(width + QDUtil.dp2px(context, 40), height + QDUtil.dp2px(context, 40), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(color);
        int bWidth = bitmap.getWidth();
        int bHeight = bitmap.getHeight();

        Path path = new Path();
        path.moveTo(0, bHeight);
        path.lineTo(bWidth, 0);
        canvas.drawTextOnPath(str, path, 15, 0, paint);

//        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
//        int baseline = (bitmap.getHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
//        canvas.drawText(str, bitmap.getWidth() / 2 - bounds.width() / 2, baseline, paint);
//
//        Matrix matrix = new Matrix();
//        matrix.setRotate(0, bitmap.getWidth() / 2 , bitmap.getHeight()/2);

        Bitmap b = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());

        BitmapDrawable drawable = new BitmapDrawable(context.getResources(), b);
        drawable.setDither(true);
        drawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        return drawable;
    }


    public Bitmap getBitmap(final Context context, final String id, final String name, String icon) {
        if (QDStorePath.ICON_PATH == null) {
            Bitmap bitmap = makeRoundCorner(createNameAvatar(context, id, name));
            return bitmap;
        } else {
            String tempPath;
            if (TextUtils.isEmpty(icon)) {
                tempPath = QDStorePath.ICON_PATH + id + "_" + name + ".png_temp";
            } else {
                String fileName = icon.substring(icon.lastIndexOf("/") + 1);
                if (fileName.contains(".")) {
                    fileName = fileName.substring(0, fileName.lastIndexOf("."));
                }
                tempPath = QDStorePath.ICON_PATH + id + "_" + name + "_" + fileName + ".png_temp";
            }

            Bitmap bitmap = makeRoundCorner(cache.get(id));
            if (bitmap == null) {
                File file = new File(tempPath);
                if (file.exists()) {
                    bitmap = makeRoundCorner(createAvatarBitmap(tempPath));
                } else {
                    bitmap = makeRoundCorner(createNameAvatar(context, id, name));
                }
            }
            return bitmap;
        }
    }
}
