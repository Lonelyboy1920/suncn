package com.suncn.ihold_zxztc.activity.hot;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.ethanhua.skeleton.Skeleton;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GITextView;
import com.gavin.giframe.widget.RoundImageView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.LoginActivity;
import com.suncn.ihold_zxztc.activity.MainActivity;
import com.suncn.ihold_zxztc.activity.application.meeting.QRCodeCheckActivity;
import com.suncn.ihold_zxztc.activity.base.BaseFragment;
import com.suncn.ihold_zxztc.activity.dialog.SendFlowerDialog;
import com.suncn.ihold_zxztc.activity.global.OverSearchListActivity;
import com.suncn.ihold_zxztc.activity.global.WebViewActivity;
import com.suncn.ihold_zxztc.activity.my.MyActivity;
import com.suncn.ihold_zxztc.adapter.MyViewPagerAdapter;
import com.suncn.ihold_zxztc.adapter.News_RVAdapter;
import com.suncn.ihold_zxztc.bean.BaseTypeBean;
import com.suncn.ihold_zxztc.bean.ActivityBean;
import com.suncn.ihold_zxztc.bean.IosAttendBean;
import com.suncn.ihold_zxztc.bean.NewsColumnListBean;
import com.suncn.ihold_zxztc.bean.NewsListBean;
import com.suncn.ihold_zxztc.bean.UnitListBean;
import com.suncn.ihold_zxztc.rxhttp.update.MyUpdateParser;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.GalleryCycleImageView;
import com.suncn.ihold_zxztc.view.MyVideoPlayer;
import com.suncn.ihold_zxztc.view.NetworkImageHolderView;
import com.xuexiang.xupdate.XUpdate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;
import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import skin.support.content.res.SkinCompatResources;
import skin.support.design.widget.SkinMaterialTabLayout;

/**
 * 热点主界面
 */
public class HotMainFragment extends BaseFragment {
    @BindView(id = R.id.rl_top)
    private RelativeLayout rl_top;//顶部标题栏（背景可动态更换）
    @BindView(id = R.id.iv_home_top)
    private ImageView topPhoto_ImageView;//顶部标题栏--左边图片（可动态更换）
    @BindView(id = R.id.iv_home_person, click = true)
    private RoundImageView homePerson_ImageView;//顶部标题栏--右边头像（我的界面入口）
    @BindView(id = R.id.tv_robot, click = true)
    private GITextView robot_TextView;//顶部标题栏--机器人图标（安安入口）
    @BindView(id = R.id.tv_search, click = true)
    private GITextView search_TextView;//顶部标题栏--搜索图标（全局搜索入口）
    @BindView(id = R.id.ll_integral, click = true)
    private LinearLayout llInterGral;//顶部标题栏--积分LinearLayout（积分签到入口）
    @BindView(id = R.id.tv_integral)
    private TextView tvIntegral; //顶部标题栏--积分分数
    @BindView(id = R.id.rl_head_lab)
    private RelativeLayout headLab_RelativeLayout;//新闻栏目RelativeLayout
    @BindView(id = R.id.tabLayout)
    private SkinMaterialTabLayout tabLayout;//新闻栏目TabLayout
    @BindView(id = R.id.tv_setting, click = true)
    private GITextView setting_TextView;//栏目设置按钮
    @BindView(id = R.id.viewPager)
    private ViewPager viewPager; // 新闻ViewPager
    @BindView(id = R.id.tv_empty, click = true)
    private ImageView empty_TextView; // 列表无数据时显示的图片
    @BindView(id = R.id.ll_skeleton)
    private LinearLayout skeleton_Layout; //
    @BindView(id = R.id.tv_scan, click = true)
    private GITextView tv_scan;

    private SendFlowerDialog dialogSys;
    private SendFlowerDialog dialogFlower;
    private SendFlowerDialog dialogBorth;
    private SendFlowerDialog dialogEven;
    private int dialogCount = 0;//是否有两个送花弹窗
    private ActivityBean activityBean;

    private ArrayList<NewsListBean.NewsBean> objImageList;//顶部大图
    private List<NewsColumnListBean.NewsColumnBean> objList;
    private ArrayList<View> myViews;
    private ArrayList<News_RVAdapter> adapters;
    private ArrayList<Boolean> isNotLoads;
    private int currentIndex = 0;
    private int[] intCurPages;
    private String strColumnId = "-1"; // 栏目ID
    private int showRootUnitTab = -1;
    private UnitListBean unitList;//独立机构单位列表
    private int curShowEventPosition = 0;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1: // 刷新积分数据
                    //getIntegral();
                    break;
                case 2: // 修改频道订阅后刷新栏目和列表
                    if (data != null) {
                        Bundle bundle = data.getExtras();
                        if (bundle != null) {
                            currentIndex = bundle.getInt("currentIndex", 0);
                        }
                    }
                    if (currentIndex == 0) {
                        GISharedPreUtil.setValue(activity, "strClickInfo", null);
                    }
                    getColunm();
                    break;
//                default:
//                    getColunm();
//                    break;
            }
        }
    }

    @Override
    public View inflaterView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hot_main, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (((MainActivity) getActivity()).currentFragment == this) {
            if (GISharedPreUtil.getBoolean(getActivity(), "isHasLogin", false)) {
                //getIntegral();
                getIsHasEvent();
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            JzvdStd.goOnPlayOnPause();
            Jzvd.releaseAllVideos();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        JzvdStd.goOnPlayOnPause();
        Jzvd.releaseAllVideos();
    }


    @Override
    public void onStop() {
        super.onStop();
        JzvdStd.goOnPlayOnPause();
    }

    @Override
    public void initData() {
        setStatusBar(false);
        GILogUtil.e("initData，，，，，，，");
        if (getResources().getBoolean(R.bool.IS_OPEN_ROBOT)) {
            robot_TextView.setVisibility(View.VISIBLE);
        } else {
            robot_TextView.setVisibility(View.GONE);
        }
        llInterGral.setVisibility(View.GONE);
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                if (-100 == sign) {
                    if (null != skeletonScreen) {
                        skeletonScreen.hide();
                    }
                    headLab_RelativeLayout.setVisibility(View.GONE);
                    viewPager.setVisibility(View.GONE);
                    empty_TextView.setVisibility(View.VISIBLE);
                } else {
                    headLab_RelativeLayout.setVisibility(View.VISIBLE);
                    viewPager.setVisibility(View.VISIBLE);
                    empty_TextView.setVisibility(View.GONE);
                    doLogic(data, sign);
                }
            }
        };
        String strAppName = GISharedPreUtil.getString(activity, "strAppName"); // 动态更换状态栏左侧项目名称图片
        if (GIStringUtil.isNotBlank(strAppName)) {
            GIImageUtil.loadImg(activity, topPhoto_ImageView, Utils.formatFileUrl(activity, strAppName), getResources().getDrawable(R.mipmap.img_home_top));
        }
        String strAppHomeTop = GISharedPreUtil.getString(activity, "strAppHomeTop"); // 动态更换状态栏背景图片
        if (GIStringUtil.isNotBlank(strAppHomeTop)) {
            GIImageUtil.setBgForImage(activity, rl_top, Utils.formatFileUrl(activity, strAppHomeTop));
        }

        if (GISharedPreUtil.getBoolean(activity, "isCheckUpdate", true))
            checkUpdate();
        getColunm();
        GIImageUtil.loadImg(activity, homePerson_ImageView, GISharedPreUtil.getString(activity, "strPhotoUrl"), 1);
        GIImageUtil.loadImg(activity, homePerson_ImageView, GISharedPreUtil.getString(activity, "strPhotoUrl"), 1);
        JzvdStd.goOnPlayOnResume();
        if (null != tabLayout) { //换肤框架有一个bug,切换皮肤时TabLayout 的字体设置出现错误（显示为紫色），所以暂时在这里重新设置一下字体
            tabLayout.setTabTextColors(getResources().getColor(R.color.font_content), SkinCompatResources.getColor(activity, R.color.view_head_bg));
            tabLayout.setBackgroundColor(SkinCompatResources.getColor(activity, R.color.bg_news_tab));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_empty:
                getColunm();
                break;
            case R.id.iv_home_person:
                if (GISharedPreUtil.getBoolean(getActivity(), "isHasLogin", false)) {
                    showActivity(fragment, MyActivity.class);
                } else {
//                    LoginInterceptor.interceptor(activity, "MyActivity", null); // 需要做是否已登录的验证
                    Bundle bundle = new Bundle();
                    bundle.putInt("currentIndex", 2);
                    showActivity(fragment, LoginActivity.class, bundle);
                }
                break;
            case R.id.tv_search:
                Bundle bundle = new Bundle();
                bundle.putInt("searchType", 0);
                showActivity(fragment, OverSearchListActivity.class, bundle);
                break;
            case R.id.tv_robot:
                showActivity(fragment, RobotActivity.class);
                break;
            case R.id.tv_setting:
                showActivity(fragment, ColumnSettingActivity.class, 2);
                break;
            case R.id.ll_integral:
                showActivity(fragment, IntegralWebViewActivity.class, 1);
                break;
            case R.id.tv_scan:
                HiPermission.create(activity).checkSinglePermission(Manifest.permission.CAMERA, new PermissionCallback() {
                    @Override
                    public void onClose() {

                    }

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onDeny(String permission, int position) {

                    }

                    @Override
                    public void onGuarantee(String permission, int position) {
                        showActivity(fragment, QRCodeCheckActivity.class, 0);
                    }
                });
                break;
            default:
                break;
        }
    }

    /**
     * 检查更新
     */
    private void checkUpdate() {
//        textParamMap = new HashMap<>();
//        textParamMap.put("intType", "20");
//        doRequestNormal(ApiManager.getInstance().checkUpdates(textParamMap), 2);
        String mUpdateUrl = Utils.formatFileUrl(activity, DefineUtil.api_update);
        XUpdate.newBuild(activity).updateUrl(mUpdateUrl).updateParser(new MyUpdateParser(activity)).update();
        GISharedPreUtil.setValue(activity, "isCheckUpdate", false);
    }

    /**
     * 获取积分
     */
    private void getIntegral() {
        textParamMap = new HashMap<>();
        doRequestNormal(ApiManager.getInstance().GetALlIntegralServlet(textParamMap), 5);
    }

    /**
     * 获取生日祝福参数
     */
    private void getBirthParm() {
        textParamMap = new HashMap<>();
        doRequestNormal(ApiManager.getInstance().GetBirthDayDataServlet(textParamMap), 6);
    }

    /**
     * 隐藏生日祝福
     */
    public void hideBirthParm() {
        textParamMap = new HashMap<>();
        doRequestNormal(ApiManager.getInstance().HideBirthPopupWithLoginUserServlet(textParamMap), 7);
    }

    /**
     * 获取是否有运营活动
     */
    public void getIsHasEvent() {
        textParamMap = new HashMap<>();
        doRequestNormal(ApiManager.getInstance().IsHavingEventServlet(textParamMap), 8);
    }

    /**
     * 获取新闻栏目
     */
    private void getColunm() {
        textParamMap = new HashMap<>();
        if (!GISharedPreUtil.getBoolean(activity, "isHasLogin")) {
            textParamMap.put("strUserId", GISharedPreUtil.getString(activity, "strUserId"));
        }
        if (ProjectNameUtil.isGYSZX(activity)
                && GIStringUtil.isNotBlank(GISharedPreUtil.getString(activity, "strWebUrl"))) {
            doRequestNormal(ApiManager.getInstance().getNewsColumnGy(textParamMap), 0);
        } else {
            doRequestNormal(ApiManager.getInstance().getNewsColumn(textParamMap), 0);
        }
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
        textParamMap.put("strId", strColumnId);
        textParamMap.put("intCurPage", intCurPages[currentIndex] + "");
        textParamMap.put("intPageSize", DefineUtil.GLOBAL_PAGESIZE + "");
        if (ProjectNameUtil.isGYSZX(activity)
                && GIStringUtil.isNotBlank(GISharedPreUtil.getString(activity, "strWebUrl"))) {
            doRequestNormal(ApiManager.getInstance().getNewsListGy(textParamMap), 1);
        } else {
            doRequestNormal(ApiManager.getInstance().getNewsList(textParamMap), 1);
        }
    }

    /**
     * 获取独立机构列表
     */
    private void getUnitList() {
        textParamMap = new HashMap<>();
        //textParamMap.put("strType", "1");
        doRequestNormal(ApiManager.getInstance().getUnitList(textParamMap), 3);
    }

    /**
     * 订阅取消订阅
     */
    public void doSubscribeNews(String strRootUnitId) {
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
                    if (GISharedPreUtil.getBoolean(getActivity(), "isHasLogin", false)) {
                        setting_TextView.setVisibility(View.VISIBLE);
                    } else {
                        setting_TextView.setVisibility(View.GONE);
                    }
                    break;
                case 1:
                    if (null != skeletonScreen) {
                        skeletonScreen.hide();
                    }
                    NewsListBean newsListBean = (NewsListBean) data;
                    View view = myViews.get(currentIndex);
                    SmartRefreshLayout smartRefreshLayout = view.findViewById(R.id.refreshLayout);
                    News_RVAdapter adapter = adapters.get(currentIndex);
                    adapter.removeAllHeaderView();
                    RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
                    objImageList = newsListBean.getBannerNewsList();
                    if (objImageList != null) {
                        adapter.addHeaderView(getHeadView(recyclerView));
                    }

                    View headerView = adapter.getHeaderLayout();
                    if (headerView != null) {
                        ConvenientBanner convenientBanner = headerView.findViewById(R.id.convenientBanner);
                        if (convenientBanner != null) { // 头部Banner页
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
                    Utils.setLoadMoreViewStateHasHeader(newsListBean.getIntAllCount(), intCurPages[currentIndex] * DefineUtil.GLOBAL_PAGESIZE
                            , "", smartRefreshLayout, adapter);
                    ArrayList<NewsListBean.NewsBean> objList = newsListBean.getObjList();
                    ArrayList<BaseTypeBean> adapterList = new ArrayList<>(objList);
                    if (intCurPages[currentIndex] == 1 && showRootUnitTab == currentIndex && unitList != null) {
                        unitList.setIntItemType(1);
                        adapterList.add(1, unitList);
                    }
                    if (intCurPages[currentIndex] == 1) {
                        adapter.setList(adapterList);
                    } else {
                        adapter.addData(adapterList);
                    }

                    if (isNotLoads != null && isNotLoads.size() > 0) {
                        isNotLoads.set(currentIndex, true);
                    }
                    break;
                case 3:
                    UnitListBean unitListBean = (UnitListBean) data;
                    unitList = unitListBean;
                    getListData(true);
                    break;
                case 4:
                    BaseTypeBean baseGlobal = (BaseTypeBean) data;
                    if (baseGlobal.getIntCount() == 1) {
                        showToast(getString(R.string.string_subcripe_success));
                    } else {
                        showToast(getString(R.string.string_cancle_subcripe));
                    }
                    break;
                case 5:
                    if (GISharedPreUtil.getString(activity, "strIsCollective").equals("1")) {
                        llInterGral.setVisibility(View.GONE);
                    } else {
                        IosAttendBean iosAttendBean = (IosAttendBean) data;
                        tvIntegral.setText(iosAttendBean.getIntALlIntegral());
                        llInterGral.setVisibility(View.VISIBLE);
                    }
                    break;
                case 8:
                    activityBean = (ActivityBean) data;
                    getListData(true);
                    if (activityBean.getPopList() != null && activityBean.getPopList().size() > 0) {
                        dialogEven = new SendFlowerDialog(activity, fragment, R.style.shareDialog, activityBean.getPopList().get(curShowEventPosition).getStrUrl());
                        dialogEven.show();
                        dialogEven.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                if (curShowEventPosition < activityBean.getPopList().size() - 1) {
                                    curShowEventPosition++;
                                    dialogEven.setUrl(activityBean.getPopList().get(curShowEventPosition).getStrUrl());
                                    dialogEven.show();
                                }
                            }
                        });
                    }
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
        myViews = new ArrayList<>();
        adapters = new ArrayList<>();
        isNotLoads = new ArrayList<>();
        intCurPages = new int[objList.size()];
        for (int i = 0; i < objList.size(); i++) {
            if ("1".equals(objList.get(i).getShowRootUnit())) {
                showRootUnitTab = i;
            }
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
        // 使用线程，解决adapter.addHeader(itemView)无法渲染的问题导致convenientBanner为null的问题
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (GISharedPreUtil.getBoolean(activity, "isHasLogin")) {
                    getIsHasEvent();
                } else {
                    getListData(false);
                }
            }
        });
    }

    /**
     * viewpage页面切换监听
     */
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
            GISharedPreUtil.setValue(activity, "strClickInfo", strColumnId);
            if (!isNotLoads.get(currentIndex)) {
                getListData(true);
            }
        }
    };

    /**
     * 初始化头部View（Banner+运营活动）
     */
    private View getHeadView(RecyclerView recyclerView) {
        View headerView = getLayoutInflater().inflate(R.layout.view_banner, recyclerView, false);
        ConvenientBanner convenientBanner = headerView.findViewById(R.id.convenientBanner);
        GalleryCycleImageView iv_wish = headerView.findViewById(R.id.iv_wish);
        convenientBanner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (objImageList != null && objImageList.size() > 0) {
                    NewsListBean.NewsBean objInfo = objImageList.get(position);
                    doClickNewsItem(objInfo);
                }
            }
        });
        if (activityBean != null && activityBean.getBannerList() != null && activityBean.getBannerList().size() > 0) {
            iv_wish.setImgList(activityBean.getBannerList());
            iv_wish.setCount(activityBean.getBannerList().size());
        } else {
            iv_wish.setVisibility(View.GONE);
        }
        iv_wish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!GISharedPreUtil.getBoolean(activity, "isHasLogin")) {
                    showActivity(fragment, LoginActivity.class);
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("strUrl", activityBean.getBannerList().get(iv_wish.getCurPosion()).getStrUrl());
                showActivity(fragment, IntegralWebViewActivity.class, bundle);

            }
        });
        return headerView;
    }

    /**
     * 添加视图(for循环添加)
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

        final RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.line_bg_white, 0, 0);
        LinearLayoutManager manager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(manager); //给ERV添加布局管理器
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
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
        final News_RVAdapter adapter = new News_RVAdapter(new ArrayList<>(), activity, (BaseFragment) fragment);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new com.chad.library.adapter.base.listener.OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (((BaseTypeBean) adapter.getData().get(position)).getItemType() == 1) {
                    return;
                }
                NewsListBean.NewsBean objInfo = (NewsListBean.NewsBean) adapter.getItem(position);
                doClickNewsItem(objInfo);
                objInfo.setStrRecord("1");
                adapter.notifyDataSetChanged();
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
        showActivity(fragment, NewsDetailActivity.class, bundle);
    }
}
