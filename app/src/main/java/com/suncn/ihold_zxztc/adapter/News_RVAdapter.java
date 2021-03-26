package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GIDensityUtil;
import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GITextView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseFragment;
import com.suncn.ihold_zxztc.activity.hot.UnitListActivity;
import com.suncn.ihold_zxztc.bean.BaseTypeBean;
import com.suncn.ihold_zxztc.bean.NewsListBean;
import com.suncn.ihold_zxztc.bean.UnitListBean;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.MyVideoPlayer;
import com.suncn.ihold_zxztc.view.jzvd.JZMediaExo;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import cn.jzvd.JzvdStd;

/**
 * 首页新闻adapter
 */
public class News_RVAdapter extends BaseMultiItemQuickAdapter<BaseTypeBean, BaseViewHolder> {
    private Context context;
    private BaseFragment fragment;

    public News_RVAdapter(ArrayList<BaseTypeBean> data, Context context, BaseFragment fragment) {
        super(data);
        this.context = context;
        this.fragment = fragment;
        addItemType(1, R.layout.item_rv_news_unit);
        addItemType(0, R.layout.item_rv_news);
    }

    public News_RVAdapter(ArrayList<BaseTypeBean> data, Context context) {
        super(data);
        this.context = context;
        addItemType(1, R.layout.item_rv_news_unit);
        addItemType(0, R.layout.item_rv_news);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, BaseTypeBean baseTypeBean) {
        switch (baseViewHolder.getItemViewType()) {
            case 1:
                RecyclerView recyclerView = baseViewHolder.getView(R.id.recyclerView_top);
                recyclerView.setFocusableInTouchMode(false); //设置不需要焦点
                recyclerView.requestFocus(); //设置焦点不需要
                TextView tvMore = baseViewHolder.getView(R.id.tv_more);
                TextView tvTitle = baseViewHolder.getView(R.id.tv_title);
                LinearLayoutManager layoutManagerTop = new LinearLayoutManager(getContext());
                layoutManagerTop.setOrientation(LinearLayoutManager.HORIZONTAL);
                recyclerView.setLayoutManager(layoutManagerTop);
                UnitListBean unitListBean = (UnitListBean) baseTypeBean;
                tvTitle.setText(unitListBean.getStrRootUnitName());
                UnitListMain_RVAdapter adapter = new UnitListMain_RVAdapter(fragment);
                adapter.setList(unitListBean.getObjList());
                recyclerView.setAdapter(adapter);
                tvMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(context, UnitListActivity.class);
                        context.startActivity(intent);
                    }
                });
                break;
            default:
                MyVideoPlayer video_Player = baseViewHolder.getView(R.id.video_player);
                ImageView simpleImage_ImageView = baseViewHolder.getView(R.id.iv_simple_image);
                ImageView multiImage1_ImageView = baseViewHolder.getView(R.id.iv_multi_image1);
                ImageView multiImage2_ImageView = baseViewHolder.getView(R.id.iv_multi_image2);
                ImageView multiImage3_ImageView = baseViewHolder.getView(R.id.iv_multi_image3);
                GITextView title_TextView = baseViewHolder.getView(R.id.tv_title);
                GITextView department_TextView = baseViewHolder.getView(R.id.tv_department);
                GITextView time_TextView = baseViewHolder.getView(R.id.tv_time);
                LinearLayout video_LinearLayout = baseViewHolder.getView(R.id.ll_video);
                LinearLayout multiImage_LinearLayout = baseViewHolder.getView(R.id.ll_multi_image);
                TextView top_TextView = baseViewHolder.getView(R.id.tv_top);
                TextView hot_TextView = baseViewHolder.getView(R.id.tv_hot);
                GITextView tv_discussCount = baseViewHolder.getView(R.id.tv_discussCount);

                NewsListBean.NewsBean newsBean = (NewsListBean.NewsBean) baseTypeBean;
                title_TextView.setText(newsBean.getStrTitle());
                String strHot = GIStringUtil.nullToEmpty(newsBean.getStrHotState());
                if (strHot.equals("1")) {
                    hot_TextView.setVisibility(View.VISIBLE);
                } else {
                    hot_TextView.setVisibility(View.GONE);
                }
                String strTop = GIStringUtil.nullToEmpty(newsBean.getStrTopState());
                if (strTop.equals("1")) {
                    top_TextView.setVisibility(View.VISIBLE);
                } else {
                    top_TextView.setVisibility(View.GONE);
                }
                tv_discussCount.setText(newsBean.getIntReplyCount() + context.getString(R.string.string_comment));
                if (("1").equals(newsBean.getStrRecord())) {
                    title_TextView.setTextColor(context.getResources().getColor(R.color.font_source));
                } else {
                    title_TextView.setTextColor(context.getResources().getColor(R.color.font_title));
                }
                department_TextView.setText(newsBean.getStrPubUnit());
                time_TextView.setText(newsBean.getStrPubDate());
                ArrayList<NewsListBean.ImageInfo> imageInfos = newsBean.getImageWithVideoPathList();
                if (imageInfos != null) {
                    if (newsBean.getStrFileType() != null && newsBean.getStrFileType().contains("video")) {
                        simpleImage_ImageView.setVisibility(View.GONE);
                        multiImage_LinearLayout.setVisibility(View.GONE);
                        video_LinearLayout.setVisibility(View.VISIBLE);
                        String videoUrl = Utils.formatFileUrl(context, imageInfos.get(0).getStrVideoImagePath());
                        String defaultPhotoUrl = Utils.formatFileUrl(context, imageInfos.get(0).getStrThumbPath());

//                        int height = ((GIDensityUtil.getScreenW(context) - GIDensityUtil.sp2px(context, 20)) / 16) * 9;
//                        video_Player.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
                        video_Player.setUp(videoUrl, "", JzvdStd.SCREEN_NORMAL, JZMediaExo.class);
                        video_Player.widthRatio = 16;
                        video_Player.heightRatio = 9;
                        GIImageUtil.loadImg(context, video_Player.posterImageView, defaultPhotoUrl, 0);
                    } else {
                        video_LinearLayout.setVisibility(View.GONE);
                        if (imageInfos.size() == 0) {
                            simpleImage_ImageView.setVisibility(View.GONE);
                            multiImage_LinearLayout.setVisibility(View.GONE);
                        } else if (imageInfos.size() > 0 && imageInfos.size() < 3) {
                            simpleImage_ImageView.setVisibility(View.VISIBLE);
                            multiImage_LinearLayout.setVisibility(View.GONE);
                            GIImageUtil.loadImg(context, simpleImage_ImageView, Utils.formatFileUrl(context, imageInfos.get(0).getStrThumbPath()), 0);
                        } else {
                            simpleImage_ImageView.setVisibility(View.GONE);
                            multiImage_LinearLayout.setVisibility(View.VISIBLE);
                            GIImageUtil.loadImg(context, multiImage1_ImageView, Utils.formatFileUrl(context, imageInfos.get(0).getStrThumbPath()), 0);
                            GIImageUtil.loadImg(context, multiImage2_ImageView, Utils.formatFileUrl(context, imageInfos.get(1).getStrThumbPath()), 0);
                            GIImageUtil.loadImg(context, multiImage3_ImageView, Utils.formatFileUrl(context, imageInfos.get(2).getStrThumbPath()), 0);
                        }
                    }
                } else {
                    video_LinearLayout.setVisibility(View.GONE);
                    simpleImage_ImageView.setVisibility(View.GONE);
                    multiImage_LinearLayout.setVisibility(View.GONE);
                }
                break;
        }
    }

}
