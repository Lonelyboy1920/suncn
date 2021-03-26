package com.share.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.share.R;
import com.umeng.socialize.bean.SHARE_MEDIA;


public class ShareAdapter extends BaseAdapter {

    private String[] titles = new String[]{"微信", "朋友圈", "微博", "QQ", "QQ空间", "钉钉"};

    private Context context;

    public ShareAdapter(Context context) {
        this.context = context;
    }

    public void setTitles(String[] titles) {
        this.titles = titles;
    }

    private int getDrawable(String title) {
        int drawable = 0;
        switch (title) {
            case "微信":
                drawable = R.drawable.umeng_socialize_wechat;
                break;
            case "朋友圈":
                drawable = R.drawable.umeng_socialize_wxcircle;
                break;
            case "微博":
                drawable = R.drawable.umeng_socialize_sina;
                break;
            case "QQ":
                drawable = R.drawable.umeng_socialize_qq;
                break;
            case "QQ空间":
                drawable = R.drawable.umeng_socialize_qzone;
                break;
            case "钉钉":
                drawable = R.drawable.umeng_socialize_ding;
                break;
        }
        return drawable;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        convertView = LayoutInflater.from(context).inflate(R.layout.share_menu_item_layout, null);
        LinearLayout llLayout = convertView.findViewById(R.id.socialize_linear);
        ImageView siView = convertView.findViewById(R.id.socialize_image_view);
        TextView stView = convertView.findViewById(R.id.socialize_text_view);
        stView.setText(titles[i]);
        siView.setImageResource(getDrawable(titles[i]));
        llLayout.setOnClickListener(v -> {
            if (mListener != null) {
                SHARE_MEDIA platform = SHARE_MEDIA.WEIXIN;
                switch (titles[i]) {
                    case "微信":
                        platform = SHARE_MEDIA.WEIXIN;
                        break;
                    case "朋友圈":
                        platform = SHARE_MEDIA.WEIXIN_CIRCLE;
                        break;
                    case "微博":
                        platform = SHARE_MEDIA.SINA;
                        break;
                    case "QQ":
                        platform = SHARE_MEDIA.QQ;
                        break;
                    case "QQ空间":
                        platform = SHARE_MEDIA.QZONE;
                        break;
                    case "钉钉":
                        platform = SHARE_MEDIA.DINGTALK;
                        break;
                }
                mListener.onItemClick(platform);
            }
        });
        return convertView;
    }

    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(SHARE_MEDIA platform);
    }

}
