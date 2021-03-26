package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;
import com.gavin.giframe.widget.GITextView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.ArrayList;

/**
 * 会议活动详情的基本信息
 */
public class MeetAct_Detail_BaseInfo_LVAdapter extends BaseAdapter {
    private ArrayList<String> baseInfoList; // 基本信息集合
    private Context context;
    private int sign; // 标识

    public MeetAct_Detail_BaseInfo_LVAdapter(Context context, ArrayList<String> baseInfoList) {
        this.baseInfoList = baseInfoList;
        this.context = context;
    }

    public void setBaseInfoList(ArrayList<String> baseInfoList) {
        this.baseInfoList = baseInfoList;
    }

    public int getSign() {
        return sign;
    }

    public void setSign(int sign) {
        this.sign = sign;
    }

    public int getCount() {
        if (baseInfoList != null)
            return baseInfoList.size();
        return 0;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_rv_proposal_detail_baseinfo, parent, false);
            holder.tab_TextView = (TextView) convertView.findViewById(R.id.tv_tag);
            holder.value_TextView = (TextView) convertView.findViewById(R.id.tv_value);
            holder.arraw_TextView = convertView.findViewById(R.id.tv_arrow);
            holder.blockView = convertView.findViewById(R.id.block);
            holder.main_LinearLayout = convertView.findViewById(R.id.ll_main);
            holder.main_LinearLayout.setForeground(context.getDrawable(R.color.transparent));
            holder.webview = convertView.findViewById(R.id.webview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String value = baseInfoList.get(position);
        String[] valueArray = value.split("\\|");
        if (GIStringUtil.isBlank(valueArray[0])) {
            holder.tab_TextView.setVisibility(View.GONE);
        } else {
            holder.tab_TextView.setVisibility(View.VISIBLE);
            holder.tab_TextView.setText(valueArray[0]);
        }
        holder.arraw_TextView.setVisibility(View.GONE);
       /* if (valueArray.length > 1)
            holder.content_MarqueeTextView.setText(valueArray[1]);
        else {
            holder.content_MarqueeTextView.setText("");
        }*/
        if (valueArray.length > 1)
            holder.value_TextView.setText(GIUtil.showHtmlInfo(valueArray[1], holder.value_TextView));
        else {
            holder.value_TextView.setText("");
        }
        GILogUtil.e(valueArray[0]);
        if ("url".equals(valueArray[0])) {
            holder.tab_TextView.setText("");
            holder.value_TextView.setVisibility(View.GONE);
            holder.webview.setVisibility(View.VISIBLE);
            holder.webview.loadUrl(Utils.formatFileUrl(context,valueArray[1]));
        } else {
            holder.value_TextView.setVisibility(View.VISIBLE);
            holder.webview.setVisibility(View.GONE);
        }
        if (valueArray.length > 2 && valueArray[2] != null && valueArray[2].equals("true")) {
            holder.blockView.setVisibility(View.VISIBLE);
        } else {
            holder.blockView.setVisibility(View.GONE);
        }
        return convertView;
    }

    private static class ViewHolder {
        private TextView tab_TextView;
        private TextView value_TextView;
        private GITextView arraw_TextView;
        private View blockView;
        private LinearLayout main_LinearLayout;
        private WebView webview;
    }
}
