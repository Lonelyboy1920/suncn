package com.suncn.ihold_zxztc.adapter;

import android.os.Parcelable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.suncn.ihold_zxztc.bean.NewsColumnListBean;
import com.suncn.ihold_zxztc.view.forscrollview.MyViewPagerForScrollView;

import java.util.ArrayList;
import java.util.List;

public class MyViewPagerAdapter extends PagerAdapter {
    private ArrayList<View> listViews = new ArrayList<View>();
    List<NewsColumnListBean.NewsColumnBean> objList;
    private boolean isMyViewPagerForScrollView;

    public MyViewPagerAdapter(ArrayList<View> listViews) {
        super();
        this.listViews = listViews;
    }

    public MyViewPagerAdapter(ArrayList<View> listViews, List<NewsColumnListBean.NewsColumnBean> objList) {
        super();
        this.objList = objList;
        this.listViews = listViews;
    }

  /*  public MyViewPagerAdapter(ArrayList<View> listViews, boolean isMyViewPagerForScrollView) {
        super();
        this.listViews = listViews;
        this.isMyViewPagerForScrollView = isMyViewPagerForScrollView;
    }*/

    @Override
    public CharSequence getPageTitle(int position) {
        if (objList == null) {
            return "";
        }
        return objList.get(position).getStrName();
    }

    public ArrayList<View> getListViews() {
        return listViews;
    }

    public void setListViews(ArrayList<View> listViews) {
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
        if (isMyViewPagerForScrollView)
            ((MyViewPagerForScrollView) arg0).removeView(listViews.get(arg1));
        else
            ((ViewPager) arg0).removeView(listViews.get(arg1));
    }*/

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (isMyViewPagerForScrollView)
            ((MyViewPagerForScrollView) container).removeView(listViews.get(position));
        else
            ((ViewPager) container).removeView(listViews.get(position));
        //super.destroyItem(container, position, object);
    }
}
