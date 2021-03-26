package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

/**
 * 新闻播报的信息
 */
public class BroadcastListBean extends BaseGlobal {
    private ArrayList<NewsInfo> newsInfoList; // 新闻集合
    private ArrayList<WorkMsg> workMsgList; // 业务数据集合
    private ArrayList<NewsInfo> objList;
    private int intType;

    public int getIntType() {
        return intType;
    }

    public ArrayList<NewsInfo> getObjList() {
        return objList;
    }

    public ArrayList<NewsInfo> getNewsInfoList() {
        return newsInfoList;
    }

    public ArrayList<WorkMsg> getWorkMsgList() {
        return workMsgList;
    }

    public class NewsInfo {
        private String strId; // 信息id
        private String strTitle;
        private String strUrl;
        private String strNewsTitle;

        public String getStrId() {
            return strId;
        }

        public String getStrUrl() {
            return strUrl;
        }

        public String getStrNewsTitle() {
            return strNewsTitle;
        }

        public String getStrTitle() {
            return strTitle;
        }
    }

    public class WorkMsg {
        private String strContent;

        public String getStrContent() {
            return strContent;
        }
    }
}
