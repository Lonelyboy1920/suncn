package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.view.dialog.Login_SettingDialog;

import java.util.ArrayList;

/**
 * @author daiyy on 2016-10-27.
 */
public class LoginSettingListviewAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> serveList;
    private ArrayList<String> portList;
    private Login_SettingDialog login_settingDialog;

    public LoginSettingListviewAdapter(Context context, ArrayList<String> serveList, ArrayList<String> portList, Login_SettingDialog login_settingDialog) {
        this.context = context;
        this.serveList = serveList;
        this.portList = portList;
        this.login_settingDialog = login_settingDialog;
    }

    @Override
    public int getCount() {
        if (serveList != null)
            return serveList.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return serveList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.view_lv_item_login_setting, null);
            viewHolder.clear_ImageView = (ImageView) convertView.findViewById(R.id.iv_clear);
            viewHolder.name_TextView = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.clear_ImageView.setId(position);
        String server = serveList.get(position);
        String port = portList.get(position);

        if (port.contains("/")) {
            if (port.indexOf("/") != 0) {
                port = ":" + port;
            }
        } else {
            port = "/" + port;
        }
        viewHolder.name_TextView.setText(server + port);

        if (server.equals(context.getResources().getString(R.string.default_server))) {
            viewHolder.clear_ImageView.setVisibility(View.GONE);
        } else {
            viewHolder.clear_ImageView.setVisibility(View.VISIBLE);
        }
        viewHolder.clear_ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = v.getId();
                login_settingDialog.deleteHistory(pos);
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        private TextView name_TextView;
        private ImageView clear_ImageView;
    }
}
