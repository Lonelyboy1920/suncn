package com.qd.longchat.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.bumptech.glide.request.RequestOptions;
import com.longchat.base.dao.QDPlug;
import com.qd.longchat.R;
import com.qd.longchat.fragment.QDWorkFragment;
import com.qd.longchat.util.QDUtil;


import java.util.List;

/**
 * @author skynight(skynight@dingtalk.com)
 * @creatTime 2018/4/16 下午5:51
 */

public class QDWorkPicAdapter extends PagerAdapter {

    private Context context;
    private List<QDPlug> picList;
    private QDWorkFragment.OnViewPagerClickListener onViewPagerClickListener;

    public QDWorkPicAdapter(Context context, List<QDPlug> picList) {
        this.context = context;
        this.picList = picList;
    }

    @Override
    public int getCount() {
        if (picList.size() == 1) {
            return 1;
        }
        return Integer.MAX_VALUE;
    }

    public void setOnViewPagerClickListener(QDWorkFragment.OnViewPagerClickListener onViewPagerClickListener) {
        this.onViewPagerClickListener = onViewPagerClickListener;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.im_item_work_pic, null);
        TextView tvName = view.findViewById(R.id.tv_work_pic_title);
        ImageView ivImg = view.findViewById(R.id.iv_work_pic);
        int count = picList.size();
        final QDPlug plug = picList.get(position % count);
        String icon = QDUtil.getWebFileServer() + plug.getIcon();
        Glide.with(context).load(icon).apply(new RequestOptions().skipMemoryCache(true).placeholder(R.mipmap.ic_download_loading)
                .error(R.mipmap.ic_download_failed)).into(ivImg);
        tvName.setText(plug.getName());
        container.addView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onViewPagerClickListener.onClick(position, plug.getTargetUrl());
            }
        });
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
