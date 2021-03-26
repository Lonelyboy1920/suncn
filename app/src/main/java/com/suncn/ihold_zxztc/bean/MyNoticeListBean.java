package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

public class MyNoticeListBean extends BaseGlobal {
    private ArrayList<MyNotice> noticeList;

    public ArrayList<MyNotice> getNoticeList() {
        return noticeList;
    }

    public class MyNotice {
        private String intDynamicCount;
        private String strNoticeDate;
        private String strNoticeUserId;

        public String getIntDynamicCount() {
            return intDynamicCount;
        }

        public String getStrNoticeDate() {
            return strNoticeDate;
        }

        public String getStrNoticeUserId() {
            return strNoticeUserId;
        }

        public String getStrNoticeUserName() {
            return strNoticeUserName;
        }

        private String strNoticeUserName;
    }
}
