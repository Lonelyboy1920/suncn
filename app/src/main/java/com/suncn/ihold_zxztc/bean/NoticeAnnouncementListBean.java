package com.suncn.ihold_zxztc.bean;

import java.util.List;

/**
 * 通知公告
 */
public class NoticeAnnouncementListBean extends BaseGlobal {
    private List<AnnouncementListBean> objList;

    public List<AnnouncementListBean> getObjList() {
        return objList;
    }

    public void setObjList(List<AnnouncementListBean> objList) {
        this.objList = objList;
    }

    public static class AnnouncementListBean {
        /**
         * objNoticeList : [{"strTitle":"关于创业带动就业，推动更高质量就业的建议","strIssue":"大会发言征集通知","strId":"08f8acf1270a4c388248d8ed6fc53228","strState":"0","strUrl":"/ios/SpeakNoticeView?strId=08f8acf1270a4c388248d8ed6fc53228&strUserId=1061ae84c34645978a7dd7a94bdcef47","strContent":"关于创业带动就业，推动更高质量就业的建议"},{"strTitle":"社会法制委员会全体会议","strIssue":"大会发言征集通知","strId":"92a15dcd1b904c82a919cadb5106f480","strState":"0","strUrl":"/ios/SpeakNoticeView?strId=92a15dcd1b904c82a919cadb5106f480&strUserId=1061ae84c34645978a7dd7a94bdcef47","strContent":"社会法制委员会全体会议"}]
         * strPubDate : 2018-12-10
         */
        private String strPubDate;
        private List<ObjAnnouncementBean> objNoticeList;

        public String getStrPubDate() {
            return strPubDate;
        }

        public void setStrPubDate(String strPubDate) {
            this.strPubDate = strPubDate;
        }

        public List<ObjAnnouncementBean> getObjNoticeList() {
            return objNoticeList;
        }
    }
}
