package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.gavin.giframe.utils.GIImageUtil;
import com.suncn.ihold_zxztc.R;
import java.io.File;
import java.util.ArrayList;


public class Photo_RVAdapter extends RecyclerView.Adapter<Photo_RVAdapter.ViewHolder> {
    private int selectMax = 3;
    private final int TYPE_CAMERA = 1;
    private final int TYPE_PICTURE = 2;

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<File> list = new ArrayList<>();

    //点击添加图片跳转
    private onAddPicListener mOnAddPicListener;

    public interface onAddPicListener {
        void onAddPicClick(int type, int position);
    }

    //点击图片放大
    private onPicClickListener mOnPicClickListener;

    public interface onPicClickListener {
        void onPicClick(View view, int position);
    }

    public Photo_RVAdapter(Context context, onAddPicListener mOnAddPicListener, onPicClickListener mOnPicClickListener) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mOnAddPicListener = mOnAddPicListener;
        this.mOnPicClickListener = mOnPicClickListener;
    }

    public void setSelectMax(int selectMax) {
        this.selectMax = selectMax;
    }

    public ArrayList<File> getList() {
        return list;
    }

    public void setList(ArrayList<File> list) {
        this.list = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mPhoto_image;
        TextView delete_TextView;

        public ViewHolder(View view) {
            super(view);
            mPhoto_image = (ImageView) view.findViewById(R.id.photo_image);
            delete_TextView = view.findViewById(R.id.tv_delete);
        }
    }

    @Override
    public int getItemCount() {
        if (list.size() < selectMax) {
            return list.size() + 1;
        } else {
            return list.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowAddItem(position)) {
            return TYPE_CAMERA;
        } else {
            return TYPE_PICTURE;
        }
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_rv_photo, viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    private boolean isShowAddItem(int position) {
        int size = list.size() == 0 ? 0 : list.size();
        return position == size;
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        if (getItemViewType(position) == TYPE_CAMERA) {
            viewHolder.mPhoto_image.setImageResource(R.mipmap.icon_add_white);
            viewHolder.mPhoto_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnAddPicListener.onAddPicClick(0, viewHolder.getAdapterPosition());
                }
            });
            viewHolder.delete_TextView.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.delete_TextView.setVisibility(View.VISIBLE);
            viewHolder.mPhoto_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnPicClickListener.onPicClick(view, viewHolder.getAdapterPosition());
                }
            });
            viewHolder.delete_TextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnAddPicListener.onAddPicClick(1, viewHolder.getAdapterPosition());
                }
            });
            GIImageUtil.loadImg(mContext, viewHolder.mPhoto_image, list.get(position).getPath(), 0);
//            Glide.with(mContext).load(list.get(position).getCompressPath()).into(viewHolder.mPhoto_image);
        }
    }
}

