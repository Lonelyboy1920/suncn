package com.suncn.ihold_zxztc.activity.application.proposal;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceEnter;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideTopExit;
import com.flyco.animation.ZoomExit.ZoomOutExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.flyco.dialog.widget.popup.base.BasePopup;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.application.meeting.MeetSpeak_NoticeListActivity;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.adapter.Proposal_JoinSupport_RVAdapter;
import com.suncn.ihold_zxztc.bean.BaseGlobal;
import com.suncn.ihold_zxztc.bean.JoinSupportListBean;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.OptionSearch;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.ClearEditText;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 联名人、附议人列表Activity
 */
public class Proposal_JoinSupportListActivity extends BaseActivity {
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;
    @BindView(id = R.id.ll_title) //标题
    private LinearLayout llTitle;
    @BindView(id = R.id.ll_search)//标题的搜索布局
    private LinearLayout llSearch;
    @BindView(id = R.id.et_search)//标题搜索框
    private ClearEditText etSearch;
    @BindView(id = R.id.tv_cancel, click = true)
    private TextView tvCancel;
    @BindView(id = R.id.refreshLayout)
    private SmartRefreshLayout refreshLayout;

    private Proposal_JoinSupport_RVAdapter adapter;
    private int curPage = 1;
    private String strKeyValue = "";
    private String strType = "";
    private String strId = "";

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setStatusBar();
        setContentView(R.layout.activity_recyclerview_nopadding);
    }

    @Override
    public void initData() {
        super.initData();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String headTitle = bundle.getString("headTitle");
            strId = bundle.getString("strId");
            strType = bundle.getString("strType");
            setHeadTitle(headTitle);
        }

        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        initRecyclerView();
        getJoinSupportList(1, true);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_goto:
               /* if (llTitle.getVisibility() == View.VISIBLE) {
                    llTitle.setVisibility(View.GONE);
                    llSearch.setVisibility(View.VISIBLE);
                    goto_Button.setVisibility(View.INVISIBLE);
                    tvCancel.setVisibility(View.VISIBLE);
                }*/
                showActivity(activity, MeetSpeak_NoticeListActivity.class);
                break;
            case R.id.tv_cancel:
                llTitle.setVisibility(View.VISIBLE);
                llSearch.setVisibility(View.GONE);
                goto_Button.setVisibility(View.VISIBLE);
                GIUtil.closeSoftInput(activity);
                if (GIStringUtil.isNotBlank(etSearch.getText().toString())) {
                    etSearch.setText("");
                }
                break;
        }
    }

    @Override
    public void setListener() {
        super.setListener();
        etSearch.getmOptionSearch(true).setListener(new OptionSearch.IFinishListener() {
            @Override
            public void getKeyword(String keyword) {
                strKeyValue = keyword;
                if (!GIStringUtil.isNotBlank(strKeyValue)) {
                    GIUtil.closeSoftInput(activity);
                }
                getJoinSupportList(1, false);
            }
        });
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        Utils.initEasyRecyclerView(activity, recyclerView, true, false, R.color.line_bg_white, 0.5f, 0);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getJoinSupportList(1, false);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getJoinSupportList(curPage + 1, false);
            }
        });
        adapter = new Proposal_JoinSupport_RVAdapter(this, strType);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 获取列表
     */
    private void getJoinSupportList(int curPage, boolean b) {
        this.curPage = curPage;
        if (b) {
            prgDialog.showLoadDialog();
        }
        textParamMap = new HashMap<>();
        textParamMap.put("intPageSize", String.valueOf(20));
        textParamMap.put("intCurPage", curPage + "");
        textParamMap.put("strKeyValue", strKeyValue);
        textParamMap.put("strType", strType);
        textParamMap.put("strId", strId);
        doRequestNormal(ApiManager.getInstance().getAllyListByIdServlet(textParamMap), 0);
    }

    /**
     * 联名确认/附议确认
     */
    private void doReportReply(String strId, int intAttend) {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strId); // 联名id
        textParamMap.put("intAttend", String.valueOf(intAttend)); // 1同意附议，0：不同意

        doRequestNormal(ApiManager.getInstance().dealAllySureServlet(textParamMap), 1);
    }

    private void doLogic(Object object, int what) {
        prgDialog.closePrgDialog();
        String toastMessage = null;
        try {
            switch (what) {
                case 1:
                    prgDialog.closePrgDialog();
                    BaseGlobal baseGlobal = (BaseGlobal) object;
                    toastMessage = getString(R.string.string_second_confirm_operation_successful);
                    getJoinSupportList(1, false);
                    break;
                case 0:
                    JoinSupportListBean meetSpeakListBean = (JoinSupportListBean) object;
                    if (curPage == 1) {
                        adapter.setList(meetSpeakListBean.getObjList());
                    } else {
                        adapter.addData(meetSpeakListBean.getObjList());
                    }
                    Utils.setLoadMoreViewState(meetSpeakListBean.getIntAllCount(), curPage * 20, strKeyValue, refreshLayout, adapter);
                    adapter.notifyDataSetChanged();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            toastMessage = getResources().getString(R.string.data_error);
        }
        if (toastMessage != null) {
            showToast(toastMessage);
        }
    }

    /**
     * 子任务详情对话框
     */
    private SimpleCustomPop mQuickCustomPopup;

    public void showMyDialog(String strReplyContent) {
        if (GIStringUtil.isBlank(strReplyContent)){
            return;
        }
        if (mQuickCustomPopup == null || !mQuickCustomPopup.isShowing()) {
            mQuickCustomPopup = new SimpleCustomPop(activity, strReplyContent);
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
        private TextView content_TextView;

        private String strReplyContent;

        public SimpleCustomPop(Context context, String strReplyContent) {
            super(context);
            this.strReplyContent = strReplyContent;
            setCanceledOnTouchOutside(false);
        }

        @Override
        public View onCreatePopupView() {
            View view = View.inflate(activity, R.layout.dialog_zxta_detail_transact, null);
            close_TextView = view.findViewById(R.id.tv_close);
            content_TextView = view.findViewById(R.id.tv_content);
            return view;
        }

        @Override
        public void setUiBeforShow() {
            content_TextView.setText(GIUtil.showHtmlInfo(strReplyContent));
            close_TextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }
    }

    /**
     * 确认签收对话框
     */
    public void showMyDialog(String name, String strId) {
        BaseAnimatorSet mBasIn = new BounceTopEnter();
        BaseAnimatorSet mBasOut = new BounceTopEnter();
        try {
            mBasIn = BounceEnter.class.newInstance();
            mBasOut = ZoomOutExit.class.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final MaterialDialog dialog = new MaterialDialog(activity);
        dialog.titleTextSize(20f);
        dialog.title(getString(R.string.string_second_confirm_tips)).content(getString(R.string.string_agree_or_not) + name + getString(R.string.string_second_application)).btnNum(3).btnText(getString(R.string.string_cancle), getString(R.string.string_agree), getString(R.string.string_refuse)).showAnim(mBasIn).dismissAnim(mBasOut).show();
        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                }, new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.superDismiss();
                        doReportReply(strId, 1);
                    }
                }, new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        doReportReply(strId, 0);
                    }
                }
        );
    }
}
