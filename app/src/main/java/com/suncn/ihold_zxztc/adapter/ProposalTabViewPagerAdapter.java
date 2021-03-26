package com.suncn.ihold_zxztc.adapter;

import android.os.Parcelable;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.suncn.ihold_zxztc.bean.ProposalTabListBean;

import java.util.ArrayList;
import java.util.List;

public class ProposalTabViewPagerAdapter extends PagerAdapter {
    private ArrayList<View> listViews;
    List<ProposalTabListBean.ProposalTab> objList;

    public ProposalTabViewPagerAdapter(ArrayList<View> listViews) {
        super();
        this.listViews = listViews;
    }

    public ProposalTabViewPagerAdapter(ArrayList<View> listViews, List<ProposalTabListBean.ProposalTab> objList) {
        super();
        this.objList = objList;
        this.listViews = listViews;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (objList == null) {
            return "";
        }
        return objList.get(position).getStrName();
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
        container.removeView(listViews.get(position));
    }
}
