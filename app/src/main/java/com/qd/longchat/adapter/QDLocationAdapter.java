package com.qd.longchat.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qd.longchat.R;
import com.qd.longchat.model.QDLocationBean;

import java.util.List;


/**
 * author Gao
 * date 2017/7/4 0004
 * description
 */

public class QDLocationAdapter extends RecyclerView.Adapter<QDLocationAdapter.ViewHolder>{

    private List<QDLocationBean> list;
    private int selectPosition=0;

    public QDLocationAdapter(List<QDLocationBean> list){
        this.list=list;
    }

    public void setSelectPosition(int selectPosition){
        this.selectPosition=selectPosition;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.yx_item_location_detail,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position==selectPosition){
            holder.selectFlag.setVisibility(View.VISIBLE);
        }else {
            holder.selectFlag.setVisibility(View.GONE);
        }
        holder.sampleText.setText(list.get(position).getSampleLocationInfo());
        holder.detailText.setText(list.get(position).getDetailLocationInfo());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView sampleText,detailText;
        private RelativeLayout rootView;
        private ImageView selectFlag;

        public ViewHolder(View itemView) {
            super(itemView);
            sampleText= (TextView) itemView.findViewById(R.id.item_location_sample_text);
            detailText= (TextView) itemView.findViewById(R.id.item_location_detail_text);
            selectFlag= (ImageView) itemView.findViewById(R.id.item_location_select);
            rootView= (RelativeLayout) itemView.findViewById(R.id.item_location_root);
            rootView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener!=null){
                onItemClickListener.onItemClick(getLayoutPosition());
            }
        }
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }


}
