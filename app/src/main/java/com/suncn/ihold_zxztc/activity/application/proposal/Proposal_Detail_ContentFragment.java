package com.suncn.ihold_zxztc.activity.application.proposal;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseFragment;

/**
 * 提案详情中的正文内容Fragment
 */
public final class Proposal_Detail_ContentFragment extends BaseFragment {
    @BindView(id = R.id.ll_reason)
    private LinearLayout reason_Layout;//情况分析LinearLayout
    @BindView(id = R.id.tv_reason_tag)
    private TextView reasonTag_TextView;//情况分析Tag  TextView
    @BindView(id = R.id.tv_reason)
    private TextView reason_TextView;//情况分析具内容TextView
    @BindView(id = R.id.ll_way)
    private LinearLayout way_Layout;//具体建议LinearLayout
    @BindView(id = R.id.tv_way_tag)
    private TextView wayTag_TextView;//具体建议Tag TextView
    @BindView(id = R.id.tv_way)
    private TextView way_TextView;//具体建议内容TextView
    @BindView(id = R.id.tv_empty)
    private ImageView empty_TextView;

    private static String reason; // 情况分析
    private static String way; // 具体建议
    private static String mStrReasonTitle; //提案内容的命名
    private static String mStrWayTitle;//解决方法的命名

    public static Proposal_Detail_ContentFragment newInstance(String mReason, String mWay, String strReasonTitle, String strWayTitle) {
        Proposal_Detail_ContentFragment baseInfoFragment = new Proposal_Detail_ContentFragment();
        reason = mReason;
        way = mWay;
        mStrReasonTitle = strReasonTitle;
        mStrWayTitle = strWayTitle;
        return baseInfoFragment;
    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.fragment_zxta_detail_content, null);
    }

    @Override
    public void initData() {
        super.initData();
//        reasonTag_TextView.setText(GISharedPreUtil.getString(activity, "strContentTitle1"));
//        wayTag_TextView.setText(GISharedPreUtil.getString(activity, "strContentTitle2"));
        reasonTag_TextView.setText(mStrReasonTitle);
        wayTag_TextView.setText(mStrWayTitle);

        if (GIStringUtil.isBlank(reason)) {
            reason_Layout.setVisibility(View.GONE);
            reason_TextView.setVisibility(View.GONE);
        } else {
            reason_Layout.setVisibility(View.VISIBLE);
            reason_TextView.setVisibility(View.VISIBLE);
            reason_TextView.setText(GIUtil.showHtmlInfo(reason));
        }

        if (GIStringUtil.isBlank(way)) {
            way_Layout.setVisibility(View.GONE);
            way_TextView.setVisibility(View.GONE);
        } else {
            way_Layout.setVisibility(View.VISIBLE);
            way_TextView.setVisibility(View.VISIBLE);
            way_TextView.setText(GIUtil.showHtmlInfo(way));
        }

        if (reason_Layout.getVisibility() == View.GONE && way_Layout.getVisibility() == View.GONE) {
            empty_TextView.setVisibility(View.VISIBLE);
        } else {
            empty_TextView.setVisibility(View.GONE);
        }
    }
}
