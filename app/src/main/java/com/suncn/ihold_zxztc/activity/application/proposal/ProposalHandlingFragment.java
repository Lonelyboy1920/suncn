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

import androidx.recyclerview.widget.RecyclerView;

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
import com.suncn.ihold_zxztc.adapter.ProposalHandlingRvAdapter;
import com.suncn.ihold_zxztc.adapter.Proposal_Detail_Transact_RVAdapter;
import com.suncn.ihold_zxztc.bean.ObjTransactBean;
import com.suncn.ihold_zxztc.bean.ProposalViewBean;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.List;

/**
 * @author :Sea
 * Date :2020-6-10 16:01
 * PackageName:com.suncn.ihold_zxztc.activity.application.proposal
 * Desc: 提案 办理情况
 */
public class ProposalHandlingFragment extends BaseFragment implements BaseInterface {
    @BindView(id = R.id.rl_head)
    private RelativeLayout head_Layout;
    @BindView(id = R.id.recyclerView)
    private RecyclerView recyclerView;//办理情况EasyRecyclerView
    private static List<ObjTransactBean> transactBeens; // 办理情况集合
    private ProposalHandlingRvAdapter adapter;

    public static ProposalHandlingFragment newInstance(List<ObjTransactBean> mTransactBeens) {
        ProposalHandlingFragment baseInfoFragment = new ProposalHandlingFragment();
        transactBeens = mTransactBeens;
        return baseInfoFragment;
    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.fragment_proposal_handling, null);
    }

    @Override
    public void initData() {
        super.initData();
        head_Layout.setVisibility(View.GONE);
//        recyclerView.setBackgroundColor(activity.getResources().getColor(R.color.main_bg));
        Utils.initEasyRecyclerView(activity, recyclerView, false, false, R.color.line_bg_white, 0.5f, 0);
        adapter = new ProposalHandlingRvAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setList(transactBeens);
        adapter.setEmptyView(R.layout.view_erv_empty);
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

    class SimpleCustomPop extends BasePopup<Proposal_Detail_TransactFragment.SimpleCustomPop> {
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

