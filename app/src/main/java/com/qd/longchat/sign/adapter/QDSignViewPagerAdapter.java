package com.qd.longchat.sign.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.longchat.base.model.gd.QDSignModel;

import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/10/24 下午3:48
 */
public class QDSignViewPagerAdapter extends PagerAdapter {

    private Context context;
    private String[] tabTitles;
    private List<QDSignModel.ListBean> mineList;
    private List<QDSignModel.ListBean> reportList;

    public QDSignViewPagerAdapter(Context context, String[] tabTitles) {
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

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ListView listView = new ListView(context);
        listView.setDivider(null);
        listView.setVerticalScrollBarEnabled(false);
        listView.setDividerHeight(30);
        final List<QDSignModel.ListBean> list;
        boolean isMine;
        if (position == 0) {
            if (mineList == null) {
                return null;
            }
            list = mineList;
            isMine = true;
        } else {
            if (reportList == null) {
                return null;
            }
            list = reportList;
            isMine = false;
        }
        QDSignAdapter adapter = new QDSignAdapter(context, list, isMine);
        listView.setAdapter(adapter);
        container.addView(listView, params);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        listView.setTag(position);
        return listView;
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

    public void setMineList(List<QDSignModel.ListBean> mineList) {
        this.mineList = mineList;
    }

    public void setReportList(List<QDSignModel.ListBean> reportList) {
        this.reportList = reportList;
    }
}
