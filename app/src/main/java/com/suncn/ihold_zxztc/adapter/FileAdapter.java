package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gavin.giframe.utils.GIMyIntent;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.ObjFileBean;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FileAdapter extends BaseAdapter {
    private ArrayList<ObjFileBean> objFiles;
    private Context context;
    private boolean IsShowImg = false;//是否显示图片和附件大小信息
    private boolean IsCanEdit = false;//是否可以编辑
    private Set<String> deletaFileId = new HashSet<>();
    private boolean isLocal = false;

    public FileAdapter(Context context, ArrayList<ObjFileBean> objFiles) {
        this.objFiles = objFiles;
        this.context = context;
    }


    @Override
    public int getCount() {
        if (objFiles != null) {
            return objFiles.size();
        }
        return 0;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public ArrayList<ObjFileBean> getObjFiles() {
        return objFiles;
    }

    public void setObjFiles(ArrayList<ObjFileBean> objFiles) {
        this.objFiles = objFiles;
    }

    public void setShowImg(boolean showImg) {
        IsShowImg = showImg;
    }

    public void setCanEdit(boolean canEdit) {
        IsCanEdit = canEdit;
    }

    public Set<String> getDeletaFileId() {
        return deletaFileId;
    }

    @Override
    public Object getItem(int position) {
        return objFiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_file_simple, null);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_icon = (TextView) convertView.findViewById(R.id.tv_icon);
            viewHolder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_name.setText(objFiles.get(position).getStrFile_name());
        if (objFiles.get(position).getStrFile_size() == null) {
            viewHolder.tv_size.setVisibility(View.GONE);
        } else {
            viewHolder.tv_size.setVisibility(View.VISIBLE);
            viewHolder.tv_size.setText(objFiles.get(position).getStrFile_size());
        }

        String fileType = objFiles.get(position).getStrFile_name().substring(objFiles.get(position).getStrFile_name().lastIndexOf(".") + 1).toLowerCase();
        if (fileType.contains("png") || fileType.contains("jpg")) {
            viewHolder.tv_icon.setText(context.getResources().getString(R.string.font_img));
        } else if (fileType.contains("doc")) {
            viewHolder.tv_icon.setText(context.getResources().getString(R.string.font_doc));
        } else if (fileType.contains("xls")) {
            viewHolder.tv_icon.setText(context.getResources().getString(R.string.font_xlsx));
        } else if (fileType.contains("pdf")) {
            viewHolder.tv_icon.setText(context.getResources().getString(R.string.font_pdf));
        } else if (fileType.contains("ppt")) {
            viewHolder.tv_icon.setText(context.getResources().getString(R.string.font_ppt));
        } else if (fileType.contains("zip") || fileType.contains("rar")) {
            viewHolder.tv_icon.setText(context.getResources().getString(R.string.font_zip));
        } else if (fileType.contains("txt")) {
            viewHolder.tv_icon.setText(context.getResources().getString(R.string.font_txt));
        } else if (fileType.contains("xml")) {
            viewHolder.tv_icon.setText(context.getResources().getString(R.string.font_xml));
        } else {
            viewHolder.tv_icon.setText(context.getResources().getString(R.string.font_file));
        }


        return convertView;
    }

    private class ViewHolder {
        private TextView tv_name;
        private TextView tv_size;
        private TextView tv_icon;
        private TextView tv_delete;
    }
}
