package com.suncn.ihold_zxztc.view;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.gavin.giframe.utils.GIImageUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.utils.Utils;

/**
 * convenientbanner获取网络图片
 */
public class NetworkImageHolderView implements Holder<String> {
    private ImageView imageView;


    @Override
    public View createView(Context context) {
        imageView = new ImageView(context);
//        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setBackgroundColor(Color.parseColor("#f1f1f1"));
        return imageView;
    }

    @Override
    public void UpdateUI(Context context, final int position, String data) {
        GIImageUtil.loadImg(context, imageView, Utils.formatFileUrl(context, data), 0);

    }
}
