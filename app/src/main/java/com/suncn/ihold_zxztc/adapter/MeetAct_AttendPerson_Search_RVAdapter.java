package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.widget.RoundImageView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.ObjAttendMemBean;
import com.suncn.ihold_zxztc.utils.Utils;

import org.jetbrains.annotations.NotNull;

/**
 * 参会人员（出席情况）搜索
 */
public class MeetAct_AttendPerson_Search_RVAdapter extends BaseQuickAdapter<ObjAttendMemBean, MeetAct_AttendPerson_Search_RVAdapter.ViewHolder> {
    private Context context;

    public MeetAct_AttendPerson_Search_RVAdapter(Context context) {
        super(R.layout.item_exlv_meetact_attendperson_child);
        this.context = context;
    }

    @Override
    protected void convert(@NotNull ViewHolder viewHolder, ObjAttendMemBean objInfo) {
        GIImageUtil.loadImg(context, viewHolder.header_ImageView, Utils.formatFileUrl(context, objInfo.getStrPathUrl()), 1);
        viewHolder.name_TextView.setText(objInfo.getStrName());
        final int intAttend = objInfo.getIntAttend();
        if (intAttend == 0) { // 0--请假
            viewHolder.attend_TextView.setTextColor(context.getResources().getColor(R.color.view_leave));
            viewHolder.attend_TextView.setText(context.getString(R.string.string_leave));
        } else if (intAttend == 1) { // 1--出席
            viewHolder.attend_TextView.setTextColor(context.getResources().getColor(R.color.font_title));
            viewHolder.attend_TextView.setText(context.getString(R.string.string_attend));
        } else { // 2--缺席
            viewHolder.attend_TextView.setTextColor(context.getResources().getColor(R.color.zxta_state_red));
            viewHolder.attend_TextView.setText(context.getString(R.string.string_absence));
        }
        int intModify = objInfo.getIntModify();
        if (intModify == 1) {
            viewHolder.arrow_TextView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.arrow_TextView.setVisibility(View.GONE);
        }
    }


    public class ViewHolder extends BaseViewHolder {
        private RoundImageView header_ImageView;
        private TextView name_TextView;
        private TextView attend_TextView;
        private TextView arrow_TextView;

        public ViewHolder(View itemView) {
            super(itemView);
            header_ImageView = (RoundImageView) itemView.findViewById(R.id.iv_head);
            name_TextView = (TextView) itemView.findViewById(R.id.tv_name);
            attend_TextView = (TextView) itemView.findViewById(R.id.tv_attend);
            arrow_TextView = (TextView) itemView.findViewById(R.id.tv_arrow);
        }
    }
}
