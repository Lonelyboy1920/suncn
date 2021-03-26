package com.qd.longchat.cloud.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.longchat.base.dao.QDDownloadInfo;
import com.longchat.base.dao.QDUploadInfo;
import com.longchat.base.databases.QDDownloadInfoHelper;
import com.longchat.base.databases.QDUploadInfoHelper;
import com.qd.longchat.util.QDSortUtil;

import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/11/3 上午11:22
 */
public class QDCloudTranPagerAdapter extends PagerAdapter {

    private final static String URL = "http://116.62.129.71:9101\\uploads\\pan\\20181107\\d1c3e2ed499f14e1ea5fd105e5b4ac8c.exe";

    private Context context;
    private String[] tabTitles;

    private boolean isStart;

    public QDCloudTranPagerAdapter(Context context, String[] tabTitles) {
        this.context = context;
        this.tabTitles = tabTitles;
    }


    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ListView listView = new SwipeMenuListView(context);
        if (position == 0) {
            List<QDDownloadInfo> downloadInfoList = QDDownloadInfoHelper.loadAll();
            QDCloudTranFileAdapter adapter = new QDCloudTranFileAdapter(context, QDSortUtil.sortDownloadInfo(downloadInfoList));
            listView.setAdapter(adapter);
        } else {
            List<QDUploadInfo> uploadInfoList = QDUploadInfoHelper.loadAll();
            QDCloudTranUploadAdapter adapter = new QDCloudTranUploadAdapter(context, QDSortUtil.sortUploadInfo(uploadInfoList));
            listView.setAdapter(adapter);
        }
        container.addView(listView);
        return listView;
    }


}
