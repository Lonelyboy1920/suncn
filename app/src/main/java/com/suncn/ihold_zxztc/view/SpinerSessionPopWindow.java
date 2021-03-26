package com.suncn.ihold_zxztc.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.SessionListBean;

import java.util.ArrayList;


public class SpinerSessionPopWindow<T> extends PopupWindow {
    private LayoutInflater inflater;
    private ListView mListView;
    private ArrayList<SessionListBean.SessionBean> list;
    private MyAdapter  mAdapter;

    public SpinerSessionPopWindow(Context context, ArrayList<SessionListBean.SessionBean> list, AdapterView.OnItemClickListener clickListener) {
        super(context);
        inflater=LayoutInflater.from(context);
        this.list=list;
        init(clickListener);
    }

    private void init(AdapterView.OnItemClickListener clickListener){
        View view = inflater.inflate(R.layout.popup_sessioncode_list, null);
        setContentView(view);
        setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
            ColorDrawable dw = new ColorDrawable(0x00);
        setBackgroundDrawable(dw);
        mListView = view.findViewById(R.id.listview);
        mListView.setAdapter(mAdapter=new MyAdapter());
        mListView.setOnItemClickListener(clickListener);
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position).getStrSessionName();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder=null;
            if(convertView==null){
                holder=new ViewHolder();
                convertView=inflater.inflate(R.layout.popup_lv_item_sessioncode, null);
                holder.tvName= convertView.findViewById(R.id.tv_name);
                convertView.setTag(holder);
            }else{
                holder=(ViewHolder) convertView.getTag();
            }
            holder.tvName.setText(getItem(position).toString());
            return convertView;
        }
    }

    private class ViewHolder{
        private TextView tvName;
    }
}