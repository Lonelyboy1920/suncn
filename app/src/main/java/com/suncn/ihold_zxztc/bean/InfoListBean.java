package com.suncn.ihold_zxztc.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by whh on 2017-12-13.
 * 首页
 */

public class InfoListBean extends BaseGlobal {
    private ArrayList<ObjInfo> workMsgList;//记录集合
    private ArrayList<ObjInfo> bannerNewsList; // 轮播图
    private ArrayList<ObjInfo> newsInfoList; // 新闻资讯
    private ArrayList<ObjInfo> radarList;//雷达图集合
    private ArrayList<ObjInfo> objList; // 新闻列表
    private String strRankShow;
    private int intEventCount;//活动条数
    private int intMeetCount;//会议条数

    public int getIntEventCount() {
        return intEventCount;
    }

    public int getIntMeetCount() {
        return intMeetCount;
    }

    public ArrayList<ObjInfo> getObjList() {
        return objList;
    }

    public ArrayList<ObjInfo> getRadarList() {
        return radarList;
    }

    public ArrayList<ObjInfo> getWorkMsgList() {
        return workMsgList;
    }

    public ArrayList<ObjInfo> getBannerNewsList() {
        return bannerNewsList;
    }

    public ArrayList<ObjInfo> getNewsInfoList() {
        return newsInfoList;
    }

    public String getStrRankShow() {
        return strRankShow;
    }

    public static class ObjInfo implements Serializable {
        private String strTypeName;//雷达图名称
        private int intScore;//得分
        private String intScoreRatio;//得分吧比例
        private String strId; // 信息id
        private String strTitle; // 标题
        private String strImagePath; //  图片路径
        private String strPubUnit; // 新闻发布单位
        private String strPubDate; // 消息发布时间
        private int intCount;//阅读人数
        private String strUrl;
        private String strDate;//任务提醒时间


        public String getStrDate() {
            return strDate;
        }

        public String getStrPubUnit() {
            return strPubUnit;
        }

        public String getStrTypeName() {
            return strTypeName;
        }

        public int getIntScore() {
            return intScore;
        }

        public String getIntScoreRatio() {
            return intScoreRatio;
        }

        public String getStrUrl() {
            return strUrl;
        }

        public void setStrId(String strId) {
            this.strId = strId;
        }

        public String getStrId() {
            return strId;
        }

        public String getStrTitle() {
            return strTitle;
        }

        public String getStrImagePath() {
            return strImagePath;
        }

        public String getStrPubDate() {
            return strPubDate;
        }

        public int getIntCount() {
            return intCount;
        }

        public void setStrTitle(String strTitle) {
            this.strTitle = strTitle;
        }

        public void setStrPubDate(String strPubDate) {
            this.strPubDate = strPubDate;
        }
    }

}
