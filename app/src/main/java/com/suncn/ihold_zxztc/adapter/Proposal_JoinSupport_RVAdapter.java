package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.application.proposal.Proposal_JoinSupportListActivity;
import com.suncn.ihold_zxztc.bean.JoinSupportListBean;

import org.jetbrains.annotations.NotNull;

/**
 * 联名人、附议人列表
 */
public class Proposal_JoinSupport_RVAdapter extends BaseQuickAdapter<JoinSupportListBean.JoinSupportBean, Proposal_JoinSupport_RVAdapter.ViewHolder> {
    private Context context;
    private Proposal_JoinSupportListActivity activity;
    private String strType = "";

    public Proposal_JoinSupport_RVAdapter(Context context, String strType) {
        super(R.layout.item_rv_proposal_joinsupport);
        this.context = context;
        this.activity = (Proposal_JoinSupportListActivity) context;
        this.strType = strType;
    }

    @Override
    protected void convert(@NotNull ViewHolder baseViewHolder, JoinSupportListBean.JoinSupportBean objInfo) {
        baseViewHolder.title_TextView.setText(objInfo.getStrAllyUserName());
        baseViewHolder.date_TextView.setText(objInfo.getStrReportDate());
        String strState = objInfo.getStrAttendName();
        baseViewHolder.state_TextView.setText(strState);
        //0 不同意 弹出具体意见 当intAttend为2、并且strType为3附议时 附议确认按钮可以点击操作
        int intAttend = objInfo.getIntAttend();
        baseViewHolder.main_Layout.setTag(intAttend);
        if (0 == intAttend || (2 == intAttend && "3".equals(strType)) || "1".equals(objInfo.getStrShowReason())) {
            baseViewHolder.main_Layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (objInfo.getStrShowReason().equals("1")) {
                        activity.showMyDialog(objInfo.getStrReason());
                    } else {
                        if ((int) baseViewHolder.main_Layout.getTag() == 0) {
                            activity.showMyDialog(objInfo.getStrReason());
                        } else if ((int) baseViewHolder.main_Layout.getTag() == 2 && "3".equals(strType)) {
                            activity.showMyDialog(objInfo.getStrAllyUserName(), objInfo.getStrId());
                        }
                    }
                }
            });
        } else {
            baseViewHolder.main_Layout.setOnClickListener(null);
        }
        int textColor;
        switch (strState) {
            case "已附议":
            case "已联名":
                textColor = context.getResources().getColor(R.color.zxta_state_green);
                break;
            default:
                textColor = context.getResources().getColor(R.color.font_title);
                break;
        }
        baseViewHolder.state_TextView.setTextColor(textColor);
    }

    public class ViewHolder extends BaseViewHolder {
        private LinearLayout main_Layout;
        private TextView title_TextView;
        private TextView date_TextView;
        private TextView state_TextView;

        public ViewHolder(View itemView) {
            super(itemView);
            main_Layout = itemView.findViewById(R.id.ll_main);
            title_TextView = (TextView) itemView.findViewById(R.id.tv_title);
            date_TextView = (TextView) itemView.findViewById(R.id.tv_date);
            state_TextView = (TextView) itemView.findViewById(R.id.tv_state);
        }

    }
}
