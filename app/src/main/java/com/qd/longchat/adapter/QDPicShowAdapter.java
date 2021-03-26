package com.qd.longchat.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.qd.longchat.R;
import com.qd.longchat.activity.QDPicActivity;

import java.io.File;
import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/6/28 下午2:52
 */
public class QDPicShowAdapter extends PagerAdapter {

    private Context context;
    private List<String> pathList;
    private QDPicActivity.OnItemClickListener itemClickListener;

    public QDPicShowAdapter(Context context, List<String> pathList) {
        this.context = context;
        this.pathList = pathList;
    }

    @Override
    public int getCount() {
        return pathList.size();
    }

    public void setPathList(List<String> pathList) {
        this.pathList = pathList;
        notifyDataSetChanged();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pic_show, null);
        ImageView imageView = view.findViewById(R.id.iv_pic);
        final String path = pathList.get(position);
        Glide.with(context).load(new File(path)).apply(new RequestOptions().error(R.mipmap.ic_download_failed)).into(imageView);
        container.addView(view);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(position);
                }
            }
        });
        return view;
    }

    public void setItemClickListener(QDPicActivity.OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
