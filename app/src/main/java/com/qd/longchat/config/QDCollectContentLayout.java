package com.qd.longchat.config;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.google.gson.JsonObject;
import com.longchat.base.btf.IMBTFAT;
import com.longchat.base.btf.IMBTFFile;
import com.longchat.base.btf.IMBTFItem;
import com.longchat.base.btf.IMBTFLoc;
import com.longchat.base.btf.IMBTFManager;
import com.longchat.base.btf.IMBTFText;
import com.longchat.base.callback.QDFileDownLoadCallBack;
import com.longchat.base.manager.QDFileManager;
import com.longchat.base.model.QDCollectMessage;
import com.longchat.base.model.gd.QDAccHistoryModel;
import com.longchat.base.util.QDGson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qd.longchat.R;
import com.qd.longchat.activity.QDWebActivity;
import com.qd.longchat.application.QDApplication;
import com.qd.longchat.util.QDAtClickSpan;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;
import com.qd.longchat.widget.QDChatSmiley;

import java.io.File;
import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/7/10 上午9:50
 */
public class QDCollectContentLayout {

    public static TextView createTextView(Context context, QDCollectMessage message) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView textView = new TextView(context);
        textView.setTextSize(18);
        textView.setTextColor(Color.BLACK);
        String content = message.getContent();
        IMBTFManager manager = new IMBTFManager(content);
        List<IMBTFItem> imbtfItemList = manager.getItemList();
        for (IMBTFItem item : imbtfItemList) {
            if (item instanceof IMBTFText) {
                textView.append(QDChatSmiley.getInstance(context).strToSmiley(((IMBTFText) item).getContent()));
            } else if (item instanceof IMBTFAT) {
                String info = ((IMBTFAT) item).getContent();
                String[] infos = info.split(";");
                String userId = infos[0];
                String userName = infos[1];
                SpannableString spannableString = new SpannableString("@" + userName);
                QDAtClickSpan span = new QDAtClickSpan(context, context.getResources().getColor(R.color.colorBtnBlue), userId, userName);
                spannableString.setSpan(span, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.append(spannableString);
            }
        }
        textView.setLayoutParams(params);
        return textView;
    }

    public static View createLocationView(Context context, QDCollectMessage message) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_collect_location, null);
        TextView tvLocationTitle = view.findViewById(R.id.tv_location_title);
        TextView tvLocationInfo = view.findViewById(R.id.tv_location_info);
        IMBTFLoc loc = IMBTFLoc.fromBTFXml(message.getContent());
        String locInfo = loc.getContent();
        final String[] infos = locInfo.split(";");
        tvLocationTitle.setText(infos[2]);
        tvLocationInfo.setText(infos[3]);
        return view;
    }

    public static ImageView createImageView(Context context, QDCollectMessage message) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.mipmap.ic_download_loading);
        imageView.setLayoutParams(params);
        final String imagePath = QDStorePath.COLLECT_PATH + message.getGid() + ".png";
        File file = new File(imagePath);
        if (file.exists()) {
            Bitmap bitmap = decodeBitmap(imagePath);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageResource(R.mipmap.ic_download_failed);
            }
            imageView.setBackgroundColor(Color.RED);
        } else {
            String content = message.getContent();
            IMBTFFile imageFile = IMBTFFile.fromBTFXml(content);
            String imageUrl = imageFile.getFsHost() + imageFile.getOriginal();

            QDFileManager.getInstance().downloadFile(imagePath, imageUrl, new QDFileDownLoadCallBack() {
                @Override
                public void onDownLoading(int per) {

                }

                @Override
                public void onDownLoadSuccess() {
                    Bitmap bitmap = decodeBitmap(imagePath);
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    } else {
                        imageView.setImageResource(R.mipmap.ic_download_failed);
                    }
                }

                @Override
                public void onDownLoadFailed(String msg) {
                    imageView.setImageResource(R.mipmap.ic_download_failed);
                }
            });
        }
        return imageView;
    }

    public static View createFileView(Context context, QDCollectMessage message) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_collect_file, null);
        ImageView ivIcon = view.findViewById(R.id.iv_file_icon);
        TextView tvTitle = view.findViewById(R.id.tv_file_name);
        TextView tvDesc = view.findViewById(R.id.tv_file_suffix);
        TextView tvInfo = view.findViewById(R.id.tv_file_size);

        String content = message.getContent();
        IMBTFFile imageFile = IMBTFFile.fromBTFXml(content);
        String name = imageFile.getName();
        int resId = QDUtil.getFileIconByName(context, name);
        ivIcon.setImageResource(resId);
        if (name.contains(".")) {
            int index = name.lastIndexOf(".");
            tvTitle.setText(name.substring(0, index));
            tvDesc.setText(name.substring(index + 1));
        } else {
            tvTitle.setText(name);
            tvDesc.setText("Unknown");
        }
        tvInfo.setText(QDUtil.changFileSizeToString(imageFile.getSize()));

        String path =  QDStorePath.COLLECT_PATH + message.getGid() + QDUtil.getSuffix(name);
        File file = new File(path);
        if (!file.exists()) {
            String url = imageFile.getFsHost() + imageFile.getOriginal();
            QDFileManager.getInstance().downloadFile(path, url, new QDFileDownLoadCallBack() {
                @Override
                public void onDownLoading(int per) {

                }

                @Override
                public void onDownLoadSuccess() {
                }

                @Override
                public void onDownLoadFailed(String msg) {
                }
            });
        }


        return view;
    }

    public static View createVoiceView(final Context context, QDCollectMessage message) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_collect_voice, null);
        final TextView tvTime = view.findViewById(R.id.tv_voice_time);
        final String content = message.getContent();
        IMBTFFile voiceFile = IMBTFFile.fromBTFXml(content);
        final String voicePath = QDStorePath.COLLECT_PATH + message.getGid() + QDUtil.getSuffix(voiceFile.getName());
        File file = new File(voicePath);
        if (file.exists()) {
            int seconds = QDUtil.getRecordFileDuration(voicePath);
            tvTime.setText(QDUtil.secondToMinutes(seconds));
        } else {
            String url = voiceFile.getFsHost() + voiceFile.getOriginal();
            QDFileManager.getInstance().downloadFile(voicePath, url, new QDFileDownLoadCallBack() {
                @Override
                public void onDownLoading(int per) {

                }

                @Override
                public void onDownLoadSuccess() {
                    int seconds = QDUtil.getRecordFileDuration(voicePath);
                    tvTime.setText(QDUtil.secondToMinutes(seconds));
                }

                @Override
                public void onDownLoadFailed(String msg) {
                    QDUtil.showToast(context, context.getResources().getString(R.string.file_download_failed));
                }
            });
        }
        return view;
    }

    public static View createVideoView(Context context, QDCollectMessage message) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(context).inflate(R.layout.item_collect_video, null);
        view.setLayoutParams(params);
        final ImageView ivVideo = view.findViewById(R.id.iv_video_img);
        ivVideo.setImageResource(R.mipmap.ic_download_loading);
        String content = message.getContent();
        IMBTFFile videoFile = IMBTFFile.fromBTFXml(content);
        final String videoPath = QDStorePath.COLLECT_PATH + message.getGid() + QDUtil.getSuffix(videoFile.getName());
        File file = new File(videoPath);
        if (file.exists()) {
            ivVideo.setImageBitmap(QDUtil.getVideoFirstPic(videoPath));
        } else {
            String url = videoFile.getFsHost() + videoFile.getOriginal();
            QDFileManager.getInstance().downloadFile(videoPath, url, new QDFileDownLoadCallBack() {
                @Override
                public void onDownLoading(int per) {

                }

                @Override
                public void onDownLoadSuccess() {
                    Bitmap bitmap = QDUtil.getVideoFirstPic(videoPath);
                    if (bitmap == null) {
                        ivVideo.setImageResource(R.mipmap.ic_download_failed);
                    } else {
                        ivVideo.setImageBitmap(bitmap);
                    }
                }

                @Override
                public void onDownLoadFailed(String msg) {
                    ivVideo.setImageResource(R.mipmap.ic_download_failed);
                }
            });
        }
        return view;
    }

    public static View createLinkView(final Context context, QDCollectMessage message) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_collect_custom, null);
        RoundedImageView ivIcon = view.findViewById(R.id.iv_custom_icon);
        TextView tvTitle = view.findViewById(R.id.tv_custom_title);

        ivIcon.setRadius(10);
        final JsonObject jsonObject = QDGson.getGson().fromJson(message.getContent(), JsonObject.class);
        ImageLoader.getInstance().displayImage(jsonObject.get("icon").getAsString(), ivIcon, QDApplication.options);
        tvTitle.setText(jsonObject.get("title").getAsString());
        final String url = jsonObject.get("url_mobile").getAsString();
        final int tartgetType = jsonObject.get("mobile_target_type").getAsInt();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QDAccHistoryModel model = new QDAccHistoryModel();
                model.setPcUrl(jsonObject.get("url_pc").getAsString());
                model.setCover(jsonObject.get("icon").getAsString());
                model.setTitle(jsonObject.get("title").getAsString());
                model.setIntro(jsonObject.get("desc").getAsString().replace("\n", ""));
                model.setMobileUrl(url);
                toWebActivity(context, url, tartgetType, model);
            }
        });
        return view;
    }

    private static void toWebActivity(Context context, String url, int type, QDAccHistoryModel model) {
        if (type == 1) {
            Intent intent = new Intent(context, QDWebActivity.class);
            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_WEB_URL, url);
            intent.putExtra(QDIntentKeyUtil.INTENT_ACC_ARTICLE, model);
            context.startActivity(intent);
        } else {
            QDUtil.toBrowser((Activity) context, url);
        }
    }

    private static Bitmap decodeBitmap(String path) {

        int BITMAP_SIZE = 200;

        Bitmap bitmap;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);

        int picWidth = opts.outWidth;
        int picHeight = opts.outHeight;

        opts.inSampleSize = 1;
        if (picWidth >= picHeight && picWidth > BITMAP_SIZE) {
            opts.inSampleSize = picWidth / BITMAP_SIZE;
        } else {
            if (picHeight > picWidth && picHeight > BITMAP_SIZE) {
                opts.inSampleSize = picHeight / BITMAP_SIZE;
            }
        }

        opts.inJustDecodeBounds = false;

        bitmap = BitmapFactory.decodeFile(path, opts);

        if (bitmap == null) {
            return null;
        } else {
            return Bitmap.createScaledBitmap(bitmap, BITMAP_SIZE, BITMAP_SIZE, true);
        }
    }
}
