package com.qd.longchat.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.longchat.base.callback.QDFileDownLoadCallBack;
import com.longchat.base.manager.QDFileManager;
import com.qd.longchat.R;
import com.qd.longchat.activity.QDFileDisplayActivity;
import com.qd.longchat.activity.QDPicActivity;
import com.qd.longchat.activity.QDPlayerVideoActivity;
import com.qd.longchat.config.QDStorePath;
import com.qd.longchat.model.QDFile;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.view.QDAlertView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/8/16 上午9:25
 */
public class QDSelfFilePagerAdapter extends PagerAdapter {

    private Context context;
    private String[] tabTitles;
    private Map<Integer, View> viewCache;
    private List<QDFile> fileList;
    private List<QDFile> docFileList;
    private List<QDFile> imgFileList;
    private List<QDFile> otherFileList;
    private List<QDFile> videoFileList;
    private QDAlertView alertView;

    public QDSelfFilePagerAdapter(Context context, String[] tabTitles, List<QDFile> fileList,
                                  List<QDFile> docFileList, List<QDFile> imgFileList, List<QDFile> videoFileList, List<QDFile> otherFileList) {
        this.context = context;
        this.tabTitles = tabTitles;
        this.fileList = fileList;
        this.docFileList = docFileList;
        this.imgFileList = imgFileList;
        this.videoFileList = videoFileList;
        this.otherFileList = otherFileList;
        this.viewCache = new HashMap<>();
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final ListView listView;
        if (viewCache.get(position) == null) {
            listView = new ListView(context);
            List<QDFile> mFileList;
            if (position == 0) {
                mFileList = fileList;
            } else if (position == 1){
                mFileList = docFileList;
            } else if (position == 2) {
                mFileList = imgFileList;
            } else if (position == 3) {
                mFileList = videoFileList;
            } else {
                mFileList = otherFileList;
            }
            QDSelfFileAdapter adapter = new QDSelfFileAdapter(context, mFileList);
            listView.setAdapter(adapter);
            listView.setDivider(null);
        } else {
            listView = (ListView) viewCache.get(position);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final QDFile f = (QDFile) listView.getAdapter().getItem(position);
                if (TextUtils.isEmpty(f.getPath())) {
                    getAlertView().show();
                    final String filePath = QDStorePath.MSG_FILE_PATH + f.getName();
                    QDFileManager.getInstance().downloadFile(filePath, f.getUrl(), new QDFileDownLoadCallBack() {
                        @Override
                        public void onDownLoading(int per) {

                        }

                        @Override
                        public void onDownLoadSuccess() {
                            getAlertView().dismiss();
                            f.setPath(filePath);
                            previewFile(f);
                        }

                        @Override
                        public void onDownLoadFailed(String msg) {
                            getAlertView().dismiss();
                        }
                    });
                } else {
                    previewFile(f);
                }
            }
        });
        container.addView(listView, params);
        return listView;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        this.viewCache.put(position, (View) object);
        container.removeView((View) object);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    private QDAlertView getAlertView() {
        if (alertView == null) {
            alertView = new QDAlertView.Builder()
                    .setContext(context)
                    .setWarning(context.getResources().getString(R.string.opening_file))
                    .setStyle(QDAlertView.Style.Warn)
                    .build();
        }
        return alertView;
    }

    private void previewFile(QDFile file) {
        String name = file.getName();
        if (!name.contains(".")) {
            Intent intent = new Intent(context, QDFileDisplayActivity.class);
            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_FILE_PATH, file.getPath());
            context.startActivity(intent);
        } else {
            String suffix = name.substring(name.lastIndexOf("."));
            if (suffix.equals(".png") || suffix.equals(".jpg") || suffix.equals(".jpeg") || suffix.equals(".bmp")) {
                List<String> pathList = new ArrayList<>();
                pathList.add(file.getPath());
                Intent intent = new Intent(context, QDPicActivity.class);
                intent.putStringArrayListExtra(QDIntentKeyUtil.INTENT_KEY_SELECTED_PHOTOS, (ArrayList<String>) pathList);
                context.startActivity(intent);
            } else if (suffix.equals(".mp4")) {
                Intent intent = new Intent(context, QDPlayerVideoActivity.class);
                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_SHOOT_PATH, file.getPath());
                context.startActivity(intent);
            } else {
                Intent intent = new Intent(context, QDFileDisplayActivity.class);
                intent.putExtra(QDIntentKeyUtil.INTENT_KEY_FILE_PATH, file.getPath());
                context.startActivity(intent);
            }
        }
    }

}
