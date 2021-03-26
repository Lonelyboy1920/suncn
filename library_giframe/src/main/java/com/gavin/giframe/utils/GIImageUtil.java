package com.gavin.giframe.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gavin.giframe.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 图片相关处理工具类
 */
public class GIImageUtil {
    /**
     * 根据uri取得系统中图片
     */
    public static Bitmap getCameraBmp(Context context, Uri uri) {
        Bitmap bmp = null;
        try {
            ContentResolver resolver = context.getContentResolver();
            bmp = MediaStore.Images.Media.getBitmap(resolver, uri);
            float scale = 200000.0f / (bmp.getWidth() * bmp.getHeight());
            if (scale < 1.0f) {
                scale = (float) Math.sqrt(scale);
                bmp = Bitmap.createScaledBitmap(bmp, (int) (bmp.getWidth() * scale), (int) (bmp.getHeight() * scale), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    /**
     * 用当前时间给取得的图片命名
     */
    public static String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss", Locale.getDefault());
        return dateFormat.format(date) + ".JPEG";
    }

    /**
     * 保存缓存图片
     */
    public static File saveMyBitmap(Context context, Bitmap mBitmap) {
        File destDir = new File(GIFileCst.DIR_IMAGE);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        File f = new File(GIFileUtil.createFile(context, getPhotoFileName()));
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.toString();
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    /**
     * 根据uri加载图片（Glide）
     */
    public static void loadImg(Activity context, Uri uri, ImageView imageView, Drawable defaultImg) {
        if (!context.isDestroyed()) {
            Glide.with(context)
                    .load(uri)
//                    .skipMemoryCache(true)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(defaultImg).error(defaultImg))
                    .into(imageView);
        }
    }

    /**
     * 根据uri加载图片（Glide）
     */
    public static void loadImg(Context context, Uri uri, ImageView imageView, Drawable defaultImg) {
        Glide.with(context)
                .load(uri)
//                    .skipMemoryCache(true)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(defaultImg).error(defaultImg))
                .into(imageView);
    }

    /**
     * 加载图片（Glide）
     */
    public static void loadImg(Context context, ImageView imageView, String url, Drawable defaultImg) {
        Glide.with(context)
                .asBitmap()//只加载静态图片，如果是git图片则只加载第一帧。
                .load(url)
//                .skipMemoryCache(false)//跳过内存缓存
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(defaultImg).error(defaultImg))
                .into(imageView);
    }

    /**
     * 加载图片（Glide）
     */
    public static void loadImg(Context context, ImageView imageView, String url, int defaultImgType) {
        Drawable defaultImg;
        Drawable defaultErrorImg;
        if (defaultImgType == 1) {
            defaultImg = context.getResources().getDrawable(R.mipmap.img_person);
            defaultErrorImg = context.getResources().getDrawable(R.mipmap.img_person);
        } else {
            defaultImg = context.getResources().getDrawable(R.mipmap.img_default);
            defaultErrorImg = context.getResources().getDrawable(R.mipmap.img_error);
        }
        Glide.with(context)
                .asBitmap()//只加载静态图片，如果是git图片则只加载第一帧。
                .load(url)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(defaultImg).error(defaultErrorImg))
//                .skipMemoryCache(false)//跳过内存缓存
                .into(imageView);
    }

    /**
     * 给View动态设置背景图片（Glide）
     */
    public static void setBgForImage(Context context, final View view, String url) {
        Glide.with(context)
                .asBitmap()
                .load(url)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Drawable drawable = new BitmapDrawable(resource);
                        view.setBackground(drawable);   //设置背景
                    }
                });
    }

    /**
     * 设置图片显示比例（圈子）
     */
    public static void loadImgNoTrans(final Context context, final ImageView imageView, final String url) {
        final Drawable defaultImg;
        final Drawable defaultErrorImg;
        defaultImg = context.getResources().getDrawable(R.mipmap.img_default);
        defaultErrorImg = context.getResources().getDrawable(R.mipmap.img_error);
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap bitmap = null;
                try {
                    bitmap = Glide.with(context)
                            .asBitmap()
                            .load(url)
                            .submit().get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap resource) {
                //获取原图的宽高
                if (resource == null) {
                    Glide.with(context)
                            .load("")
//                .skipMemoryCache(false)//跳过内存缓存
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(defaultImg)
                                    .error(defaultErrorImg).skipMemoryCache(false).centerCrop())
                            .into(imageView);
                    return;
                }
                int width = resource.getWidth();
                int height = resource.getHeight();
                int imageViewWidth = 450;
                int screenWidth = GIDensityUtil.getScreenW(context);
                //如果是宽图
                if (height > width) {
                    imageViewWidth = (screenWidth / 5) * 2;
                } else if (width > (screenWidth / 4) * 3) {
                    imageViewWidth = (screenWidth / 4) * 3;
                } else {
                    imageViewWidth = width;
                }
                //计算缩放比例
                float sy = (float) (imageViewWidth * 0.1) / (float) (width * 0.1);

                //计算图片等比例放大后的高
                int imageViewHeight = (int) (height * sy);
                ViewGroup.LayoutParams params = imageView.getLayoutParams();
                params.width = imageViewWidth;
                params.height = imageViewHeight;
                imageView.setLayoutParams(params);
                Glide.with(context)
                        .load(url)
//                .skipMemoryCache(false)//跳过内存缓存
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(defaultImg)
                                .error(defaultErrorImg).skipMemoryCache(false).centerCrop())
                        .into(imageView);
            }
        }.execute();
    }

    /**
     * 压缩图片
     */
//    public static Bitmap getSmallBitmap(Context context, Uri imageFilePath) {
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        try {
//            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imageFilePath), null, options);
//            options.inSampleSize = calculateInSampleSize(options, 1280, 720);
//            options.inJustDecodeBounds = false;
//            return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imageFilePath), null, options);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    /**
     * 计算InSampleSize
     * 宽的压缩比和高的压缩比的较小值  取接近的2的次幂的值
     * 比如宽的压缩比是3 高的压缩比是5 取较小值3  而InSampleSize必须是2的次幂，取接近的2的次幂4
     *
     * @param options
     * @param reqWidth  期望的宽
     * @param reqHeight 期望的高
     * @return
     */
//    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        int inSampleSize = 1;
//
//        if (height > reqHeight || width > reqWidth) {
//            final int heightRatio = Math.round((float) height / (float) reqHeight);
//            final int widthRatio = Math.round((float) width / (float) reqWidth);
//            int ratio = heightRatio < widthRatio ? widthRatio : heightRatio;
//            // inSampleSize只能是2的次幂  将ratio就近取2的次幂的值
//            if (ratio < 3)
//                inSampleSize = ratio;
//            else if (ratio < 6.5)
//                inSampleSize = 4;
//            else if (ratio < 8)
//                inSampleSize = 8;
//            else
//                inSampleSize = ratio;
//        }
//        return inSampleSize;
//    }

    /**
     * 获取视频的缩略图
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     *
     * @param videoPath 视频的路径
     * @param width     指定输出视频缩略图的宽度
     * @param height    指定输出视频缩略图的高度度
     * @param kind      参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图
     */
    private static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        System.out.println("w" + bitmap.getWidth());
        System.out.println("h" + bitmap.getHeight());
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    public static Bitmap getVideoThumbnail(String videoPath) {
        return getVideoThumbnail(videoPath, 200, 200, MediaStore.Images.Thumbnails.MINI_KIND);
    }

    /**
     * 根据指定的图像路径和大小来获取缩略图
     * 此方法有两点好处：
     * 1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
     * 第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。
     * 2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使
     * 用这个工具生成的图像不会被拉伸。
     *
     * @param imagePath 图像的路径
     * @param width     指定输出图像的宽度
     * @param height    指定输出图像的高度
     * @return 生成的缩略图
     */
    private static Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    public static Bitmap getImageThumbnail(String imagePath) {
        return getImageThumbnail(imagePath, 200, 200);
    }

    /**
     * 保存位图到本地
     */
    public static void savaImage(Context context, Bitmap bitmap) {
        String galleryPath = Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_DCIM
                + File.separator + "Camera" + File.separator;
//                + File.separator + "yingtan" + File.separator;
        File file = new File(galleryPath);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
        FileOutputStream fileOutputStream = null;
        String filePhth;
        String fileName;
        //文件夹不存在，则创建它
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            filePhth = galleryPath + "/" + System.currentTimeMillis() + ".png";
            fileName = System.currentTimeMillis() + "";
            File file1 = new File(filePhth);
            fileOutputStream = new FileOutputStream(file1.getPath());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
            //图片路径
//            MediaStore.Images.Media.insertImage(context.getContentResolver(),
//                    filePhth,fileName , null);
            MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "", "");
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file1.getAbsolutePath())));
//            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
            GIToastUtil.showMessage(context, "保存成功");
        } catch (Exception e) {
            GIToastUtil.showMessage(context, "保存失败");
            e.printStackTrace();
        }
    }

    /**
     * bitmap转为base64
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * base64转为bitmap
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * 通过uri获取图片并进行压缩
     */
    public static Bitmap getBitmapFormUri(Activity ac, Uri uri) throws IOException {
        InputStream input = ac.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1))
            return null;
        //比例压缩
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = 2;//设置缩放比例
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = ac.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();

        return compressImage(bitmap);//再进行质量压缩
    }

    /**
     * 质量压缩方法
     */
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 1024) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }
}
