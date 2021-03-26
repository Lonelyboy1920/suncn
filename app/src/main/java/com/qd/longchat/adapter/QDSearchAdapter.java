package com.qd.longchat.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gavin.giframe.utils.GIImageUtil;
import com.qd.longchat.R;
import com.qd.longchat.activity.QDSearchActivity;
import com.qd.longchat.holder.QDSearchHolder;
import com.qd.longchat.model.QDSearchInfo;
import com.qd.longchat.util.QDBitmapUtil;
import com.qd.longchat.util.QDDateUtil;
import com.qd.longchat.widget.QDChatSmiley;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/7/5 下午4:10
 */
public class QDSearchAdapter extends BaseAdapter {

    private Context context;
    private List<QDSearchInfo> searchInfoList;
    private OnMoreClickListener onMoreClickListener;

    public QDSearchAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<QDSearchInfo> searchInfoList) {
        this.searchInfoList = searchInfoList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (searchInfoList == null) {
            return 0;
        }
        return searchInfoList.size();
    }

    @Override
    public QDSearchInfo getItem(int position) {
        return searchInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        QDSearchHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_search, null);
            holder = new QDSearchHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (QDSearchHolder) convertView.getTag();
        }

        final QDSearchInfo searchInfo = searchInfoList.get(position);
        String title = searchInfo.getTitle();
        if (TextUtils.isEmpty(title)) {
            holder.tvItemTitle.setVisibility(View.GONE);
            holder.ivItemTileLine.setVisibility(View.GONE);
            holder.ivItemSpace.setVisibility(View.GONE);
        } else {
            holder.tvItemTitle.setVisibility(View.VISIBLE);
            holder.ivItemTileLine.setVisibility(View.VISIBLE);
            holder.tvItemTitle.setText(title);
            if (position != 0) {
                holder.ivItemSpace.setVisibility(View.VISIBLE);
            } else {
                holder.ivItemSpace.setVisibility(View.GONE);
            }
        }

        boolean isMore = searchInfo.isMore();
        if (isMore) {
            holder.llItemMoreLayout.setVisibility(View.VISIBLE);
            holder.tvItemMore.setText(searchInfo.getMoreText());
        } else {
            holder.llItemMoreLayout.setVisibility(View.GONE);
        }

        int type = searchInfo.getType();

        switch (type) {
            case QDSearchActivity.SEARCH_TYPE_GROUP_MEMBER:
            case QDSearchActivity.SEARCH_TYPE_MORE_USER:
            case QDSearchActivity.SEARCH_TYPE_USER:
                bindUserView(holder, searchInfo);
                break;
            case QDSearchActivity.SEARCH_TYPE_GROUP:
                bindGroupView(holder, searchInfo);
                break;
            case QDSearchActivity.SEARCH_TYPE_MORE_GROUP_MSG:
            case QDSearchActivity.SEARCH_TYPE_MORE_PERSONAL_MSG:
            case QDSearchActivity.SEARCH_TYPE_CONVERSATION:
                bindConversation(holder, searchInfo);
                break;
            case QDSearchActivity.SEARCH_TYPE_PERSONAL_HISTORY:
                bindUserView(holder, searchInfo);
                holder.tvItemTime.setVisibility(View.VISIBLE);
                break;
            case QDSearchActivity.SEARCH_TYPE_GROUP_HISTORY:
                bindGroupView(holder, searchInfo);
                holder.tvItemTime.setVisibility(View.VISIBLE);
                break;
            case QDSearchActivity.SEARCH_TYPE_REMOTE:
                bindUserView(holder, searchInfo);
                break;
        }

        holder.llItemMoreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onMoreClickListener != null) {
                    onMoreClickListener.onMoreClick(searchInfo.getItemType());
                }
            }
        });
        return convertView;
    }

    private void bindUserView(QDSearchHolder holder, QDSearchInfo searchInfo) {
        GIImageUtil.loadImg(context,holder.ivItemIcon, Utils.formatFileUrl(context,searchInfo.getIcon()),1);
        holder.tvItemName.setText(searchInfo.getName());
        String subName = searchInfo.getSubname();
        if (!TextUtils.isEmpty(subName)) {
            holder.tvItemSubname.setText(QDChatSmiley.getInstance(context).strToSmileyInfo(subName));
        } else {
            holder.tvItemSubname.setText("");
        }
        long time = searchInfo.getTime();
        holder.tvItemTime.setText(QDDateUtil.getConversationTime(time / 1000));
        holder.tvItemTime.setVisibility(View.GONE);
    }

    private void bindGroupView(QDSearchHolder holder, QDSearchInfo searchInfo) {
        Glide.with(context).load(searchInfo.getIcon()).apply(new RequestOptions().placeholder(R.mipmap.ic_download_loading).error(R.mipmap.im_recent_group_icon)).into(holder.ivItemIcon);
        holder.tvItemName.setText(searchInfo.getName());
        String subName = searchInfo.getSubname();
        if (!TextUtils.isEmpty(subName)) {
            holder.tvItemSubname.setText(QDChatSmiley.getInstance(context).strToSmileyInfo(subName));
        } else {
            holder.tvItemSubname.setText("");
        }
        long time = searchInfo.getTime();
        holder.tvItemTime.setText(QDDateUtil.getConversationTime(time / 1000));
        holder.tvItemTime.setVisibility(View.GONE);
    }

    private void bindConversation(QDSearchHolder holder, QDSearchInfo searchInfo) {
        int itemType = searchInfo.getItemType();
        if (itemType == QDSearchInfo.ITEM_TYPE_PERSON_CHAT || itemType == QDSearchInfo.ITEM_TYPE_USER) {
            bindUserView(holder, searchInfo);
        } else {
            bindGroupView(holder, searchInfo);
        }
        if (itemType == QDSearchInfo.ITEM_TYPE_USER || itemType == QDSearchInfo.ITEM_TYPE_GROUP) {
            holder.tvItemTime.setVisibility(View.GONE);
        } else {
            holder.tvItemTime.setVisibility(View.VISIBLE);
        }
    }

    public void setOnMoreClickListener(OnMoreClickListener onMoreClickListener) {
        this.onMoreClickListener = onMoreClickListener;
    }

    public interface OnMoreClickListener {
        void onMoreClick(int itemType);
    }
}
