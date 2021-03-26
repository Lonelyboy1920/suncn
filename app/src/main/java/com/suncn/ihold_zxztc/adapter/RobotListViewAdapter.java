package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.widget.ListViewForScrollView;
import com.gavin.giframe.widget.GITextView;
import com.gavin.giframe.widget.RoundImageView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.global.WebViewActivity;
import com.suncn.ihold_zxztc.activity.hot.NewsDetailActivity;
import com.suncn.ihold_zxztc.activity.hot.RobotActivity;
import com.suncn.ihold_zxztc.bean.BroadcastListBean;
import com.suncn.ihold_zxztc.bean.RobotBean;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.ArrayList;

public class RobotListViewAdapter extends BaseAdapter {
    private ArrayList<RobotBean> robotBeans = new ArrayList<>();
    private Context context;
    private ArrayList<BroadcastListBean.NewsInfo> newsInfos;
    private SeekBar seekBar;
    private int curPosition;


    public RobotListViewAdapter(Context context) {
        this.context = context;
    }

    public ArrayList<RobotBean> getRobotBeans() {
        return robotBeans;
    }

    public void setRobotBeans(ArrayList<RobotBean> robotBeans) {
        this.robotBeans = robotBeans;
    }

    public void setPosition(int position) {
        this.curPosition = position;
    }

    @Override
    public int getCount() {
        if (robotBeans == null) {
            return 0;
        } else {
            return robotBeans.size();
        }
    }

    public SeekBar getSeekBar() {
        return seekBar;
    }

    public void setNewsInfos(ArrayList<BroadcastListBean.NewsInfo> newsInfos) {
        this.newsInfos = newsInfos;
    }

    @Override
    public Object getItem(int position) {
        return robotBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_robot, null);
            holder.left_Layout = convertView.findViewById(R.id.ll_left);
            holder.right_Layout = convertView.findViewById(R.id.ll_right);
            holder.leftContent_TextView = convertView.findViewById(R.id.tv_content_left);
            holder.rightContent_TextView = convertView.findViewById(R.id.tv_content_right);
            holder.iv_head = convertView.findViewById(R.id.iv_head);
            holder.ll_play = convertView.findViewById(R.id.ll_play);
            holder.seekBar = convertView.findViewById(R.id.seekBar);
            holder.tv_play = convertView.findViewById(R.id.tv_play);
            holder.tv_turn_text = convertView.findViewById(R.id.tv_turn_text);
            holder.ll_list = convertView.findViewById(R.id.ll_list);
            holder.ll_leader = convertView.findViewById(R.id.ll_leader);
            holder.listview_leader = convertView.findViewById(R.id.listview_leader);
            holder.tv_more = convertView.findViewById(R.id.tv_more);
            holder.ll_maybe = convertView.findViewById(R.id.ll_maybe);
            holder.ll_list_maybe = convertView.findViewById(R.id.ll_list_maybe);
            holder.tv_place = convertView.findViewById(R.id.tv_place);
            holder.ll_content = convertView.findViewById(R.id.ll_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == robotBeans.size() - 1) {//为了使话筒按钮不挡着内容
            holder.tv_place.setVisibility(View.VISIBLE);
        } else {
            holder.tv_place.setVisibility(View.GONE);
        }
        holder.seekBar.setEnabled(false);
        String photoUrl = GISharedPreUtil.getString(context, "strPhotoUrl");
        GIImageUtil.loadImg(context, holder.iv_head, Utils.formatFileUrl(context, photoUrl), 1);
        RobotBean data = robotBeans.get(position);

        //列表第一个默认显示猜你想问
        if (position == 0) {
            holder.ll_leader.setVisibility(View.VISIBLE);
            holder.listview_leader.setAdapter(((RobotActivity) context).getSimpleTextAdapter());
        } else {
            holder.ll_leader.setVisibility(View.GONE);
        }

        holder.listview_leader.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("strUrl", ((RobotActivity) context).getLeaderList().get(position).getStrUrl());
                bundle.putBoolean("isPersonInfo", true);
                intent.putExtras(bundle);
                intent.setClass(context, WebViewActivity.class);
                context.startActivity(intent);
            }
        });
        holder.tv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RobotActivity) context).getLeaderListData();
            }
        });
        switch (data.getType()) {
            case 0:
                holder.ll_play.setVisibility(View.GONE);
                holder.ll_list.setVisibility(View.GONE);
                holder.left_Layout.setVisibility(View.VISIBLE);
                holder.right_Layout.setVisibility(View.GONE);
                holder.leftContent_TextView.setVisibility(View.VISIBLE);
                holder.leftContent_TextView.setText(data.getContent());
                break;
            case 1:
            case 2:
                holder.ll_play.setVisibility(View.GONE);
                holder.ll_list.setVisibility(View.GONE);
                holder.left_Layout.setVisibility(View.GONE);
                holder.right_Layout.setVisibility(View.VISIBLE);
                holder.rightContent_TextView.setText(data.getContent());
                break;
            case 3:
                holder.left_Layout.setVisibility(View.VISIBLE);
                holder.right_Layout.setVisibility(View.GONE);
                holder.ll_play.setVisibility(View.VISIBLE);
                holder.ll_maybe.setVisibility(View.GONE);
                holder.leftContent_TextView.setVisibility(View.GONE);
                seekBar = holder.seekBar;
                holder.tv_play.setId(position);
                holder.tv_play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((RobotActivity) context).isPlayNews = true;
                        if (holder.tv_play.getText().toString().equals(context.getString(R.string.font_robot_play))) {
                            holder.tv_play.setText(R.string.font_robot_pause);
                            curPosition = v.getId();
                            //如果已经播放完毕，那就重头播放，否则继续播放
                            if (holder.seekBar.getProgress() == 0) {
                                ((RobotActivity) context).speakText(true, data.getContent());
                            } else {
                                ((RobotActivity) context).mTts.resumeSpeaking();
                            }
                        } else {
                            holder.tv_play.setText(R.string.font_robot_play);
                            ((RobotActivity) context).mTts.pauseSpeaking();
                        }
                    }
                });
                //动态加载新闻的文字
                holder.ll_list.removeAllViews();
                for (BroadcastListBean.NewsInfo newsInfo : newsInfos) {
                    LinearLayout ly = (LinearLayout) LayoutInflater.from(context).inflate(
                            R.layout.item_simple_text, null, false);
                    TextView textView = ly.findViewById(R.id.tv_text);
                    textView.setText(newsInfo.getStrNewsTitle());
                    ly.findViewById(R.id.tv_icon).setVisibility(View.VISIBLE);
                    ((TextView) ly.findViewById(R.id.tv_icon)).setText(context.getString(R.string.string_information) + (newsInfos.indexOf(newsInfo) + 1));
                    ly.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putString("strUrl", newsInfo.getStrUrl());
                            bundle.putString("strNewsId", newsInfo.getStrId());
                            bundle.putBoolean("isNews", true);
                            bundle.putBoolean("isShowComment", false);
                            Intent intent = new Intent(context, NewsDetailActivity.class);
                            intent.putExtras(bundle);
                            context.startActivity(intent);
                        }
                    });
                    holder.ll_list.addView(ly);
                }

                holder.tv_turn_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.ll_list.setVisibility(View.VISIBLE);
                    }
                });

                break;
            case 4:
                holder.ll_play.setVisibility(View.GONE);
                holder.ll_list.setVisibility(View.GONE);
                holder.left_Layout.setVisibility(View.VISIBLE);
                holder.right_Layout.setVisibility(View.GONE);
                holder.leftContent_TextView.setVisibility(View.GONE);
                holder.leftContent_TextView.setText(data.getContent());
                holder.ll_maybe.setVisibility(View.VISIBLE);
                holder.ll_list_maybe.removeAllViews();
                if (data.getList() != null && data.getList().size() > 0) {
                    for (BroadcastListBean.NewsInfo newsInfo : data.getList()) {
                        LinearLayout ly = (LinearLayout) LayoutInflater.from(context).inflate(
                                R.layout.item_simple_text, null, false);
                        TextView textView = ly.findViewById(R.id.tv_text);
                        textView.setText(newsInfo.getStrTitle());
                        ly.findViewById(R.id.tv_icon).setVisibility(View.VISIBLE);
                        ((TextView) ly.findViewById(R.id.tv_icon)).setText(context.getString(R.string.string_problem) + (data.getList().indexOf(newsInfo) + 1));
                        ly.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((RobotActivity) context).setRequestData(newsInfo.getStrTitle());
                            }
                        });
                        holder.ll_list_maybe.addView(ly);
                    }
                } else {
                    holder.ll_maybe.setVisibility(View.GONE);
                }
                break;
        }

        return convertView;
    }

    private class ViewHolder {
        private LinearLayout left_Layout;
        private RelativeLayout right_Layout;
        private TextView leftContent_TextView;
        private TextView rightContent_TextView;
        private LinearLayout ll_play;
        private RoundImageView iv_head;
        private SeekBar seekBar;
        private GITextView tv_play;
        private ListViewForScrollView listview_news;
        private TextView tv_turn_text;
        private LinearLayout ll_list;
        private LinearLayout ll_leader;
        private ListViewForScrollView listview_leader;
        private TextView tv_more;
        private LinearLayout ll_maybe;
        private ListViewForScrollView listview_maybe;
        private LinearLayout ll_list_maybe;
        private TextView tv_place;
        private LinearLayout ll_content;
    }
}
