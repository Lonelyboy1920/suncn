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
import com.longchat.base.callback.QDFileDownLoadCallBack;
import com.longchat.base.manager.QDFileManager;
import com.qd.longchat.R;
import com.qd.longchat.activity.QDPicActivity;
import com.qd.longchat.config.QDStorePath;
import com.qd.longchat.util.QDUtil;


import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/11/21 下午3:16
 */
public class QDPicAdapter extends PagerAdapter {
    private Context context;
    private List<String> pathList;
    private QDPicActivity.OnItemClickListener itemClickListener;
    private QDPicActivity.OnItemLongClickListener itemLongClickListener;
    private boolean isRemote;
    private boolean isHaveLongClick;
    private Map<Integer, ImageView> viewMap;

    public QDPicAdapter(Context context, List<String> pathList, boolean isRemote, boolean isHaveLongClick) {
        this.context = context;
        this.pathList = pathList;
        this.isRemote = isRemote;
        this.isHaveLongClick = isHaveLongClick;
        viewMap = new HashMap<>();
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
        final ImageView imageView = view.findViewById(R.id.iv_pic);

//        Bitmap bitmap = BitmapFactory.decodeFile(pathList.get(position));
//
//        int bWidth = bitmap.getWidth();
//        int bHeight = bitmap.getHeight();
//
//        DisplayMetrics outMetrics = new DisplayMetrics();
//        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
//        int sWidth = outMetrics.widthPixels;
//        int sHeight = outMetrics.heightPixels;
//
//        float scale;
//        if (bWidth >= bHeight) {
//            scale = sWidth / bWidth;
//        } else {
//            scale = sHeight / bHeight;
//        }
//
//        Matrix matrix = new Matrix();
//        matrix.setScale(scale, scale);
//
//        Bitmap b = Bitmap.createBitmap(bitmap, 0, 0, bWidth, bHeight, matrix, true);
//        imageView.setImageBitmap(b);

        if (isRemote) {
            String url = pathList.get(position);
            final String path = QDStorePath.APP_PATH + url.substring(url.lastIndexOf("/") + 1);
            File file = new File(path);
            if (file.exists()) {
                Glide.with(context).load(path).apply(new RequestOptions().error(R.mipmap.ic_download_failed)).into(imageView);
            } else {
                imageView.setImageResource(R.mipmap.ic_download_loading);
                QDFileManager.getInstance().downloadFile(path, QDUtil.replaceWebServerAndToken(url), new QDFileDownLoadCallBack() {
                    @Override
                    public void onDownLoading(int per) {

                    }

                    @Override
                    public void onDownLoadSuccess() {
                        Glide.with(context).load(path).apply(new RequestOptions().error(R.mipmap.ic_download_failed)).into(imageView);
                    }

                    @Override
                    public void onDownLoadFailed(String msg) {
                        imageView.setImageResource(R.mipmap.ic_download_failed);
                    }
                });
            }
//            Glide.with(context).load(QDUtil.replaceWebServerAndToken(url)).error(R.mipmap.ic_download_failed).centerInside().into(imageView);
        } else {
            Glide.with(context).load(new File(pathList.get(position))).apply(new RequestOptions().error(R.mipmap.ic_download_failed)).into(imageView);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(position);
            }
        });

        if (isHaveLongClick) {
            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (itemLongClickListener != null) {
                        itemLongClickListener.onItemLongClick(position);
                    }
                    return false;
                }
            });
        }

        container.addView(view);
        viewMap.put(position, imageView);

        return view;
    }

    public void setItemClickListener(QDPicActivity.OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setItemLongClickListener(QDPicActivity.OnItemLongClickListener itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;
    }

    public ImageView getImageView(int position) {
        return viewMap.get(position);
    }
}
