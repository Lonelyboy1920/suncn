package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.ObjFileBean;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.ArrayList;

/**
 * 文件GridView的dapter
 */
public class File_GVAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ObjFileBean> fileList;
    private boolean isModify;
    private String deleteFileId = "";

    public File_GVAdapter(Context context, ArrayList<ObjFileBean> fileList) {
        this.context = context;
        this.fileList = fileList;
        // options = GIOptions.getSlidimgeOptions(R.mipmap.ic_news_default);
    }

    public File_GVAdapter(Context context, ArrayList<ObjFileBean> fileList, boolean isModify) {
        this.context = context;
        this.fileList = fileList;
        this.isModify = isModify;
    }

    public String getDeleteFileId() {
        return deleteFileId.replaceFirst(",", "");
    }

    public ArrayList<ObjFileBean> getFileList() {
        return fileList;
    }

    public void setFileList(ArrayList<ObjFileBean> fileList) {
        this.fileList = fileList;
    }

    @Override
    public int getCount() {
        if (fileList != null)
            return fileList.size();
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return fileList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int posiotion, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.view_gv_item_file, null);
            viewHolder = new ViewHolder();
            viewHolder.file_ImageView = (ImageView) view.findViewById(R.id.iv_file);
            viewHolder.video_ImageView = (ImageView) view.findViewById(R.id.iv_play);
            viewHolder.fileName_TextView = (TextView) view.findViewById(R.id.tv_fileName);
            viewHolder.delete_TextView = (TextView) view.findViewById(R.id.tv_delete);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.video_ImageView.setVisibility(View.GONE);
        viewHolder.fileName_TextView.setVisibility(View.GONE);
        if (isModify)
            viewHolder.delete_TextView.setVisibility(View.VISIBLE);
        else {
            viewHolder.delete_TextView.setVisibility(View.GONE);
        }
        ObjFileBean objFile = fileList.get(posiotion);
        String fileUrl = objFile.getStrFile_url();
        if (objFile.getStrFile_Type().equals("add") && GIStringUtil.isBlank(objFile.getStrFile_url())) {
            viewHolder.delete_TextView.setVisibility(View.GONE);
            viewHolder.file_ImageView.setImageResource(R.mipmap.icon_add);
        } else if (objFile.getStrFile_Type().contains("image/")) { // 图片
            if (GIStringUtil.isBlank(fileUrl))
                viewHolder.file_ImageView.setImageResource(R.mipmap.img_error);
            else if (fileUrl.contains("http://")) {
                GIImageUtil.loadImg(context, viewHolder.file_ImageView, Utils.formatFileUrl(context, objFile.getStrFile_url()), 0);
            } else {
                viewHolder.file_ImageView.setImageBitmap(objFile.getThumb());
            }
        } else if (objFile.getStrFile_Type().contains("video/")) { // 视频
            viewHolder.video_ImageView.setVisibility(View.VISIBLE);
            if (GIStringUtil.isBlank(fileUrl))
                viewHolder.file_ImageView.setImageResource(R.mipmap.img_error);
            else if (fileUrl.contains("http://")) {
                GIImageUtil.loadImg(context, viewHolder.file_ImageView, Utils.formatFileUrl(context, objFile.getStrFile_url()), 0);
            } else {
                viewHolder.file_ImageView.setImageBitmap(objFile.getThumb());
            }
        } else { // 文件
            viewHolder.file_ImageView.setImageResource(R.mipmap.img_file);
            viewHolder.fileName_TextView.setVisibility(View.VISIBLE);
            viewHolder.fileName_TextView.setText(objFile.getStrFile_name());
        }
        viewHolder.delete_TextView.setTag(posiotion);
        viewHolder.delete_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = (int) view.getTag();
                ObjFileBean objFile = fileList.get(pos);
                if (GIStringUtil.isNotBlank(objFile.getStrFile_id())) {
                    deleteFileId = deleteFileId + "," + objFile.getStrFile_id() + "$|$" + objFile.getStrFile_Type();
                }
                fileList.remove(pos);
                notifyDataSetChanged();
            }
        });
        return view;
    }

    private static class ViewHolder {
        private ImageView file_ImageView;
        private ImageView video_ImageView;
        private TextView fileName_TextView;
        private TextView delete_TextView;
    }
}
