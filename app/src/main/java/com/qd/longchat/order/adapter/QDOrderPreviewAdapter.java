package com.qd.longchat.order.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.longchat.base.model.gd.QDOrderPreviewModel;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qd.longchat.R;
import com.qd.longchat.application.QDApplication;
import com.qd.longchat.order.holder.QDOrderPreviewHolder;
import com.qd.longchat.util.QDBitmapUtil;
import com.qd.longchat.util.QDUtil;

import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/9/27 上午10:47
 */
public class QDOrderPreviewAdapter extends BaseAdapter {

    private Context context;
    private List<QDOrderPreviewModel.Bean.ListBean> listBean;

    public QDOrderPreviewAdapter(Context context, List<QDOrderPreviewModel.Bean.ListBean> listBean) {
        this.context = context;
        this.listBean = listBean;
    }

    public void setListBean(List<QDOrderPreviewModel.Bean.ListBean> listBean) {
        this.listBean = listBean;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listBean.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        QDOrderPreviewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_order_preview_detail, null);
            holder = new QDOrderPreviewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (QDOrderPreviewHolder) convertView.getTag();
        }
        QDOrderPreviewModel.Bean.ListBean bean = listBean.get(position);
        holder.tvItemName.setText(bean.getEmpName());
        String avatar = bean.getAvatar();
        if (TextUtils.isEmpty(avatar)) {
            Bitmap bitmap = QDBitmapUtil.getInstance().createNameAvatar(context, bean.getEmpId(), bean.getEmpName());
            holder.ivItemIcon.setImageBitmap(bitmap);
        } else {
            ImageLoader.getInstance().displayImage(QDUtil.replaceWebServerAndToken(avatar), holder.ivItemIcon, QDApplication.options);
        }
        return convertView;
    }
}
