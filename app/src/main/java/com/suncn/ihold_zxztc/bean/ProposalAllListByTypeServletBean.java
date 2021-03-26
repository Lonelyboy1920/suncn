package com.suncn.ihold_zxztc.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author :Sea
 * Date :2020-6-12 15:40
 * PackageName:com.suncn.ihold_zxztc.bean
 * Desc:机关端的管理列表
 */
public class ProposalAllListByTypeServletBean extends BaseGlobal {

    private List<ObjListBean> objList;

    public List<ObjListBean> getObjList() {
        return objList;
    }

    public void setObjList(List<ObjListBean> objList) {
        this.objList = objList;
    }

    public static class ObjListBean {
        /**
         * strId : 6c78b4fc1dee44d1936e73178c9da6c8
         * strNo :
         * strTitle : 關于維護老年獨生子女父母權益的建議
         * strSourceName : 李菲菲
         * strSector : 民革
         * strFaction : 中共
         * strType :
         * strReportDate : 2020-06-11
         * strIsKey : 0
         * strIsGood : 0
         * strFlowState : 未接收
         * strCheckResultType :
         * strAttendType :
         * strMainUnit :
         * strMeetUnit :
         */

        private String strId;
        private String strNo;
        private String strTitle;
        private String strSourceName;
        private String strSector;
        private String strFaction;
        private String strType;
        private String strReportDate;
        private String strIsKey;
        private String strIsGood;
        private String strFlowState;
        private String strCheckResultType;   //提案去向
        private String strAttendType;  //1、主办、2、会办、3、分办
        private String strMainUnit;
        private String strMeetUnit;
        private String strCaseNo;
        private String strPubDate;
        private String strHandlerFinishLevel;
        private String dtHandlerFinishDate;
        private String strHandlerType;
        private String strIskey;
        private String strSuggestUnitName;  //承办单位

        public String getStrSuggestUnitName() {
            return strSuggestUnitName;
        }

        public void setStrSuggestUnitName(String strSuggestUnitName) {
            this.strSuggestUnitName = strSuggestUnitName;
        }

        public String getStrPubDate() {
            return strPubDate;
        }

        public void setStrPubDate(String strPubDate) {
            this.strPubDate = strPubDate;
        }

        public String getStrHandlerFinishLevel() {
            return strHandlerFinishLevel;
        }

        public void setStrHandlerFinishLevel(String strHandlerFinishLevel) {
            this.strHandlerFinishLevel = strHandlerFinishLevel;
        }

        public String getDtHandlerFinishDate() {
            return dtHandlerFinishDate;
        }

        public void setDtHandlerFinishDate(String dtHandlerFinishDate) {
            this.dtHandlerFinishDate = dtHandlerFinishDate;
        }

        public String getStrHandlerType() {
            return strHandlerType;
        }

        public void setStrHandlerType(String strHandlerType) {
            this.strHandlerType = strHandlerType;
        }

        public String getStrIskey() {
            return strIskey;
        }

        public void setStrIskey(String strIskey) {
            this.strIskey = strIskey;
        }

        public String getStrCaseNo() {
            return strCaseNo;
        }

        public void setStrCaseNo(String strCaseNo) {
            this.strCaseNo = strCaseNo;
        }

        public String getStrId() {
            return strId;
        }

        public void setStrId(String strId) {
            this.strId = strId;
        }

        public String getStrNo() {
            return strNo;
        }

        public void setStrNo(String strNo) {
            this.strNo = strNo;
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

        public String getStrSector() {
            return strSector;
        }

        public void setStrSector(String strSector) {
            this.strSector = strSector;
        }

        public String getStrFaction() {
            return strFaction;
        }

        public void setStrFaction(String strFaction) {
            this.strFaction = strFaction;
        }

        public String getStrType() {
            return strType;
        }

        public void setStrType(String strType) {
            this.strType = strType;
        }

        public String getStrReportDate() {
            return strReportDate;
        }

        public void setStrReportDate(String strReportDate) {
            this.strReportDate = strReportDate;
        }

        public String getStrIsKey() {
            return strIsKey;
        }

        public void setStrIsKey(String strIsKey) {
            this.strIsKey = strIsKey;
        }

        public String getStrIsGood() {
            return strIsGood;
        }

        public void setStrIsGood(String strIsGood) {
            this.strIsGood = strIsGood;
        }

        public String getStrFlowState() {
            return strFlowState;
        }

        public void setStrFlowState(String strFlowState) {
            this.strFlowState = strFlowState;
        }

        public String getStrCheckResultType() {
            return strCheckResultType;
        }

        public void setStrCheckResultType(String strCheckResultType) {
            this.strCheckResultType = strCheckResultType;
        }

        public String getStrAttendType() {
            return strAttendType;
        }

        public void setStrAttendType(String strAttendType) {
            this.strAttendType = strAttendType;
        }

        public String getStrMainUnit() {
            return strMainUnit;
        }

        public void setStrMainUnit(String strMainUnit) {
            this.strMainUnit = strMainUnit;
        }

        public String getStrMeetUnit() {
            return strMeetUnit;
        }

        public void setStrMeetUnit(String strMeetUnit) {
            this.strMeetUnit = strMeetUnit;
        }
    }
}
