package com.suncn.ihold_zxztc.bean;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.widget.GITextView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.utils.RecyclerViewSpacesItemDecoration;
import com.suncn.ihold_zxztc.view.MyVideoPlayer;

import java.util.HashMap;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.gavin.giframe.utils.GIUtil.getContext;

//
public class NewsViewHolder extends BaseViewHolder {
    public MyVideoPlayer video_Player;
    public ImageView simpleImage_ImageView;
    public ImageView multiImage1_ImageView;
    public ImageView multiImage2_ImageView;
    public ImageView multiImage3_ImageView;
    public GITextView title_TextView;
    public GITextView department_TextView;
    public GITextView time_TextView;
    public LinearLayout multiImage_LinearLayout;
    public LinearLayout video_LinearLayout;
    public TextView top_TextView;
    public TextView hot_TextView;
    public GITextView tv_discussCount;
    public RecyclerView recyclerView;
    public TextView tvMore;
    public TextView tvTitle;

    public NewsViewHolder(View itemView) {
        super(itemView);
        video_Player = itemView.findViewById(R.id.video_player);
        simpleImage_ImageView = (ImageView) itemView.findViewById(R.id.iv_simple_image);
        multiImage1_ImageView = (ImageView) itemView.findViewById(R.id.iv_multi_image1);
        multiImage2_ImageView = (ImageView) itemView.findViewById(R.id.iv_multi_image2);
        multiImage3_ImageView = (ImageView) itemView.findViewById(R.id.iv_multi_image3);
        title_TextView = (GITextView) itemView.findViewById(R.id.tv_title);
        department_TextView = (GITextView) itemView.findViewById(R.id.tv_department);
        time_TextView = (GITextView) itemView.findViewById(R.id.tv_time);
        video_LinearLayout = (LinearLayout) itemView.findViewById(R.id.ll_video);
        multiImage_LinearLayout = (LinearLayout) itemView.findViewById(R.id.ll_multi_image);
        top_TextView = (TextView) itemView.findViewById(R.id.tv_top);
        hot_TextView = (TextView) itemView.findViewById(R.id.tv_hot);
        tv_discussCount = (GITextView) itemView.findViewById(R.id.tv_discussCount);
        recyclerView = itemView.findViewById(R.id.recyclerView_top);
        tvMore = itemView.findViewById(R.id.tv_more);
        tvTitle = itemView.findViewById(R.id.tv_title);
        LinearLayoutManager layoutManagerTop = new LinearLayoutManager(getContext());
        layoutManagerTop.setOrientation(LinearLayoutManager.HORIZONTAL);
        HashMap<String, Integer> hashMapTop = new HashMap<>();
        recyclerView.addItemDecoration(new RecyclerViewSpacesItemDecoration(hashMapTop));
        hashMapTop.put(RecyclerViewSpacesItemDecoration.RIGHT_DECORATION, 5);
        hashMapTop.put(RecyclerViewSpacesItemDecoration.LEFT_DECORATION, 5);
        recyclerView.addItemDecoration(new RecyclerViewSpacesItemDecoration(hashMapTop));
        //完成layoutManager设置
        recyclerView.setLayoutManager(layoutManagerTop);
    }

}