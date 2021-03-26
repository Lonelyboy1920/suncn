package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

/**
 * 工作人员端 会议发言列表
 */
public class MeetingSpeakListBean extends BaseGlobal {
    private ArrayList<MeetingSpeakBean> objList;

    public ArrayList<MeetingSpeakBean> getObjList() {
        return objList;
    }

    public class MeetingSpeakBean {
        //会议发言
        private String strId; // 会议发言的主键
        private String strTitle;//会议发言的标题
        private String strSourceName;//发言人
        private String strReportDate;//上报日期
        private String strPubDate;
        private String strCheckState;//0已审核 1待审核
        private String strStateName;//新增字段-----------状态名称显示
        private String strUrl;//详情地址
        private String intToMsz; //提交给秘书长 滁州市政协增加
        private String intToZx;  //提交给主席  滁州市政协增加
        private String strCheckType;
        private String strType; //01-口头发言 02-书面发言
        private String intPass; //0-隐藏不通过按钮  1-展示不通过按钮
        private String strLeaderState;//是否已批示
        private String strIssue;
        private String strAttendName;
        private String strState;//1表示提交0表示暂存
        private String strSector;
        private String strFaction;
        private int intAttend;
        private String strEditStatus;

        public String getStrEditStatus() {
            return strEditStatus;
        }

        public int getIntAttend() {
            return intAttend;
        }

        public String getStrFaction() {
            return strFaction;
        }

        public String getStrSector() {
            return strSector;
        }

        public String getStrState() {
            return strState;
        }

        public String getStrAttendName() {
            return strAttendName;
        }

        public String getStrIssue() {
            return strIssue;
        }

        public String getStrLeaderState() {
            return strLeaderState;
        }

        public String getIntPass() {
            return intPass;
        }

        public void setIntPass(String intPass) {
            this.intPass = intPass;
        }

        public String getStrType() {
            return strType;
        }

        public void setStrType(String strType) {
            this.strType = strType;
        }

        public String getStrCheckType() {
            return strCheckType;
        }

        public void setStrCheckType(String strCheckType) {
            this.strCheckType = strCheckType;
        }

        public String getIntToMsz() {
            return intToMsz;
        }

        public void setIntToMsz(String intToMsz) {
            this.intToMsz = intToMsz;
        }

        public String getIntToZx() {
            return intToZx;
        }

        public void setIntToZx(String intToZx) {
            this.intToZx = intToZx;
        }

        //社情民意
        private String strMasTitle;//标题
        private String strSource;//来源人
        private String strDate;//提交时间
        private String strUseNum;//审核状态、（待审核不传）

        public String getStrPubDate() {
            return strPubDate;
        }

        public String getStrStateName() {
            return strStateName;
        }

        public String getStrMasTitle() {
            return strMasTitle;
        }

        public String getStrSource() {
            return strSource;
        }

        public String getStrDate() {
            return strDate;
        }

        public String getStrUseNum() {
            return strUseNum;
        }

        public String getStrUrl() {
            return strUrl;
        }

        public void setStrUrl(String strUrl) {
            this.strUrl = strUrl;
        }

        public String getStrId() {
            return strId;
        }

        public void setStrId(String strId) {
            this.strId = strId;
        }

        public String getStrTitle() {
            return strTitle;
        }

        public void setStrTitle(String strTitle) {
            this.strTitle = strTitle;
        }

        public String getStrSourceName() {
            return strSourceName;
        }

        public void setStrSourceName(String strSourceName) {
            this.strSourceName = strSourceName;
        }

        public String getStrReportDate() {
            return strReportDate;
        }

        public String getStrCheckState() {
            return strCheckState;
        }

        public void setStrCheckState(String strCheckState) {
            this.strCheckState = strCheckState;
        }

    }
}
