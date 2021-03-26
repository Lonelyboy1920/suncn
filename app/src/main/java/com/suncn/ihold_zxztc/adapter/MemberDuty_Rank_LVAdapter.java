package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.MemRecordScoreBean.ScoreRankListBean.RankListBean;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 履职得分项目adapter
 */
public class MemberDuty_Rank_LVAdapter extends BaseQuickAdapter<RankListBean, MemberDuty_Rank_LVAdapter.ViewHolder> {
    private List<RankListBean> objList;
    private Context context;
    private int sign;

    public MemberDuty_Rank_LVAdapter(Context context, List<RankListBean> radarIndicatorList, int sign) {
        super(R.layout.item_rv_memberduty_rank);
        this.context = context;
        this.objList = radarIndicatorList;
        this.sign = sign;
    }

    public MemberDuty_Rank_LVAdapter(Context context, int sign) {
        super(R.layout.item_rv_memberduty_rank);
        this.context = context;
        this.sign = sign;
//        if (viewType == 0 && sign == 0) {
//            v.setBackgroundColor(Color.parseColor("#ebf6ff"));
//        } else {
//            v.setBackgroundColor(Color.parseColor("#ffffff"));
//        }
    }

    public void setObjList(List<RankListBean> objList) {
        this.objList = objList;
    }

    public List<RankListBean> getObjList() {
        return objList;
    }


    @Override
    protected void convert(@NotNull ViewHolder baseViewHolder, RankListBean objInfo) {
        baseViewHolder.name_TextView.setText(objInfo.getStrName());
        baseViewHolder.score_TextView.setText(objInfo.getDbAllScore() + "");
        int rank = objInfo.getIntScoreRank();
        if (rank == 0) {
            baseViewHolder.rank_TextView.setText("--");
        } else {
            baseViewHolder.rank_TextView.setText(rank + "");
        }
        baseViewHolder. icon_ImageView.setVisibility(View.VISIBLE);
        if (rank == 1) {
            baseViewHolder.icon_ImageView.setImageResource(R.mipmap.icon_rank1);
        } else if (rank == 2) {
            baseViewHolder. icon_ImageView.setImageResource(R.mipmap.icon_rank2);
        } else if (rank == 3) {
            baseViewHolder. icon_ImageView.setImageResource(R.mipmap.icon_rank3);
        } else {
            baseViewHolder.icon_ImageView.setVisibility(View.INVISIBLE);
        }
    }


    public class ViewHolder extends BaseViewHolder {
        private ImageView icon_ImageView;
        private TextView name_TextView;
        private TextView score_TextView;
        private TextView rank_TextView;

        public ViewHolder(View view) {
            super(view);
            icon_ImageView = (ImageView) view.findViewById(R.id.iv_icon);
            name_TextView = (TextView) view.findViewById(R.id.tv_name);
            score_TextView = (TextView) view.findViewById(R.id.tv_score);
            rank_TextView = (TextView) view.findViewById(R.id.tv_rank);

        }

    }

}
