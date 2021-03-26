package com.suncn.ihold_zxztc.bean;

/**
 * 通知公告实体类
 */
public class ObjAnnouncementBean {
    /**
     * strTitle : 关于创业带动就业，推动更高质量就业的建议
     * strIssue : 大会发言征集通知
     * strId : 08f8acf1270a4c388248d8ed6fc53228
     * strState : 0
     * strUrl : /ios/SpeakNoticeView?strId=08f8acf1270a4c388248d8ed6fc53228&strUserId=1061ae84c34645978a7dd7a94bdcef47
     * strContent : 关于创业带动就业，推动更高质量就业的建议
     */
    private String strTitle;
    private String strIssue;
    private String strId;
    private String strAffirId;//会议发言
    private String strState;// 0未读 1已读
    private String strUrl;
    private String strContent;
    private String strSorceType;//3大会发言，跳webwiew用
    private String strPubDate;
    private String strTopTitle;

    public String getStrTopTitle() {
        return strTopTitle;
    }

    public String getStrPubDate() {
        return strPubDate;
    }

    public void setStrPubDate(String strPubDate) {
        this.strPubDate = strPubDate;
    }

    public String getStrAffirId() {
        return strAffirId;
    }

    public String getStrSorceType() {
        return strSorceType;
    }

    public String getStrTitle() {
        return strTitle;
    }

    public void setStrTitle(String strTitle) {
        this.strTitle = strTitle;
    }

    public String getStrIssue() {
        return strIssue;
    }

    public void setStrIssue(String strIssue) {
        this.strIssue = strIssue;
    }

    public String getStrId() {
        return strId;
    }

    public void setStrId(String strId) {
        this.strId = strId;
    }

    public String getStrState() {
        return strState;
    }

    public void setStrState(String strState) {
        this.strState = strState;
    }

    public String getStrUrl() {
        return strUrl;
    }

    public void setStrUrl(String strUrl) {
        this.strUrl = strUrl;
    }

    public String getStrContent() {
        return strContent;
    }

    public void setStrContent(String strContent) {
        this.strContent = strContent;
    }
}
