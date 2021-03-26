package com.suncn.ihold_zxztc.activity.duty;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideTopExit;
import com.flyco.dialog.widget.popup.base.BasePopup;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIDensityUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;
import com.gavin.giframe.widget.ListViewForScrollView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.MemberDuty_Rank_LVAdapter;
import com.suncn.ihold_zxztc.adapter.MemberDuty_ScoreDetail_LVAdapter;
import com.suncn.ihold_zxztc.adapter.MemberDuty_Score_LVAdapter;
import com.suncn.ihold_zxztc.bean.MemRecordScoreBean;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;
import com.suncn.ihold_zxztc.view.RadarView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import skin.support.content.res.SkinCompatResources;

/**
 * 履职得分情况
 */
public class MemberDuty_ScoreDetailActivity extends BaseActivity {
    @BindView(id = R.id.scrollview)
    private ScrollView scrollView; //
    @BindView(id = R.id.lv_score)
    private ListViewForScrollView score_ListView; // 得分ListView
    @BindView(id = R.id.tv_allScore)
    private TextView allScore_TextView; // 总得分
    @BindView(id = R.id.rv_view)
    private RadarView radarView; // 能力雷达图
    @BindView(id = R.id.tv_details)
    private TextView details_TextView; // 能力分析信息
    @BindView(id = R.id.tv_label_more, click = true)
    private TextView moreLabel_TextView; // 查看更多按钮
    @BindView(id = R.id.ll_menu)
    private LinearLayout menu_Layout; // 履职排行类型
    @BindView(id = R.id.view_line)
    private View line_View; //
    @BindView(id = R.id.lv_rank)
    private RecyclerView rank_ListView; // 履职排行ListView
    @BindView(id = R.id.llMemberDutyAnalysis)
    private LinearLayout llMemberDutyAnalysis;  //履职分析

    private MemberDuty_Rank_LVAdapter rankLvAdapter;
    private ArrayList<MemRecordScoreBean.ScoreRankListBean> scoreRankList;
    private String strYear = "";
    private String strUserId = "";
    private String rules = "";//计分规则

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_memberduty_score_detail);
        isShowBackBtn = true;
    }

    @Override
    public void initData() {
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(sign, data);
            }
        };
        setHeadTitle(getString(R.string.string_duty_ranking));
        Bundle bundle = getIntent().getExtras();
        setRecyclerViewStyle();
        rankLvAdapter = new MemberDuty_Rank_LVAdapter(activity, 0);
        rank_ListView.setAdapter(rankLvAdapter);
        rankLvAdapter.setEmptyView(R.layout.view_erv_empty); // 设置无数据时的view
        if (ProjectNameUtil.isSZSZX(activity) || ProjectNameUtil.isJMSZX(activity) || ProjectNameUtil.isCZSZX(activity)) {
            moreLabel_TextView.setVisibility(View.GONE);
        }
        if (bundle != null) {
            strUserId = bundle.getString("strUserId");
            strYear = bundle.getString("strYear");
            getMemDetail();
        }

        if (ProjectNameUtil.isSZSZX(activity)) {
            llMemberDutyAnalysis.setVisibility(View.GONE);
        }
    }

    /**
     * 设置RecyclerView的排序方向和边距
     */
    private void setRecyclerViewStyle() {
        LinearLayoutManager layoutManagerBottom = new LinearLayoutManager(activity);
        layoutManagerBottom.setOrientation(LinearLayoutManager.VERTICAL);
        //完成layoutManager设置
        rank_ListView.setLayoutManager(layoutManagerBottom);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_label_more: // 查看更多排行
                Bundle bundle = new Bundle();
                bundle.putSerializable("scoreRankList", scoreRankList);
                bundle.putString("strUserId", strUserId);
                bundle.putString("strYear", strYear);
                showActivity(activity, MemberDuty_RankListActivity.class, bundle);
                break;
            default:
                break;
        }
        super.onClick(v);
    }


    @Override
    public void setListener() {
        score_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MemRecordScoreBean.AbilityAnalysisBean.RadarIndicatorListBean radarIndicatorListBean = (MemRecordScoreBean.AbilityAnalysisBean.RadarIndicatorListBean) adapterView.getItemAtPosition(i);
                showMyDialog(radarIndicatorListBean);
            }
        });

        super.setListener();
    }

    /**
     * 获取委员履职信息
     */
    private void getMemDetail() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strYear", strYear); // 年份，默认当前年
        if (GIStringUtil.isNotBlank(strUserId))
            textParamMap.put("strUserId", strUserId); // 委员用户名（非委员登录时必输）
        doRequestNormal(ApiManager.getInstance().getMemRecordScore(textParamMap), 0);
    }

    /**
     * 请求结果
     */
    private void doLogic(int what, Object obj) {
        String toastMessage = null;
        switch (what) {
            case 0:
                prgDialog.closePrgDialog();
                try {
                    MemRecordScoreBean memRecordScoreBean = (MemRecordScoreBean) obj;
                    allScore_TextView.setText(memRecordScoreBean.getDbAllScore() + "");
                    MemRecordScoreBean.AbilityAnalysisBean abilityAnalysis = memRecordScoreBean.getAbilityAnalysis();
                    rules = memRecordScoreBean.getScoreProportionShow();
                    String strRankShow = abilityAnalysis.getStrRankShow();
                    if (GIStringUtil.isNotBlank(strRankShow))
                        details_TextView.setText(GIUtil.showHtmlInfo(strRankShow));
                    List<MemRecordScoreBean.AbilityAnalysisBean.RadarIndicatorListBean> radarIndicatorList = abilityAnalysis.getRadarIndicatorList();
                    if (radarIndicatorList != null && radarIndicatorList.size() > 0) {
                        score_ListView.setAdapter(new MemberDuty_Score_LVAdapter(activity, radarIndicatorList));
                        initRadar(radarIndicatorList);
                    }
                    scoreRankList = (ArrayList<MemRecordScoreBean.ScoreRankListBean>) memRecordScoreBean.getScoreRankList();
                    initMenu();
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            default:
                break;
        }

        if (toastMessage != null) {
            showToast(toastMessage);
        }
    }

    /**
     * 初始化履职排行界面
     */
    private void initMenu() {
        if (scoreRankList != null && scoreRankList.size() > 0) {
            MemRecordScoreBean.ScoreRankListBean scoreRankListBean;
            for (int i = 0; i < scoreRankList.size(); i++) {
                scoreRankListBean = scoreRankList.get(i);
                final TextView textView = new TextView(activity);
                textView.setText(scoreRankListBean.getStrRankName());
                textView.setTextColor(getResources().getColor(R.color.font_title));
                textView.setTextSize(16);
                textView.setGravity(Gravity.CENTER);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, GIDensityUtil.dip2px(activity, 40));
                params.gravity = Gravity.CENTER;
                params.weight = 1;
                textView.setId(i);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        int count = menu_Layout.getChildCount();
                        for (int j = 0; j < count; j++) {
                            ((TextView) menu_Layout.getChildAt(j)).setTextColor(getResources().getColor(R.color.font_title));
                        }
                        ((TextView) view).setTextColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
                        int pos = view.getId();
                        MemRecordScoreBean.ScoreRankListBean scoreRankListBean = scoreRankList.get(pos);
                        rankLvAdapter.setList(scoreRankListBean.getRankList());
                        rankLvAdapter.notifyDataSetChanged();
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(view.getWidth(), GIDensityUtil.dip2px(activity, 2));
                                params.leftMargin = view.getLeft();
                                line_View.setLayoutParams(params);
                                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        });
                    }
                });
                if (i == 0) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(textView.getWidth(), GIDensityUtil.dip2px(activity, 2));
                            params.leftMargin = 0;
                            line_View.setLayoutParams(params);
                        }
                    });
                    textView.setTextColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
                    rankLvAdapter.setList(scoreRankList.get(0).getRankList());
                    rankLvAdapter.notifyDataSetChanged();
                }
                menu_Layout.addView(textView, params);
            }
        }
    }

    /**
     * 初始化雷达图
     */
    private void initRadar(List<MemRecordScoreBean.AbilityAnalysisBean.RadarIndicatorListBean> radarList) {
        if (radarList != null && radarList.size() > 0) {
            String[] radarName = new String[radarList.size()];
            ArrayList<Double> data = new ArrayList<>();
            for (int i = 0; i < radarList.size(); i++) {
                radarName[i] = radarList.get(i).getStrTypeName();
                data.add(Double.valueOf(radarList.get(i).getDbScoreRatio()));
            }
            radarView.setTitles(radarName);
            radarView.setData(data);
            radarView.setMaxValue(1);
            radarView.setValuePaintColor(SkinCompatResources.getColor(activity, R.color.view_head_bg)); // 阴影颜色
            radarView.setStrokeWidth(3f);
            radarView.setMainPaintColor(Color.parseColor("#919191")); // 背景边线颜色
            radarView.setTextPaintColor(Color.parseColor("#666666")); // Label字体颜色
            radarView.setCircleRadius(0f);
            radarView.setTextPaintTextSize(28);
            radarView.setInnerAlpha(130);
            radarView.setLableCount(radarList.size());
            radarView.setDrawLabels(false);
            radarView.setShowValueText(false);
            radarView.invalidate();
        }
    }

    /**
     * 得分详细对话框
     */
    private SimpleCustomPop mQuickCustomPopup;

    public void showMyDialog(MemRecordScoreBean.AbilityAnalysisBean.RadarIndicatorListBean radarIndicatorListBean) {
        if (mQuickCustomPopup == null || !mQuickCustomPopup.isShowing()) {
            mQuickCustomPopup = new SimpleCustomPop(activity, radarIndicatorListBean);
            mQuickCustomPopup
                    .alignCenter(true)
                    .anchorView(findViewById(R.id.rl_head))
                    .gravity(Gravity.BOTTOM)
                    .showAnim(new BounceTopEnter())
                    .dismissAnim(new SlideTopExit())
                    .offset(0, 0)
                    .dimEnabled(true)
                    .show();
        }
    }

    class SimpleCustomPop extends BasePopup<SimpleCustomPop> {
        private TextView close_TextView;
        private TextView name_TextView;
        private TextView score_TextView;//得分TextView
        private ListView scoreDetail_ListView;//得分详情ListView
        private TextView rules_TextView;//得分规则TextView
        private MemRecordScoreBean.AbilityAnalysisBean.RadarIndicatorListBean radarIndicatorListBean;

        public SimpleCustomPop(Context context, MemRecordScoreBean.AbilityAnalysisBean.RadarIndicatorListBean radarIndicatorListBean) {
            super(context);
            this.radarIndicatorListBean = radarIndicatorListBean;
            setCanceledOnTouchOutside(false);
        }

        @Override
        public View onCreatePopupView() {
            View view = View.inflate(activity, R.layout.dialog_memberduty_score_detail, null);
            close_TextView = view.findViewById(R.id.tv_close);
            name_TextView = view.findViewById(R.id.tv_name);
            score_TextView = view.findViewById(R.id.tv_score);
            rules_TextView = view.findViewById(R.id.tv_rules);
            scoreDetail_ListView = view.findViewById(R.id.lv_scoreDetail);
            return view;
        }

        @Override
        public void setUiBeforShow() {
            name_TextView.setText(radarIndicatorListBean.getStrTypeName());
            score_TextView.setText(radarIndicatorListBean.getDbScore() + "");
            rules_TextView.setText(rules);
            scoreDetail_ListView.setAdapter(new MemberDuty_ScoreDetail_LVAdapter(activity, radarIndicatorListBean.getExamList()));
            close_TextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }
    }
}
