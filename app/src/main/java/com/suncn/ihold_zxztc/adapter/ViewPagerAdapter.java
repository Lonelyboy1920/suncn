package com.suncn.ihold_zxztc.adapter;

import android.os.Parcelable;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.View;
import android.view.ViewGroup;

import com.suncn.ihold_zxztc.bean.TabBean;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {
    private List<View> listViews;
    private ArrayList<TabBean> titlesList;
    private List<String> titles;

    public ViewPagerAdapter(List<View> listViews, ArrayList<TabBean> titles) {
        this.listViews = listViews;
        this.titlesList = titles;
    }

    public ViewPagerAdapter(List<View> listViews, List<String> titles) {
        this.listViews = listViews;
        this.titles = titles;
    }

    public List<View> getListViews() {
        return listViews;
    }

    public void setListViews(List<View> listViews) {
        this.listViews = listViews;
    }

    @Override
    public void startUpdate(ViewGroup container) {
        super.startUpdate(container);
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {

    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        if (titlesList != null && titlesList.size() > 0) {
            title = titlesList.get(position).getStrName();
        } else {
            title = titles.get(position);
        }
        if (title.contains("|")) {
            return title.split("\\|")[1];
        }
        return title;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public Object instantiateItem(ViewGroup arg0, int arg1) {
        // index = arg1;
        // 实例化位置为arg1位置的页面
        // ((ViewPager) arg0).addView(listViews.get(arg1));
        // return listViews.get(arg1);
        try {
            if (listViews.get(arg1).getParent() == null) {
                arg0.addView(listViews.get(arg1));
            } else {
                ((ViewGroup) listViews.get(arg1).getParent()).removeView(listViews.get(arg1));
                arg0.addView(listViews.get(arg1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listViews.get(arg1);
    }

    @Override
    public int getCount() {
        return listViews.size();
    }


    @Override
    public void finishUpdate(ViewGroup container) {
        super.finishUpdate(container);
    }

    /*  @Override
      public void destroyItem(View arg0, int arg1, Object arg2) {
          // 销毁一个页面之后会调用这个方法
          ((ViewPager) arg0).removeView(listViews.get(arg1));

      }*/
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView(listViews.get(position));
        //super.destroyItem(container, position, object);
    }
}
