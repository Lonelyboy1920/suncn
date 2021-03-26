package com.suncn.ihold_zxztc.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 会议发言通知列表
 */
public class MeetSpeakNoticeListBean extends BaseGlobal {
    private ArrayList<MeetSpeakNoticeBean> objList;

    public ArrayList<MeetSpeakNoticeBean> getObjList() {
        return objList;
    }

    public static class MeetSpeakNoticeBean implements Serializable {
        private String strId;
        private String strMeetSpeakNoticeId;
        private String strTitle; // 会议标题
        private String strPubDate; // 发布日期
        private String strPubUnit; // 发布单位
        private String strMeetName; // 会议名称
        private String strUrl; // URL
        private String strState; // 1已读
        private String strShowAddMeetSpeak; // 1显示提交发言

        public String getStrId() {
            return strId;
        }

        public String getStrTitle() {
            return strTitle;
        }

        public String getStrPubDate() {
            return strPubDate;
        }

        public String getStrMeetName() {
            return strMeetName;
        }

        public String getStrUrl() {
            return strUrl;
        }

        public String getStrMeetSpeakNoticeId() {
            return strMeetSpeakNoticeId;
        }

        public String getStrPubUnit() {
            return strPubUnit;
        }

        public String getStrState() {
            return strState;
        }

        public String getStrShowAddMeetSpeak() {
            return strShowAddMeetSpeak;
        }
    }
}
