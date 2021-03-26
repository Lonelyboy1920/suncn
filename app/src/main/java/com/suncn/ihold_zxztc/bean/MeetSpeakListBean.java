package com.suncn.ihold_zxztc.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 会议发言列表
 */
public class MeetSpeakListBean extends BaseGlobal {
    private int intNotReadCount;//未读的数量
    private ArrayList<MeetSpeakBean> objList;

    public ArrayList<MeetSpeakBean> getObjList() {
        return objList;
    }

    public int getIntNotReadCount() {
        return intNotReadCount;
    }

    public static class MeetSpeakBean implements Serializable {
        private String strId;
        private String strTitle; // 会议标题
        private String strPubDate; // 发布日期
        private String strMeetName; // 会议名称
        private String strUrl; // URL
        private String strSourceName; // 发布人
        private String strStateName; // 发言状态（0-未处理；1-口头发言；2-采用为书面发言；3-不采用；

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

        public String getStrSourceName() {
            return strSourceName;
        }

        public String getStrStateName() {
            return strStateName;
        }
    }
}
