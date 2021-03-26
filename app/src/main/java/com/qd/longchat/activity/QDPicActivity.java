package com.qd.longchat.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.ImageView;


import com.qd.longchat.R;
import com.qd.longchat.adapter.QDPicAdapter;
import com.qd.longchat.config.QDStorePath;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;
import com.qd.longchat.view.QDAlertView;

import java.io.File;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.qd.longchat.R2;
/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/6/28 下午3:25
 */
public class QDPicActivity extends QDBaseActivity {

    @BindView(R2.id.pic_pager)
    ViewPager viewPager;

    @BindString(R2.string.save_to_album)
    String strSaveAlbum;
    @BindString(R2.string.identify_qr_code)
    String strQrCode;
    @BindString(R2.string.save_success)
    String strSaveSuccess;
    @BindString(R2.string.save_failed)
    String strSaveFailed;

    private QDPicAdapter adapter;
    private boolean isRemote;
    private boolean isHaveLongClick;
    private List<String> pathList;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    private OnItemClickListener itemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            finish();
        }
    };

    private OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener() {
        @Override
        public void onItemLongClick(int position) {
            File file;
            if (isRemote) {
                String url = pathList.get(position);
                file = new File(QDStorePath.APP_PATH + url.substring(url.lastIndexOf("/") + 1));
            } else {
                file = new File(pathList.get(position));
            }
            ImageView imageView = adapter.getImageView(position);
            imageView.setDrawingCacheEnabled(true);
            Bitmap bitmap = imageView.getDrawingCache();
            showAlert(file, "");
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int flag= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        getWindow().setFlags(flag, flag);
        setContentView(R.layout.view_pic);
        ButterKnife.bind(this);

        pathList = getIntent().getStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_PHOTOS);
        int index = getIntent().getIntExtra(QDIntentKeyUtil.INTENT_KEY_INDEX, 0);
        isRemote = getIntent().getBooleanExtra(QDIntentKeyUtil.INTENT_KEY_IS_REMOTE, false);
        isHaveLongClick = getIntent().getBooleanExtra(QDIntentKeyUtil.INTENT_KEY_IS_HAVE_LONG_CLICK, false);
        adapter = new QDPicAdapter(context, pathList, isRemote, isHaveLongClick);
        viewPager.setAdapter(adapter);
        adapter.setItemClickListener(itemClickListener);
        if (isHaveLongClick) {
            adapter.setItemLongClickListener(itemLongClickListener);
        }
        viewPager.setCurrentItem(index);
    }

    private void showAlert(final File file, final String result) {
        String[] strs;
        if (TextUtils.isEmpty(result)) {
            strs = new String[]{strSaveAlbum};
        } else {
            strs = new String[]{strSaveAlbum, strQrCode};
        }

        QDAlertView alertView = new QDAlertView.Builder()
                .setContext(context)
                .setStyle(QDAlertView.Style.Bottom)
                .setSelectList(strs)
                .setDismissOutside(true)
                .setOnStringItemClickListener(new QDAlertView.OnStringItemClickListener() {
                    @Override
                    public void onItemClick(String str, int position) {
                        if (str.equalsIgnoreCase(strSaveAlbum)) {
                            if (QDUtil.savePicToGallery(context, file)) {
                                QDUtil.showToast(context, strSaveSuccess);
                            } else {
                                QDUtil.showToast(context, strSaveFailed);
                            }
                        } else {
                            Intent intent = new Intent();
                            try {
                                intent.setAction("android.intent.action.VIEW");
                                intent.setData(Uri.parse(result));
                                context.startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                                intent.setClass(context, QDWarningActivity.class);
                                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_MODIFY_INFO, result);
                                context.startActivity(intent);
                            }
                        }
                    }
                }).build();
        alertView.show();
    }
}
