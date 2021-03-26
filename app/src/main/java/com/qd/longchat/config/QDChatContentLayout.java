package com.qd.longchat.config;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.LruCache;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.google.gson.JsonObject;
import com.longchat.base.QDClient;
import com.longchat.base.btf.IMBTFAT;
import com.longchat.base.btf.IMBTFFile;
import com.longchat.base.btf.IMBTFItem;
import com.longchat.base.btf.IMBTFLoc;
import com.longchat.base.btf.IMBTFManager;
import com.longchat.base.btf.IMBTFText;
import com.longchat.base.callback.QDFileCallBack;
import com.longchat.base.callback.QDFileDownLoadCallBack;
import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.command.QDBaseCommand;
import com.longchat.base.config.QDSDKConfig;
import com.longchat.base.dao.QDGroup;
import com.longchat.base.dao.QDMessage;
import com.longchat.base.databases.QDGroupHelper;
import com.longchat.base.databases.QDMessageHelper;
import com.longchat.base.manager.QDFileManager;
import com.longchat.base.model.QDCollectMessage;
import com.longchat.base.model.QDRevokeMsg;
import com.longchat.base.model.gd.QDAccHistoryModel;
import com.longchat.base.model.gd.QDFileModel;
import com.longchat.base.util.QDGson;
import com.longchat.base.util.QDStringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qd.longchat.R;
import com.qd.longchat.activity.QDChatActivity;
import com.qd.longchat.activity.QDContactActivity;
import com.qd.longchat.activity.QDGaodeLocationInfoActivity;
import com.qd.longchat.activity.QDPicActivity;
import com.qd.longchat.activity.QDPlayerVideoActivity;
import com.qd.longchat.activity.QDSelectContactActivity;
import com.qd.longchat.activity.QDUserDetailActivity;
import com.qd.longchat.activity.QDWebActivity;
import com.qd.longchat.application.QDApplication;
import com.qd.longchat.model.QDLocationBean;
import com.qd.longchat.model.QDPersonCard;
import com.qd.longchat.util.QDAtClickSpan;
import com.qd.longchat.util.QDBitmapUtil;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDRecorderUtil;
import com.qd.longchat.util.QDUtil;
import com.qd.longchat.view.QDCopyPopupList;
import com.qd.longchat.view.QDImageView;

import com.qd.longchat.widget.QDChatSmiley;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/6/7 下午2:03
 */
public class QDChatContentLayout {

    private LruCache<String, Bitmap> cache;

    public static View createTextView(Context context, QDMessage message) {
        TextView textView = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int textSize = QDSDKConfig.getInstance().getMsgTextSize();
        textView.setTextSize(textSize);
        textView.setMaxWidth(QDUtil.dp2px(context, 250));
        int direction = message.getDirection();
        if (direction == QDMessage.DIRECTION_IN) {
            textView.setTextColor(Color.BLACK);
            textView.setBackgroundResource(R.mipmap.im_chat_msg_item_content_left_normal);
        } else {
            textView.setTextColor(Color.WHITE);
            textView.setBackgroundResource(R.mipmap.im_chat_msg_item_content_right_normal);
        }
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setAutoLinkMask(Linkify.ALL);
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
                SpannableString spannableString = new SpannableString("@" + userName + " ");
                QDAtClickSpan span = new QDAtClickSpan(context, context.getResources().getColor(R.color.colorBtnBlue), userId, userName);
                spannableString.setSpan(span, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.append(spannableString);
            }
        }
        textView.setGravity(Gravity.LEFT);
        textView.setLayoutParams(params);
        showPopupWindow(context, textView, message);
        return textView;
    }

    private static void downloadImage(Context context, QDMessage message, String path, String url, ImageView imageView) {
        QDFileManager.getInstance().downloadFile(message.getMsgId(), path, url, new QDFileDownLoadCallBack() {
            @Override
            public void onDownLoading(int per) {

            }

            @Override
            public void onDownLoadSuccess() {
                message.setFilePath(path);
                QDMessageHelper.updateMessage(message);

                File bitmapFile = new File(path);
                displayImage(context, imageView, bitmapFile);
            }

            @Override
            public void onDownLoadFailed(String msg) {
                imageView.setImageResource(R.mipmap.ic_download_failed);
            }
        });
    }

    public static View createImageView(final Context context, final QDMessage message, final String chatId) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        QDImageView imageView = new QDImageView(context);
        imageView.setAdjustViewBounds(true);
        imageView.setLayoutParams(params);
        imageView.setRadius(10);
        imageView.setBorderAlpha(0);
        imageView.setImageResource(R.mipmap.ic_download_loading);

        String path = message.getFilePath();
        String content = message.getContent();
        IMBTFFile file = IMBTFFile.fromBTFXml(content);
        if (TextUtils.isEmpty(path)) {
            if (file == null) {
                imageView.setImageResource(R.mipmap.ic_download_failed);
            } else {
                path = QDStorePath.IMG_PATH + file.getName();
                String fsHost = file.getFsHost();
                if (TextUtils.isEmpty(fsHost)) {
                    fsHost = QDUtil.getWebFileServer();
                }
                String url = fsHost + file.getOriginal();
                downloadImage(context, message, path, url, imageView);
            }
        } else {
            File imageFile = new File(path);
            if (imageFile.exists()) {
                displayImage(context, imageView, imageFile);
            } else {
                if (file == null) {
                    imageView.setImageResource(R.mipmap.ic_download_failed);
                } else {
                    String fsHost = file.getFsHost();
                    if (TextUtils.isEmpty(fsHost)) {
                        fsHost = QDUtil.getWebFileServer();
                    }
                    String url = fsHost + file.getOriginal();
                    downloadImage(context, message, path, url, imageView);
                }
            }
        }


//        View view = LayoutInflater.from(context).inflate(R.layout.chat_image_view, null);
//        final ImageView rightImg = view.findViewById(R.id.iv_chat_img_right);
//        final ImageView leftImg = view.findViewById(R.id.iv_chat_img_left);
//
//        final int direction = message.getDirection();
//        if (direction == QDMessage.DIRECTION_IN) {
//            rightImg.setVisibility(View.GONE);
//            leftImg.setVisibility(View.VISIBLE);
//            leftImg.setImageResource(R.mipmap.ic_download_loading);
//        } else {
//            rightImg.setVisibility(View.VISIBLE);
//            leftImg.setVisibility(View.GONE);
//            rightImg.setImageResource(R.mipmap.ic_download_loading);
//        }
//        String filePath = message.getFilePath();
//        if (TextUtils.isEmpty(filePath)) {
//            String content = message.getContent();
//            final IMBTFFile file = IMBTFFile.fromBTFXml(content);
//            if (file == null) {
//                if (direction == QDMessage.DIRECTION_OUT) {
//                    rightImg.setImageResource(R.mipmap.ic_download_failed);
//                } else {
//                    leftImg.setImageResource(R.mipmap.ic_download_failed);
//                }
//                return view;
//            }
//            final String path = QDStorePath.IMG_PATH + file.getName();
//            String fsHost = file.getFsHost();
//            if (TextUtils.isEmpty(fsHost)) {
//                fsHost = QDUtil.getWebFileServer();
//            }
//            String url = fsHost + file.getOriginal();
//            QDFileManager.downloadFile(message.getMsgId(), path, url, new QDFileDownLoadCallBack() {
//                @Override
//                public void onDownLoading(int per) {
//
//                }
//
//                @Override
//                public void onDownLoadSuccess() {
//                    message.setFilePath(path);
//                    QDMessageHelper.updateMessage(message);
//
//                    File bitmapFile = new File(path);
//                    if (direction == QDMessage.DIRECTION_OUT) {
//                        displayImage(context, rightImg, bitmapFile);
//                    } else {
//                        displayImage(context, leftImg, bitmapFile);
//                    }
//                }
//
//                @Override
//                public void onDownLoadFailed(String msg) {
//                    if (direction == QDMessage.DIRECTION_OUT) {
//                        rightImg.setImageResource(R.mipmap.ic_download_failed);
//                    } else {
//                        leftImg.setImageResource(R.mipmap.ic_download_failed);
//                    }
//                }
//            });
//        } else {
//            File bitmapFile = new File(filePath);
//            if (!bitmapFile.exists()) {
//                String content = message.getContent();
//                final IMBTFFile file = IMBTFFile.fromBTFXml(content);
//                if (file == null) {
//                    return view;
//                }
//                final String path = QDStorePath.IMG_PATH + file.getName();
//                String fsHost = file.getFsHost();
//                if (TextUtils.isEmpty(fsHost)) {
//                    fsHost = QDUtil.getWebFileServer();
//                }
//                String url = fsHost + file.getOriginal();
//                QDFileManager.downloadFile(message.getMsgId(), path, url, new QDFileDownLoadCallBack() {
//                    @Override
//                    public void onDownLoading(int per) {
//
//                    }
//
//                    @Override
//                    public void onDownLoadSuccess() {
//                        message.setFilePath(path);
//                        QDMessageHelper.updateMessage(message);
//
//                        File bitmapFile = new File(path);
//                        if (direction == QDMessage.DIRECTION_OUT) {
//                            displayImage(context, rightImg, bitmapFile);
//                        } else {
//                            displayImage(context, leftImg, bitmapFile);
//                        }
//                    }
//
//                    @Override
//                    public void onDownLoadFailed(String msg) {
//                        if (direction == QDMessage.DIRECTION_OUT) {
//                            rightImg.setImageResource(R.mipmap.ic_download_failed);
//                        } else {
//                            leftImg.setImageResource(R.mipmap.ic_download_failed);
//                        }
//                    }
//                });
//            } else {
//                if (direction == QDMessage.DIRECTION_IN) {
//                    displayImage(context, leftImg, bitmapFile);
//                } else {
//                    displayImage(context, rightImg, bitmapFile);
//                }
//            }
//        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<QDMessage> msgList;
                if (TextUtils.isEmpty(message.getGroupId())) {
                    msgList = QDMessageHelper.getImageListWithUserId(chatId);
                } else {
                    msgList = QDMessageHelper.getImageListWithGroupId(chatId);
                }
                int size = msgList.size();
                int index = 0;
                List<String> pathList = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    QDMessage msg = msgList.get(i);
                    if (msg.getMsgId().equalsIgnoreCase(message.getMsgId())) {
                        index = i;
                    }
                    String filePath = msg.getFilePath();
                    if (!TextUtils.isEmpty(filePath)) {
                        pathList.add(msg.getFilePath());
                    } else {
                        pathList.add("");
                    }
                }
                Intent intent = new Intent(context, QDPicActivity.class);
                intent.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_PHOTOS, (ArrayList<String>) pathList);
                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_INDEX, index);
                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_HAVE_LONG_CLICK, true);
                context.startActivity(intent);
            }
        });
        showPopupWindow(context, imageView, message);
        return imageView;
    }


    private static List<String> getUrlList(List<QDMessage> msgList) {
        List<String> urlList = new ArrayList<>();
        for (QDMessage message : msgList) {
            String filePath = message.getFilePath();
            if (!TextUtils.isEmpty(filePath)) {
                urlList.add(message.getFilePath());
            } else {
                urlList.add("");
            }
        }
        return urlList;
    }

    public static View createFileView(final Context context, final QDMessage message) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_file_view, null);
        ImageView ivIcon = view.findViewById(R.id.iv_chat_img);
        TextView tvTitle = view.findViewById(R.id.tv_chat_title);
        TextView tvDesc = view.findViewById(R.id.tv_chat_desc);
        final TextView tvInfo = view.findViewById(R.id.tv_chat_info);
        final ProgressBar progress = view.findViewById(R.id.pb_chat_progress);

        progress.setMax(100);
        int direction = message.getDirection();
        if (direction == QDMessage.DIRECTION_IN) {
            view.setBackgroundResource(R.mipmap.im_chat_msg_item_content_left_normal);
            tvTitle.setTextColor(Color.BLACK);
            tvDesc.setTextColor(context.getResources().getColor(R.color.colorTextUnFocused));
            tvInfo.setTextColor(context.getResources().getColor(R.color.colorTextUnFocused));
        } else {
            view.setBackgroundResource(R.mipmap.im_chat_msg_item_content_right_normal);
            tvTitle.setTextColor(Color.WHITE);
            tvDesc.setTextColor(context.getResources().getColor(R.color.colorActivityBackground));
            tvInfo.setTextColor(context.getResources().getColor(R.color.colorActivityBackground));
        }


        if (message.getFileStatus() == QDMessage.MSG_FILE_STATUS_UPLOADING) {
            progress.setVisibility(View.VISIBLE);
        } else {
            progress.setVisibility(View.GONE);
        }

        QDFileCallBack callBack = new QDFileCallBack<QDFileModel>() {
            @Override
            public void onUploading(String path, int per) {
                progress.setProgress(per);
            }

            @Override
            public void onUploadFailed(String errorMsg) {
                progress.setVisibility(View.GONE);
                tvInfo.setText(R.string.file_load_failed);
            }

            @Override
            public void onUploadSuccess(QDFileModel model) {
                progress.setVisibility(View.GONE);
                tvInfo.setText(R.string.file_load_success);
            }
        };

        String filePath = message.getFilePath();
        String url = "";
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            int resId = QDUtil.getFileIconByName(context, file.getName());
            ivIcon.setImageResource(resId);
            tvTitle.setText(file.getName());
            tvDesc.setText(QDUtil.changFileSizeToString(file.length()));
            QDFileManager.getInstance().addUploadCallBack(message.getMsgId(), callBack);
        } else {
            final String content = message.getContent();
            IMBTFFile imbtfFile = IMBTFFile.fromBTFXml(content);
            if (imbtfFile == null) {
                return view;
            }
            String fsHost = imbtfFile.getFsHost();
            if (TextUtils.isEmpty(fsHost)) {
                fsHost = QDUtil.getWebFileServer();
            }
            String original = imbtfFile.getOriginal();
            url = fsHost + original;

            int resId = QDUtil.getFileIconByName(context, imbtfFile.getName());
            ivIcon.setImageResource(resId);
            tvTitle.setText(imbtfFile.getName());
            tvDesc.setText(QDUtil.changFileSizeToString(imbtfFile.getSize()));

            filePath = QDStorePath.MSG_FILE_PATH + imbtfFile.getName();
        }
        final String finalFilePath = filePath;
        final String finalUrl = url;

        final QDFileDownLoadCallBack downLoadCallBack = new QDFileDownLoadCallBack() {
            @Override
            public void onDownLoading(int per) {
                progress.setVisibility(View.VISIBLE);
                progress.setProgress(per);
            }

            @Override
            public void onDownLoadSuccess() {
                progress.setVisibility(View.GONE);
                tvInfo.setText(R.string.file_downloaded);
                message.setFileStatus(QDMessage.MSG_FILE_STATUS_DOWNLOADED);
                message.setFilePath(finalFilePath);
                QDMessageHelper.updateMessage(message);
            }

            @Override
            public void onDownLoadFailed(String msg) {
                progress.setVisibility(View.GONE);
                tvInfo.setText(R.string.file_download_failed);
                message.setFileStatus(QDMessage.MSG_FILE_STATUS_DOWNLOAD_FAILED);
                QDMessageHelper.updateMessage(message);
            }
        };

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(message.getFilePath())) {
                    if (QDUtil.isHaveSelfAce(QDLanderInfo.getInstance().getMsgAce(), QDRolAce.ACE_MA_BAN_ADD_CUSTOMER)) {
                        QDUtil.showToast(context, context.getResources().getString(R.string.ace_self_ban_download_file));
                        return;
                    }
                    if (progress.getVisibility() == View.GONE) {
                        QDFileManager.getInstance().downloadFile(message.getMsgId(), finalFilePath, finalUrl, downLoadCallBack);
                    }
                } else {
                    QDUtil.openFile(context, message.getFilePath());
                }
            }
        });


        switch (message.getFileStatus()) {
            case QDMessage.MSG_FILE_STATUS_UPLOADING:
                tvInfo.setText(context.getResources().getString(R.string.file_loading));
                break;
            case QDMessage.MSG_FILE_STATUS_UPLOAD_FAILED:
                tvInfo.setText(context.getResources().getString(R.string.file_load_failed));
                break;
            case QDMessage.MSG_FILE_STATUS_UPLOADED:
                tvInfo.setText(context.getResources().getString(R.string.file_load_success));
                break;
            case QDMessage.MSG_FILE_STATUS_UNDOWNLOAD:
                tvInfo.setText(context.getResources().getString(R.string.file_undownload));
                break;
            case QDMessage.MSG_FILE_STATUS_DOWNLOADED:
                tvInfo.setText(context.getResources().getString(R.string.file_downloaded));
                break;
            case QDMessage.MSG_FILE_STATUS_DOWNLOAD_FAILED:
                tvInfo.setText(context.getResources().getString(R.string.file_download_failed));
                break;
        }

        showPopupWindow(context, view, message);

        return view;
    }

    public static View createVoiceView(Context context, final QDMessage message) {
        final int direction = message.getDirection();
        final View view;
        if (direction == QDMessage.DIRECTION_OUT) {
            view = LayoutInflater.from(context).inflate(R.layout.chat_voice_right, null);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.chat_voice_left, null);
        }
        final ImageView imageView = view.findViewById(R.id.iv_chat_voice);
        final TextView textView = view.findViewById(R.id.tv_chat_length);
        textView.setText(QDUtil.getRecordFileDuration(message.getFilePath()) + "\"");
        if (direction == QDMessage.DIRECTION_OUT) {
            imageView.setImageResource(R.mipmap.im_chat_to_voice_playing_f3_right);
        } else {
            imageView.setImageResource(R.mipmap.im_chat_to_voice_playing_f3_left);
        }
        String filePath = message.getFilePath();
        if (TextUtils.isEmpty(filePath)) {
            String content = message.getContent();
            IMBTFFile file = IMBTFFile.fromBTFXml(content);
            if (file == null) {
                return view;
            }
            final String path = QDStorePath.MSG_VOICE_PATH + file.getName();
            String fsHost = file.getFsHost();
            if (TextUtils.isEmpty(fsHost)) {
                fsHost = QDUtil.getWebFileServer();
            }
            String url = fsHost + file.getOriginal();
            QDFileManager.getInstance().downloadFile(message.getMsgId(), path, url, new QDFileDownLoadCallBack() {
                @Override
                public void onDownLoading(int per) {

                }

                @Override
                public void onDownLoadSuccess() {
                    message.setFilePath(path);
                    QDMessageHelper.updateMessage(message);
                    textView.setText(QDUtil.getRecordFileDuration(message.getFilePath()) + "\"");
                }

                @Override
                public void onDownLoadFailed(String msg) {

                }
            });

        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setEnabled(false);
                if (direction == QDMessage.DIRECTION_OUT) {
                    imageView.setImageResource(R.drawable.im_chat_record_play_anim_right);
                } else {
                    imageView.setImageResource(R.drawable.im_chat_record_play_anim_left);
                }
                final AnimationDrawable drawable = (AnimationDrawable) imageView.getDrawable();

                drawable.start();
                QDRecorderUtil.play(message.getFilePath(), new QDRecorderUtil.OnCompleteListener() {
                    @Override
                    public void onComplete() {
                        view.setEnabled(true);
                        drawable.stop();
                        if (direction == QDMessage.DIRECTION_OUT) {
                            imageView.setImageResource(R.mipmap.im_chat_to_voice_playing_f3_right);
                        } else {
                            imageView.setImageResource(R.mipmap.im_chat_to_voice_playing_f3_left);
                        }
                    }
                });
            }
        });

        showPopupWindow(context, view, message);

        return view;
    }

    public static View createShootView(final Context context, final QDMessage message) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_shoot_layout, null);
        final ImageView imageRight = view.findViewById(R.id.iv_chat_img_right);
        final ImageView imageLeft = view.findViewById(R.id.iv_chat_img_left);
        final ImageView imageStart = view.findViewById(R.id.iv_chat_start);

        final int direction = message.getDirection();
        if (direction == QDMessage.DIRECTION_OUT) {
            imageRight.setVisibility(View.VISIBLE);
            imageLeft.setVisibility(View.GONE);
            imageRight.setImageResource(R.mipmap.ic_download_loading);
        } else {
            imageRight.setVisibility(View.GONE);
            imageLeft.setVisibility(View.VISIBLE);
            imageLeft.setImageResource(R.mipmap.ic_download_loading);
        }
        imageStart.setVisibility(View.GONE);

        String filePath = message.getFilePath();
        if (TextUtils.isEmpty(filePath)) {
            String content = message.getContent();
            IMBTFFile file = IMBTFFile.fromBTFXml(content);
            if (file == null) {
                return view;
            }
            final String path = QDStorePath.MSG_VIDEO_PATH + file.getName();
            String fsHost = file.getFsHost();
            if (TextUtils.isEmpty(fsHost)) {
                fsHost = QDUtil.getWebFileServer();
            }
            String url = fsHost + file.getOriginal();

            QDFileManager.getInstance().downloadFile(message.getMsgId(), path, url, new QDFileDownLoadCallBack() {
                @Override
                public void onDownLoading(int per) {

                }

                @Override
                public void onDownLoadSuccess() {
                    imageStart.setVisibility(View.VISIBLE);
                    message.setFilePath(path);
                    QDMessageHelper.updateMessage(message);
                    if (direction == QDMessage.DIRECTION_OUT) {
                        displayImage(context, imageRight, QDUtil.getVideoFirstPic(message.getFilePath()));
                    } else {
                        displayImage(context, imageLeft, QDUtil.getVideoFirstPic(message.getFilePath()));
                    }

                }

                @Override
                public void onDownLoadFailed(String msg) {
                    imageRight.setImageResource(R.mipmap.ic_download_failed);
                    imageLeft.setImageResource(R.mipmap.ic_download_failed);
                }
            });

        } else {
            imageStart.setVisibility(View.VISIBLE);
            if (direction == QDMessage.DIRECTION_OUT) {
                displayImage(context, imageRight, QDUtil.getVideoFirstPic(message.getFilePath()));
            } else {
                displayImage(context, imageLeft, QDUtil.getVideoFirstPic(message.getFilePath()));
            }
        }

        imageStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(message.getFilePath())) {
                    Intent intent = new Intent(context, QDPlayerVideoActivity.class);
                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_SHOOT_PATH, message.getFilePath());
                    context.startActivity(intent);
                }
            }
        });

        showPopupWindow(context, view, message);

        return view;
    }

    public static View createLocationView(final Context context, final QDMessage message) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_location_layout, null);
        ImageView imageView = view.findViewById(R.id.iv_chat_loc_icon);
        TextView title = view.findViewById(R.id.iv_chat_loc_title);
        TextView desc = view.findViewById(R.id.tv_chat_location);

        int direction = message.getDirection();
        if (direction == QDMessage.DIRECTION_OUT) {
            view.setBackgroundResource(R.mipmap.im_chat_msg_item_content_right_normal);
            title.setTextColor(Color.WHITE);
            desc.setTextColor(context.getResources().getColor(R.color.colorActivityBackground));
        } else {
            view.setBackgroundResource(R.mipmap.im_chat_msg_item_content_left_normal);
            title.setTextColor(Color.BLACK);
            desc.setTextColor(context.getResources().getColor(R.color.colorTextUnFocused));
        }
        String content = message.getContent();
        IMBTFLoc loc = IMBTFLoc.fromBTFXml(content);
        String locInfo = loc.getContent();
        final String[] infos = locInfo.split(";");
        final QDLocationBean locationBean = new QDLocationBean();
        locationBean.setLatitude(QDStringUtil.strToDouble(infos[0]));
        locationBean.setLongitude(QDStringUtil.strToDouble(infos[1]));
        locationBean.setSampleLocationInfo(infos[2]);
        locationBean.setDetailLocationInfo(infos[3]);
        title.setText(locationBean.getSampleLocationInfo());
        desc.setText(locationBean.getDetailLocationInfo());
        imageView.setImageResource(R.mipmap.ic_location);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, QDGaodeLocationInfoActivity.class);
                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_LOCATION, locationBean);
                context.startActivity(intent);
            }
        });

        showPopupWindow(context, view, message);
        return view;
    }

    public static View createSignView(final Context context, final QDMessage message) {
        View view = LayoutInflater.from(context).inflate(R.layout.im_item_sign, null);
        TextView tvContent = view.findViewById(R.id.tv_sign_content);
        final TextView tvState = view.findViewById(R.id.tv_sign_state);
        ImageView ivLine = view.findViewById(R.id.iv_sign_line);
        int textSize = QDSDKConfig.getInstance().getMsgTextSize();
        tvContent.setTextSize(textSize);
        tvState.setTextSize(textSize);

        setTextContent(context, tvContent, message);
        int direction = message.getDirection();

        if (direction == QDMessage.DIRECTION_IN) {
            view.setBackgroundResource(R.mipmap.im_chat_msg_item_content_left_normal);
            tvContent.setTextColor(Color.BLACK);
            ivLine.setBackgroundColor(context.getResources().getColor(R.color.colorLine));
            tvState.setText(R.string.confirm_receipt);
            tvState.setTextColor(context.getResources().getColor(R.color.colorPopupItemEnable));
            tvState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvState.setText(R.string.already_signed);
                    tvState.setTextColor(context.getResources().getColor(R.color.colorTextUnFocused));
                    signForMessage(message);
                }
            });
        } else {
            view.setBackgroundResource(R.mipmap.im_chat_msg_item_content_right_normal);
            ivLine.setBackgroundColor(Color.parseColor("#3cc8fa"));
            tvContent.setTextColor(Color.WHITE);
            tvState.setText(R.string.no_signed);
            tvState.setTextColor(Color.parseColor("#f54119"));
        }
        showPopupWindow(context, view, message);
        return view;
    }

    public static View createConfirmedView(final Context context, final QDMessage message) {
        View view = LayoutInflater.from(context).inflate(R.layout.im_item_sign, null);
        TextView tvContent = view.findViewById(R.id.tv_sign_content);
        final TextView tvState = view.findViewById(R.id.tv_sign_state);
        ImageView ivLine = view.findViewById(R.id.iv_sign_line);
        int textSize = QDSDKConfig.getInstance().getMsgTextSize();
        tvContent.setTextSize(textSize);
        tvState.setTextSize(textSize);

        setTextContent(context, tvContent, message);
        int direction = message.getDirection();

        if (direction == QDMessage.DIRECTION_IN) {
            view.setBackgroundResource(R.mipmap.im_chat_msg_item_content_left_normal);
            tvContent.setTextColor(Color.BLACK);
            ivLine.setBackgroundColor(context.getResources().getColor(R.color.colorLine));
            tvState.setText(R.string.already_signed);
            tvState.setTextColor(context.getResources().getColor(R.color.colorTextUnFocused));
            return view;
        } else {
            view.setBackgroundResource(R.mipmap.im_chat_msg_item_content_right_normal);
            ivLine.setBackgroundColor(Color.parseColor("#3cc8fa"));
            tvContent.setTextColor(Color.WHITE);
            tvState.setText(R.string.already_signed);
            tvState.setTextColor(Color.parseColor("#b4faff"));
            return view;
        }
    }

    public static View createCmdView(final Context context, final QDMessage message) {
        TextView textView = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int textSize = QDSDKConfig.getInstance().getMsgTextSize();
        textView.setTextSize(textSize);
        textView.setMaxWidth(QDUtil.dp2px(context, 250));
        int direction = message.getDirection();
        if (direction == QDMessage.DIRECTION_IN) {
            textView.setTextColor(Color.BLACK);
            textView.setBackgroundResource(R.mipmap.im_chat_msg_item_content_left_normal);
        } else {
            textView.setTextColor(Color.WHITE);
            textView.setBackgroundResource(R.mipmap.im_chat_msg_item_content_right_normal);
        }
        String extType = message.getExtType();
        if (extType.equalsIgnoreCase("::Shake")) {
            textView.setText(message.getSenderName() + context.getResources().getString(R.string.shark_message));
        }
        textView.setLayoutParams(params);
        return textView;
    }

    public static View createVideoView(final Context context, final QDMessage message) {
        TextView textView = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int textSize = QDSDKConfig.getInstance().getMsgTextSize();
        textView.setTextSize(textSize);
        textView.setMaxWidth(QDUtil.dp2px(context, 250));
        int direction = message.getDirection();
        String msgType = message.getMsgType();
        if (direction == QDMessage.DIRECTION_IN) {
            textView.setTextColor(Color.BLACK);
            Drawable drawable;
            if (msgType.equalsIgnoreCase(QDMessage.MSG_TYPE_AUDIO)) {
                drawable = context.getResources().getDrawable(R.drawable.ic_audio);
            } else {
                drawable = context.getResources().getDrawable(R.drawable.ic_video_left);
            }
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            textView.setCompoundDrawables(drawable, null, null, null);
            textView.setBackgroundResource(R.mipmap.im_chat_msg_item_content_left_normal);
        } else {
            textView.setTextColor(Color.WHITE);
            Drawable drawable;
            if (msgType.equalsIgnoreCase(QDMessage.MSG_TYPE_AUDIO)) {
                drawable = context.getResources().getDrawable(R.drawable.ic_audio);
            } else {
                drawable = context.getResources().getDrawable(R.drawable.ic_video_right);
            }
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            textView.setCompoundDrawables(null, null, drawable, null);
            textView.setBackgroundResource(R.mipmap.im_chat_msg_item_content_right_normal);
        }
        textView.setCompoundDrawablePadding(QDUtil.dp2px(context, 5));
        textView.setText(message.getContent());
        textView.setLayoutParams(params);
        return textView;
    }

    private static void setTextContent(Context context, TextView textView, QDMessage message) {
        String content = message.getContent();
        IMBTFText text = IMBTFText.fromBTFXml(content);
        if (text != null) {
            textView.setText(QDChatSmiley.getInstance(context).strToSmiley(text.getContent()));
        }
    }

    public static View createLinkView(final Context context, final QDMessage message) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_location_layout, null);
        RoundedImageView imageView = view.findViewById(R.id.iv_chat_loc_icon);
        TextView title = view.findViewById(R.id.iv_chat_loc_title);
        TextView desc = view.findViewById(R.id.tv_chat_location);

        imageView.setRadius(10);
        int direction = message.getDirection();
        if (direction == QDMessage.DIRECTION_OUT) {
            view.setBackgroundResource(R.mipmap.im_chat_msg_item_content_right_normal);
            title.setTextColor(Color.WHITE);
            desc.setTextColor(context.getResources().getColor(R.color.colorActivityBackground));
        } else {
            view.setBackgroundResource(R.mipmap.im_chat_msg_item_content_left_normal);
            title.setTextColor(Color.BLACK);
            desc.setTextColor(context.getResources().getColor(R.color.colorTextUnFocused));
        }
        try {
            final JsonObject jsonObject = QDGson.getGson().fromJson(message.getContent(), JsonObject.class);
            title.setText(jsonObject.get("title").getAsString());
            desc.setText(jsonObject.get("desc").getAsString().replace("\n", ""));
            ImageLoader.getInstance().displayImage(jsonObject.get("icon").getAsString(), imageView, QDApplication.options);

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        showPopupWindow(context, view, message);
        return view;
    }

    public static View createCardView(final Context context, final QDMessage message) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_card_layout, null);
        ImageView imageView = view.findViewById(R.id.iv_chat_card_icon);
        TextView title = view.findViewById(R.id.iv_chat_card_title);
        TextView desc = view.findViewById(R.id.tv_chat_card_desc);
        TextView info = view.findViewById(R.id.tv_chat_card_info);
//        int direction = message.getDirection();
//        if (direction == QDMessage.DIRECTION_OUT) {
////            view.setBackgroundResource(R.mipmap.im_chat_msg_item_content_right_normal);
//            title.setTextColor(Color.WHITE);
//            desc.setTextColor(context.getResources().getColor(R.color.colorActivityBackground));
//            info.setTextColor(context.getResources().getColor(R.color.colorActivityBackground));
//        } else {
////            view.setBackgroundResource(R.mipmap.im_chat_msg_item_content_left_normal);
//            title.setTextColor(Color.BLACK);
//            desc.setTextColor(context.getResources().getColor(R.color.colorTextUnFocused));
//            info.setTextColor(context.getResources().getColor(R.color.colorTextUnFocused));
//        }
        title.setTextColor(Color.BLACK);
        desc.setTextColor(context.getResources().getColor(R.color.colorTextUnFocused));
        info.setTextColor(context.getResources().getColor(R.color.colorTextUnFocused));
        QDPersonCard card = QDGson.getGson().fromJson(message.getContent(), QDPersonCard.class);
        String icon = card.getIcon();
        if (TextUtils.isEmpty(icon)) {
            imageView.setImageBitmap(QDBitmapUtil.getInstance().createNameAvatar(context, card.getUserId(), card.getTitle()));
        } else {
            if (!icon.startsWith("http://") && !icon.startsWith("https://")) {
                icon = QDUtil.getWebFileServer() + icon;
            }
            ImageLoader.getInstance().displayImage(QDUtil.replaceWebServerAndToken(icon), imageView, QDApplication.options);
        }
        title.setText(card.getTitle());
        desc.setText(card.getDesc());
        info.setText(card.getName());
        final String code = card.getCode();
        final String id = card.getUserId();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (code.equalsIgnoreCase("personcard")) {
                    Intent intent = new Intent(context, QDUserDetailActivity.class);
                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_USER_ID, id);
                    context.startActivity(intent);
                }
            }
        });
        showPopupWindow(context, view, message);
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

    private static void displayImage(Context context, ImageView view, File file) {
        try {
            if (file.getName().toLowerCase().endsWith("gif")) {
                Glide.with(context).asGif().load(file).apply(new RequestOptions().placeholder(R.mipmap.ic_download_loading).centerInside()).into(view);
            } else {
                Glide.with(context).load(file).apply(new RequestOptions().placeholder(R.mipmap.ic_download_loading)).into(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void displayImage(Context context, ImageView view, Bitmap bitmap) {
        try {
            Glide.with(context).load(bitmap).apply(new RequestOptions().placeholder(R.mipmap.ic_download_loading).override(Target.SIZE_ORIGINAL).dontAnimate().centerInside()).into(view);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void showPopupWindow(final Context context, View view, final QDMessage message) {
        final List<String> popupMenuItemList = new ArrayList<>();
        String msgType = message.getMsgType();
        final String strCopy = context.getResources().getString(R.string.str_copy);
        // final String strForward = context.getResources().getString(R.string.cloud_forward);
        final String strDel = context.getResources().getString(R.string.conversation_del);
//        final String strCollect = context.getResources().getString(R.string.str_collect);
        final String strRevoke = context.getResources().getString(R.string.str_revoke);
//        final String strMultiSel = context.getResources().getString(R.string.str_multi_sel);
//        final String strDropMsg = context.getResources().getString(R.string.str_drop_msg);
        boolean isOpenGroupDropMsg = QDUtil.isHaveClientAce(QDRolAce.ACE_CLIENT_OPEN_GROUP_DROP_MESSAGE);

        if (!msgType.equalsIgnoreCase(QDMessage.MSG_TYPE_CONFIRM) && !msgType.equalsIgnoreCase(QDMessage.MSG_TYPE_CARD)) {
            if (msgType.equalsIgnoreCase(QDMessage.MSG_TYPE_TEXT)) {
                popupMenuItemList.add(strCopy);
            }
            // popupMenuItemList.add(strForward);
            popupMenuItemList.add(strDel);
            if (!TextUtils.isEmpty(message.getGroupId()) && isOpenGroupDropMsg) {
                QDGroup group = QDGroupHelper.getGroupById(message.getGroupId());
                if (group.getOwnerId().equalsIgnoreCase(QDLanderInfo.getInstance().getId())) {
                    //popupMenuItemList.add(strDropMsg);
                }
            }
            //popupMenuItemList.add(strCollect);
            if (message.getSenderId().equalsIgnoreCase(QDLanderInfo.getInstance().getId()) && !message.getSenderId().equalsIgnoreCase(message.getReceiverId())) {
                popupMenuItemList.add(strRevoke);
            }
            // popupMenuItemList.add(strMultiSel);
        } else {
            if (message.getDirection() == QDMessage.DIRECTION_OUT) {
                popupMenuItemList.add(strRevoke);
            }
            if (!TextUtils.isEmpty(message.getGroupId()) && isOpenGroupDropMsg) {
                QDGroup group = QDGroupHelper.getGroupById(message.getGroupId());
                if (group.getOwnerId().equalsIgnoreCase(QDLanderInfo.getInstance().getId())) {
                    // popupMenuItemList.add(strDropMsg);
                }
            }
        }

        final QDCopyPopupList popupList = new QDCopyPopupList();
        popupList.init(context, view, popupMenuItemList, new QDCopyPopupList.OnPopupListClickListener() {
            @Override
            public void onPopupListClick(View contextView, int contextPosition, int position) {
//                if (popupMenuItemList.get(position).equalsIgnoreCase(strForward)) {
//                    toSelectContactActivity(context, message);
//                } else
                if (popupMenuItemList.get(position).equalsIgnoreCase(strRevoke)) {
                    if (message.getDirection() == QDMessage.DIRECTION_OUT && !message.getSenderId().equalsIgnoreCase(message.getReceiverId())) {
                        long createDate = message.getCreateDate();
                        long offset = (System.currentTimeMillis() + QDClient.getInstance().getLoginInfo().getSTimeOffset()) - (createDate / 1000);
                        if (offset >= 2 * 60 * 1000) {
                            QDUtil.showToast(context, context.getResources().getString(R.string.revoke_timeout));
                            return;
                        } else {
                            revokeMessage(message);
                        }
                    }
                } else if (popupMenuItemList.get(position).equalsIgnoreCase(strCopy)) {
                    copyText(context, message);
                } else if (popupMenuItemList.get(position).equalsIgnoreCase(strDel)) {
                    QDMessageHelper.deleteMessageById(message.getMsgId());
                    EventBus.getDefault().post(message);
                }
            }
        });
        popupList.setIndicatorView(popupList.getDefaultIndicatorView(popupList.dp2px(16), popupList.dp2px(8), 0xFF444444));
    }

    private static void copyText(Context context, QDMessage message) {
        IMBTFManager manager = new IMBTFManager(message.getContent());
        StringBuilder sb = new StringBuilder();
        List<IMBTFItem> imbtfItemList = manager.getItemList();
        for (IMBTFItem item : imbtfItemList) {
            if (item instanceof IMBTFText) {
                sb.append(QDChatSmiley.getInstance(context).strToSmiley(((IMBTFText) item).getContent()));
            } else if (item instanceof IMBTFAT) {
                String info = ((IMBTFAT) item).getContent();
                String[] infos = info.split(";");
                String userId = infos[0];
                String userName = infos[1];
                SpannableString spannableString = new SpannableString("@" + userName + " ");
                QDAtClickSpan span = new QDAtClickSpan(context, context.getResources().getColor(R.color.colorBtnBlue), userId, userName);
                spannableString.setSpan(span, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                sb.append(spannableString);
            }
        }
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", sb.toString());
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
    }

    private static void toSelectContactActivity(Context context, QDMessage message) {
        Intent intent = new Intent(context, QDSelectContactActivity.class);
        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_CONTACT_MODE, QDContactActivity.MODE_SINGLE);
        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_FORWARD, true);
        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_MESSAGE, message);
        ((QDChatActivity) context).startActivityForResult(intent, QDChatActivity.REQUEST_FORWARD);
    }

    private static void collectMessage(final Context context, QDMessage message) {
        final QDCollectMessage collectMessage = new QDCollectMessage();
        String groupId = message.getGroupId();
        String source;
        if (TextUtils.isEmpty(groupId)) {
            source = message.getSenderName();
        } else {
            source = "群-" + message.getGroup().getName();
        }
        collectMessage.setContent(message.getContent());
        collectMessage.setContentType(message.getContentType());
        collectMessage.setMsgId(message.getMsgId());
        collectMessage.setSenderId(message.getSenderId());
        collectMessage.setSenderName(message.getSenderName());
        collectMessage.setSource(source);
        collectMessage.setMsgType(message.getMsgType());
        String attachment = message.getAttachment();
        if (!TextUtils.isEmpty(attachment)) {
            int index = attachment.lastIndexOf("/");
            String fileId = attachment.substring(index + 1);
            collectMessage.setFileIds(fileId);
        }
        QDClient.getInstance().collectMsg(collectMessage, new QDResultCallBack() {
            @Override
            public void onError(String errorMsg) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        QDUtil.showToast(context, context.getResources().getString(R.string.collect_failed));
                    }
                });
            }

            @Override
            public void onSuccess(Object o) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        QDUtil.showToast(context, context.getResources().getString(R.string.collect_success));
                    }
                });
            }
        });
    }

    private static void signForMessage(QDMessage message) {
        QDRevokeMsg revokeMsg = new QDRevokeMsg();
        QDMessage msg = QDMessageHelper.getMessageById(message.getMsgId());
        revokeMsg.setContentType(QDBaseCommand.CONTENT_TYPE_JSON);
        revokeMsg.setExtData(msg.getExtData());
        revokeMsg.setMsgextType(msg.getExtType());
        revokeMsg.setMsgFlag(msg.getMsgFlag());
        revokeMsg.setMsgId(com.longchat.base.util.QDUtil.newGuid());
        revokeMsg.setMsgType(QDMessage.MSG_TYPE_CONFIRMED);
        revokeMsg.setSubject(msg.getSubject());
        QDRevokeMsg.ContentBean contentBean = new QDRevokeMsg.ContentBean();
        contentBean.setMsgId(msg.getMsgId());
        contentBean.setSenderMsgNum(msg.getSenderMsgNumber());
        revokeMsg.setContent(contentBean);
        revokeMsg.setRecvId(msg.getSenderId());
        revokeMsg.setRecvName(msg.getSenderName());
        QDClient.getInstance().signForMessage(revokeMsg);
    }

    private static void revokeMessage(QDMessage message) {
        QDRevokeMsg revokeMsg = new QDRevokeMsg();
        QDMessage msg = QDMessageHelper.getMessageById(message.getMsgId());
        revokeMsg.setAttachments(msg.getAttachment());
        revokeMsg.setContentType(QDBaseCommand.CONTENT_TYPE_JSON);
        revokeMsg.setExtData(msg.getExtData());
        revokeMsg.setMsgextType(msg.getExtType());
        revokeMsg.setMsgFlag(msg.getMsgFlag());
        revokeMsg.setMsgId(com.longchat.base.util.QDUtil.newGuid());
        revokeMsg.setMsgType(QDMessage.MSG_TYPE_CANCEL);
        revokeMsg.setSubject(msg.getSubject());
        QDRevokeMsg.ContentBean contentBean = new QDRevokeMsg.ContentBean();
        contentBean.setMsgId(msg.getMsgId());
        contentBean.setMsgNum(msg.getMsgNumber());
        contentBean.setMsgType(msg.getMsgType());
        String attachment = msg.getAttachment();
        if (!TextUtils.isEmpty(attachment)) {
            String fid = attachment.substring(attachment.lastIndexOf("/") + 1);
            contentBean.setFguid(fid);
        }
        revokeMsg.setContent(contentBean);
        if (TextUtils.isEmpty(message.getGroupId())) {
            revokeMsg.setRecvId(msg.getReceiverId());
            revokeMsg.setRecvName(msg.getReceiverName());
            QDClient.getInstance().revokeMessage(revokeMsg);
        } else {
            revokeMsg.setGroupId(message.getGroupId());
            QDClient.getInstance().revokeGMessage(revokeMsg);
        }
    }

    private void displayImage(String msgId, ImageView imageView, String path) {
        if (cache == null) {
            long maxMemory = Runtime.getRuntime().maxMemory();
            int cacheSize = (int) (maxMemory / 8);
            cache = new LruCache<>(cacheSize);
        }
        if (cache.get(msgId) == null) {
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeFile(path);
            } catch (Exception e) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                bitmap = BitmapFactory.decodeFile(path, options);
            }
            cache.put(msgId, bitmap);
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageBitmap(cache.get(msgId));
        }
    }
}
