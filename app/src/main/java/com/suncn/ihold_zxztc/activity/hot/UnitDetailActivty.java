package com.suncn.ihold_zxztc.activity.hot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ethanhua.skeleton.Skeleton;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.widget.RoundImageView;
import com.google.android.material.tabs.TabLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.MyViewPagerAdapter;
import com.suncn.ihold_zxztc.adapter.News_RVAdapter;
import com.suncn.ihold_zxztc.bean.BaseTypeBean;
import com.suncn.ihold_zxztc.bean.NewsColumnListBean;
import com.suncn.ihold_zxztc.bean.NewsListBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.MyVideoPlayer;
import com.suncn.ihold_zxztc.view.NetworkImageHolderView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;
import skin.support.content.res.SkinCompatResources;

/**
 * 独立机构详情主页面
 */
public class UnitDetailActivty extends BaseActivity {
    @BindView(id = R.id.rl_head_lab)
    private RelativeLayout headLab_RelativeLayout;//顶部栏目
    @BindView(id = R.id.tabLayout)
    private TabLayout tabLayout;//顶部MyTabLayout
    @BindView(id = R.id.viewPager)
    private ViewPager viewPager;
    @BindView(id = R.id.tv_empty, click = true)
    private ImageView empty_TextView;
    @BindView(id = R.id.tv_head_img)
    private RoundImageView tvHeadImg;
    @BindView(id = R.id.tv_name)
    private TextView tvName;
    @BindView(id = R.id.tv_state, click = true)
    private TextView tvState;
    @BindView(id = R.id.tv_myBack, click = true)
    private TextView tvBack;
    @BindView(id = R.id.iv_top)
    private ImageView ivTop;//头部背景

    private ArrayList<NewsListBean.NewsBean> objImageList;//顶部大图
    private List<NewsColumnListBean.NewsColumnBean> objList;
    private ArrayList<View> myViews;
    private ArrayList<News_RVAdapter> adapters;
    private ArrayList<Boolean> isNotLoads;

    private int currentIndex = 0;
    private int[] intCurPages;
    private String strColumnId = "-1"; // 栏目ID
    private String strRootUnitId = "";//单位id
    private String strPhotoUrl = "";//头像地址
    private String strUnitName = "";//名称
    private String strState = "";//订阅状态
    private String strBgUrl = "";//背景图地址

    @Override
    public void setRootView() {
        setStatusBar(true, false);
        setContentView(R.layout.activity_unit_detail_main);
    }

    @Override
    public void onResume() {
        super.onResume();
        JzvdStd.goOnPlayOnResume();
        if (null != tabLayout) { //换肤框架有一个bug,切换皮肤时TabLayout 的字体设置出现错误（显示为紫色），所以暂时在这里重新设置一下字体
            tabLayout.setTabTextColors(getResources().getColor(R.color.font_content), SkinCompatResources.getColor(activity, R.color.view_head_bg));
            tabLayout.setBackgroundColor(SkinCompatResources.getColor(activity, R.color.bg_news_tab));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        JzvdStd.goOnPlayOnPause();
        Jzvd.releaseAllVideos();
    }

    @Override
    public void initData() {
        super.initData();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            strRootUnitId = bundle.getString("strRootUnitId", "");
            strPhotoUrl = bundle.getString("strPhotoUrl", "");
            strUnitName = bundle.getString("strUnitName", "");
            strState = bundle.getString("strState", "");
            strBgUrl = bundle.getString("strBgUrl", "");
        }
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                if (-100 == sign) {
                    headLab_RelativeLayout.setVisibility(View.GONE);
                    viewPager.setVisibility(View.GONE);
                    empty_TextView.setVisibility(View.VISIBLE);
                } else {
//                    headLab_RelativeLayout.setVisibility(View.VISIBLE);
//                    viewPager.setVisibility(View.VISIBLE);
//                    empty_TextView.setVisibility(View.GONE);
                    doLogic(data, sign);
                }
            }
        };
        GIImageUtil.loadImg(activity, tvHeadImg, Utils.formatFileUrl(activity, strPhotoUrl), getResources().getDrawable(R.mipmap.unit_head_default));
        GIImageUtil.loadImg(activity, ivTop, Utils.formatFileUrl(activity, strBgUrl), 0);
        tvName.setText(strUnitName);
        if (strState.equals("1")) {
            tvState.setText(getString(R.string.string_subscribed));
            tvState.setBackground(getResources().getDrawable(R.drawable.shape_bg_gray_r3));
        } else {
            tvState.setText("+" + getString(R.string.string_subscription));
            tvState.setBackground(SkinCompatResources.getDrawable(activity, R.drawable.shape_bg_red_r3));
        }
        if (GISharedPreUtil.getString(activity, "strDefalutRootUnitId").equals(strRootUnitId)) {
            tvState.setVisibility(View.GONE);
        } else {
            tvState.setVisibility(View.VISIBLE);
        }
        getColunm();
    }

    /**
     * 获取新闻栏目
     */
    private void getColunm() {
        textParamMap = new HashMap<>();
        if (!GISharedPreUtil.getBoolean(activity, "isHasLogin")) {
            textParamMap.put("strUserId", GISharedPreUtil.getString(activity, "strUserId"));
        }
        textParamMap.put("strRootUnitId", strRootUnitId);
        doRequestNormal(ApiManager.getInstance().getNewsColumn(textParamMap), 0);
    }

    /**
     * 获取资讯列表
     */
    private void getListData(boolean b) {
        if (b) {
            skeletonScreen = Skeleton.bind(myViews.get(currentIndex).findViewById(R.id.recyclerView))
                    .adapter(adapters.get(currentIndex))
                    .load(R.layout.item_rv_news_skeleton)
                    .show();
        }
        textParamMap = new HashMap<>();
        if (!GISharedPreUtil.getBoolean(activity, "isHasLogin")) {
            textParamMap.put("strUserId", GISharedPreUtil.getString(activity, "strUserId"));
        }
        textParamMap.put("strRootUnitId", strRootUnitId);
        textParamMap.put("strId", strColumnId);
        textParamMap.put("intCurPage", intCurPages[currentIndex] + "");
        textParamMap.put("intPageSize", DefineUtil.GLOBAL_PAGESIZE + "");
        doRequestNormal(ApiManager.getInstance().getNewsList(textParamMap), 1);
    }

    /**
     * 订阅/取消订阅
     */
    public void doSubscribeNews() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strRootUnitId", strRootUnitId);
        doRequestNormal(ApiManager.getInstance().SubscribeNewsUnit(textParamMap), 4);
    }

    private void doLogic(Object data, int sign) {
        String totastMessage = null;
        try {
            switch (sign) {
                case 0:  // 栏目
                    NewsColumnListBean result = (NewsColumnListBean) data;
                    headLab_RelativeLayout.setVisibility(View.VISIBLE);
                    viewPager.setVisibility(View.VISIBLE);
                    empty_TextView.setVisibility(View.GONE);
                    objList = result.getNewsColumnList();
                    if (objList != null && objList.size() > 0) {
                        initTab();
                    } else {
                        headLab_RelativeLayout.setVisibility(View.GONE);
                        viewPager.setVisibility(View.GONE);
                        empty_TextView.setVisibility(View.VISIBLE);
                    }
                    break;
                case 1:
                    skeletonScreen.hide();
                    NewsListBean newsListBean = (NewsListBean) data;
                    View view = myViews.get(currentIndex);
                    SmartRefreshLayout smartRefreshLayout = view.findViewById(R.id.refreshLayout);
                    smartRefreshLayout.finishRefresh(true);
                    smartRefreshLayout.finishLoadMore(true);
                    if (newsListBean.getIntAllCount() > DefineUtil.GLOBAL_PAGESIZE * intCurPages[currentIndex]) {
                        smartRefreshLayout.setNoMoreData(false);
                    } else {
                        smartRefreshLayout.setNoMoreData(true);
                    }
                    News_RVAdapter adapter = adapters.get(currentIndex);
                    View headerView = adapter.getHeaderLayout();
                    if (headerView != null) {
                        ConvenientBanner convenientBanner = headerView.findViewById(R.id.convenientBanner);
                        if (convenientBanner != null) { // 头部Banner页
                            objImageList = newsListBean.getBannerNewsList();
                            if (objImageList != null && objImageList.size() > 0) {
                                convenientBanner.setVisibility(View.VISIBLE);
                                ArrayList<String> objTitleList = new ArrayList<>();
                                ArrayList<String> objPicPathList = new ArrayList<>();
                                for (int i = 0; i < objImageList.size(); i++) {
                                    NewsListBean.NewsBean objInfo = objImageList.get(i);
                                    objPicPathList.add(Utils.formatFileUrl(activity, objInfo.getStrThumbPath()));
                                    objTitleList.add(objInfo.getStrTitle());
                                }
                                convenientBanner.setPages(
                                        new CBViewHolderCreator<NetworkImageHolderView>() {
                                            @Override
                                            public NetworkImageHolderView createHolder() {
                                                return new NetworkImageHolderView();
                                            }
                                        }, objPicPathList)
                                        //设置指示器的方向
                                        .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)
                                        .startTurning(3000)
                                        .setPageIndicator(new int[]{R.drawable.shape_dot_normal, R.drawable.shape_dot_checked}) // 一定要在setImageTile之前设置翻页图片，为了给mPointViews赋值
                                        .setImageTitle(objTitleList);
                            } else {
                                convenientBanner.setVisibility(View.GONE);
                            }
                        }
                    }

                    ArrayList<NewsListBean.NewsBean> objList = newsListBean.getObjList();
                    ArrayList<BaseTypeBean> adapterList = new ArrayList<>(objList);
                    if (intCurPages[currentIndex] == 1) {
                        adapter.setList(adapterList);
                    } else {
                        adapter.addData(adapterList);
                    }

                    if (isNotLoads != null && isNotLoads.size() > 0) {
                        isNotLoads.set(currentIndex, true);
                    }
                    break;
                case 4:
                    prgDialog.closePrgDialog();
                    BaseTypeBean baseGlobal = (BaseTypeBean) data;
                    if (baseGlobal.getIntCount() == 1) {
                        showToast(getString(R.string.string_subcripe_success));
                        strState = "1";
                        tvState.setText(getString(R.string.string_subscribed));
                        tvState.setBackground(getResources().getDrawable(R.drawable.shape_bg_gray_r3));
                    } else {
                        showToast(getString(R.string.string_cancle_subcripe));
                        strState = "0";
                        tvState.setText("+" + getString(R.string.string_subscription));
                        tvState.setBackground(SkinCompatResources.getDrawable(activity, R.drawable.shape_bg_red_r3));
                    }
                    setResult(RESULT_OK);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            totastMessage = getString(R.string.data_error);
        }
        if (totastMessage != null) {
            showToast(totastMessage);
        }
    }

    /**
     * 初始化tab栏
     */
    private void initTab() {
        myViews = new ArrayList<View>();
        adapters = new ArrayList<News_RVAdapter>();
        isNotLoads = new ArrayList<>();
        intCurPages = new int[objList.size()];
        for (int i = 0; i < objList.size(); i++) {
            intCurPages[i] = 1;
            isNotLoads.add(false);
            initAddView(i);
        }
        MyViewPagerAdapter viewPagerAdapter = new MyViewPagerAdapter(myViews, objList);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(currentIndex);
        strColumnId = objList.get(currentIndex).getStrId();
        viewPager.addOnPageChangeListener(pageListener);
        tabLayout.setupWithViewPager(viewPager);
        getListData(true);
    }

    ViewPager.OnPageChangeListener pageListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int position, float offset, int arg2) {

        }

        @Override
        public void onPageSelected(int position) {
            Jzvd.releaseAllVideos();
            viewPager.setCurrentItem(position);
            currentIndex = position;
            strColumnId = objList.get(position).getStrId();
            if (!isNotLoads.get(currentIndex)) {
                getListData(true);
            }
        }
    };

    /**
     * 添加视图
     */
    private void initAddView(final int position) {
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_viewpager_add_view, null);
        SmartRefreshLayout smartRefreshLayout = view.findViewById(R.id.refreshLayout);
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                intCurPages[currentIndex] = 1;
                getListData(false);
            }
        });
        smartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                intCurPages[currentIndex] = intCurPages[currentIndex] + 1;
                getListData(false);
            }
        });

        final RecyclerView easyRecyclerView = view.findViewById(R.id.recyclerView);
        Utils.initEasyRecyclerView(activity, easyRecyclerView, false, false, R.color.line_bg_white, 0, 0);
        LinearLayoutManager manager = new LinearLayoutManager(activity);
        easyRecyclerView.setLayoutManager(manager); //给ERV添加布局管理器
        easyRecyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                MyVideoPlayer jzvd = view.findViewById(R.id.video_player);
                if (jzvd != null && Jzvd.CURRENT_JZVD != null && jzvd.jzDataSource != null && jzvd.jzDataSource.containsTheUrl(Jzvd.CURRENT_JZVD.jzDataSource.getCurrentUrl())) {
                    if (Jzvd.CURRENT_JZVD != null && Jzvd.CURRENT_JZVD.screen != Jzvd.SCREEN_FULLSCREEN) {
                        Jzvd.releaseAllVideos();
                    }
                }
            }
        });
        final News_RVAdapter adapter = new News_RVAdapter(new ArrayList<>(), activity);
        easyRecyclerView.setAdapter(adapter);
        if (position == 0)
            adapter.addHeaderView(getHeadView(easyRecyclerView));
        adapter.setEmptyView(R.layout.view_erv_empty);
        adapter.setOnItemClickListener(new com.chad.library.adapter.base.listener.OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                NewsListBean.NewsBean objInfo = (NewsListBean.NewsBean) adapter.getItem(position);
                doClickNewsItem(objInfo);
            }
        });

        adapters.add(adapter);
        myViews.add(view);
    }

    /**
     * 新闻列表被点击（跳转操作）
     */
    private void doClickNewsItem(NewsListBean.NewsBean objInfo) {
        Bundle bundle = new Bundle();
        bundle.putString("headTitle", getString(R.string.string_information_content));//objList.get(currentIndex).getStrName());
        bundle.putString("strUrl", objInfo.getStrUrl());
        bundle.putString("strNewsId", objInfo.getStrId());
        bundle.putSerializable("objShare", objInfo.getObjShare());
        bundle.putBoolean("isNews", true);
        showActivity(activity, NewsDetailActivity.class, bundle);
    }

    private View getHeadView(RecyclerView recyclerView) {
        View headerView = getLayoutInflater().inflate(R.layout.view_banner, recyclerView, false);
        ConvenientBanner convenientBanner = headerView.findViewById(R.id.convenientBanner);
        convenientBanner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (objImageList != null && objImageList.size() > 0) {
                    NewsListBean.NewsBean objInfo = objImageList.get(position);
                    doClickNewsItem(objInfo);
                }
            }
        });
        return headerView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_myBack:
                finish();
                break;
            case R.id.tv_state:
                doSubscribeNews();
                break;
            default:
                break;
        }
    }
}
