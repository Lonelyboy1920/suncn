package com.suncn.ihold_zxztc.activity.application.proposal;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideTopExit;
import com.flyco.dialog.widget.popup.base.BasePopup;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;
import com.suncn.ihold_zxztc.BaseInterface;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseFragment;
import com.suncn.ihold_zxztc.adapter.Proposal_Detail_Transact_RVAdapter;
import com.suncn.ihold_zxztc.bean.ObjTransactBean;
import com.suncn.ihold_zxztc.bean.ProposalViewBean;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 提案详情中的办理情况Fragment
 */
public final class Proposal_Detail_TransactFragment extends BaseFragment implements BaseInterface {
    @BindView(id = R.id.rl_head)
    private RelativeLayout head_Layout;
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;//办理情况EasyRecyclerView
    private static List<ObjTransactBean> transactBeens; // 办理情况集合
    private static ProposalViewBean.MemOpinions memOpinions;//委员意见反馈
    private Proposal_Detail_Transact_RVAdapter adapter;

    public static Proposal_Detail_TransactFragment newInstance(List<ObjTransactBean> mTransactBeens, ProposalViewBean.MemOpinions memOpinion) {
        Proposal_Detail_TransactFragment baseInfoFragment = new Proposal_Detail_TransactFragment();
        transactBeens = mTransactBeens;
        memOpinions = memOpinion;
        return baseInfoFragment;
    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.activity_recyclerview_nopadding, null);
    }

    @Override
    public void initData() {
        super.initData();
        head_Layout.setVisibility(View.GONE);
        recyclerView.setBackgroundColor(activity.getResources().getColor(R.color.main_bg));
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.line_bg_white, 0.5f, 0);
        adapter = new Proposal_Detail_Transact_RVAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setList(transactBeens);
        adapter.setEmptyView(R.layout.view_erv_empty);
        if (memOpinions != null)
            adapter.addFooterView(getFootView());
        // }
    }

    private View getFootView() {
        View view = getLayoutInflater().inflate(R.layout.view_proposal_detail_transact_feedback, recyclerView, false);
        LinearLayout zhOpinion_LinearLayout = view.findViewById(R.id.ll_zh_opinion);//综合意见
        TextView zhOpinion_TextView = view.findViewById(R.id.et_zh_opinion);
        LinearLayout tdOpinion_LinearLayout = view.findViewById(R.id.ll_td_opinion);//态度意见
        TextView tdOpinion_TextView = view.findViewById(R.id.tv_td_opinion);
        LinearLayout jgOpinion_LinearLayout = view.findViewById(R.id.ll_jg_opinion);//结果意见
        TextView jgOpinion_TextView = view.findViewById(R.id.tv_jg_opinion);
        LinearLayout jtOpinion_LinearLayout = view.findViewById(R.id.ll_jt_opinion);//具体意见布局
        TextView jtOpinion_TextView = view.findViewById(R.id.tv_jt_opinion);

        String strLastMemFeedBackState = GIStringUtil.nullToEmpty(memOpinions.getStrLastMemFeedBackState());
        if (GIStringUtil.isNotBlank(strLastMemFeedBackState)) {
            zhOpinion_LinearLayout.setVisibility(View.VISIBLE);
            zhOpinion_TextView.setText(memOpinions.getStrLastMemFeedBackState());
        } else {
            zhOpinion_LinearLayout.setVisibility(View.GONE);
        }

        String getStrMemFeedBackAttitudeIdea = GIStringUtil.nullToEmpty(memOpinions.getStrMemFeedBackAttitudeIdea());
        if (GIStringUtil.isNotBlank(getStrMemFeedBackAttitudeIdea)) {
            tdOpinion_LinearLayout.setVisibility(View.VISIBLE);
            tdOpinion_TextView.setText(memOpinions.getStrMemFeedBackAttitudeIdea());
        } else {
            tdOpinion_LinearLayout.setVisibility(View.GONE);
        }

        String getStrMemFeedBackMainIdea = GIStringUtil.nullToEmpty(memOpinions.getStrMemFeedBackMainIdea());
        if (GIStringUtil.isNotBlank(getStrMemFeedBackMainIdea)) {
            jtOpinion_LinearLayout.setVisibility(View.VISIBLE);
            jtOpinion_TextView.setText(memOpinions.getStrMemFeedBackMainIdea());
        } else {
            jtOpinion_LinearLayout.setVisibility(View.GONE);
        }

        String getStrMemFeedBackResultIdea = GIStringUtil.nullToEmpty(memOpinions.getStrMemFeedBackResultIdea());
        if (GIStringUtil.isNotBlank(getStrMemFeedBackResultIdea)) {
            jgOpinion_LinearLayout.setVisibility(View.VISIBLE);
            jgOpinion_TextView.setText(memOpinions.getStrMemFeedBackResultIdea());
        } else {
            jgOpinion_LinearLayout.setVisibility(View.GONE);
        }
        GISharedPreUtil.setValue(activity, "isLoad", false);
        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        GISharedPreUtil.setValue(activity, "isLoad", true);
    }

    /**
     * 子任务详情对话框
     */
    private SimpleCustomPop mQuickCustomPopup;

    public void showMyDialog(String strReplyContent) {
        if (mQuickCustomPopup == null || !mQuickCustomPopup.isShowing()) {
            mQuickCustomPopup = new SimpleCustomPop(activity, strReplyContent);
            mQuickCustomPopup
                    .alignCenter(true)
                    .anchorView(getActivity().findViewById(R.id.rl_head))
                    .gravity(Gravity.BOTTOM)
                    .showAnim(new BounceTopEnter())
                    .dismissAnim(new SlideTopExit())
                    .offset(0, 0)
                    .dimEnabled(true)
                    .show();
        }
    }

    @Override
    public void setOnClick(Object object, View view, int type) {


        if (object instanceof ObjTransactBean) {
            ObjTransactBean objTransactBean = (ObjTransactBean) object;
            switch (view.getId()) {
                case R.id.tv_reply:
                    showMyDialog(objTransactBean.getStrReplyContent());
                    break;
            }
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
}
