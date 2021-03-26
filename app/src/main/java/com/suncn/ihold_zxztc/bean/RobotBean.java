package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

public class RobotBean {
    private int type; // 0-机器人；1-用户
    private String content; // 聊天内容
    private boolean isPlay;
    ArrayList<BroadcastListBean.NewsInfo> list;//您是否想问的数据集合

    public ArrayList<BroadcastListBean.NewsInfo> getList() {
        return list;
    }

    public void setList(ArrayList<BroadcastListBean.NewsInfo> list) {
        this.list = list;
    }

    public RobotBean(int type, String content) {
        this.type = type;
        this.content = content;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public int getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
