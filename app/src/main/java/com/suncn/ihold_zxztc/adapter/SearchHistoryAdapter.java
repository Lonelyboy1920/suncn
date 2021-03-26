package com.suncn.ihold_zxztc.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.global.OverSearchListActivity;

import org.jetbrains.annotations.NotNull;

/**
 * @author whh on 2018-06-18.
 * 查询历史
 */
public class SearchHistoryAdapter extends BaseQuickAdapter<String, SearchHistoryAdapter.ViewHolder> {
    private Context context;
    private int intType;//0:热点全局;1消息提醒;2通知公告

    public SearchHistoryAdapter(Activity context, int intType) {
        super(R.layout.item_rv_oversearch_history);
        this.context = context;
        this.intType = intType;
    }

    @Override
    protected void convert(@NotNull ViewHolder viewHolder, String objInfo) {
        viewHolder.name_TextView.setText(objInfo);
        viewHolder.clear_ImageView.setVisibility(View.VISIBLE);
        viewHolder.clear_ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OverSearchListActivity) context).setSettingData(objInfo, 1);
            }
        });
    }


    public class ViewHolder extends BaseViewHolder {
        private TextView info_TextView;
        private TextView name_TextView;
        private TextView clear_ImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            clear_ImageView = (TextView) itemView.findViewById(R.id.iv_clear);
            name_TextView = (TextView) itemView.findViewById(R.id.tv_name);
            info_TextView = (TextView) itemView.findViewById(R.id.tv_info);

        }

    }
}
