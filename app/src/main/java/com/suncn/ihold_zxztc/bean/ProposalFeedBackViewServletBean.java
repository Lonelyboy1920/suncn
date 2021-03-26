package com.suncn.ihold_zxztc.bean;

import java.util.List;

/**
 * @author :Sea
 * Date :2020-6-11 9:23
 * PackageName:com.suncn.ihold_zxztc.bean
 * Desc:委员反馈的信息
 */
public class ProposalFeedBackViewServletBean extends BaseGlobal {
    private List<ObjListBean> objList;
    private String strAttendTypeCode;

    public String getStrAttendTypeCode() {
        return strAttendTypeCode;
    }

    public List<ObjListBean> getObjList() {
        return objList;
    }

    public void setObjList(List<ObjListBean> objList) {
        this.objList = objList;
    }

    public class ObjListBean {
        private String strProposalDispenseId;   //承办单位的id
        private String strRecUnitName;  //承办单位的名称
        private String strAttendType;  //承办单位的类型 主办 、会办 、分办

        private String strFeedBackResultState;  //办理结果
        private String strFeedBackAttitudeState;// 办理态度
        private String strFeedBackTotalState;//总体评价
        private String strFeedBackSpecificIdea;//具体意见

        public String getStrFeedBackResultState() {
            return strFeedBackResultState;
        }

        public void setStrFeedBackResultState(String strFeedBackResultState) {
            this.strFeedBackResultState = strFeedBackResultState;
        }

        public String getStrFeedBackAttitudeState() {
            return strFeedBackAttitudeState;
        }

        public void setStrFeedBackAttitudeState(String strFeedBackAttitudeState) {
            this.strFeedBackAttitudeState = strFeedBackAttitudeState;
        }

        public String getStrFeedBackTotalState() {
            return strFeedBackTotalState;
        }

        public void setStrFeedBackTotalState(String strFeedBackTotalState) {
            this.strFeedBackTotalState = strFeedBackTotalState;
        }

        public String getStrFeedBackSpecificIdea() {
            return strFeedBackSpecificIdea;
        }

        public void setStrFeedBackSpecificIdea(String strFeedBackSpecificIdea) {
            this.strFeedBackSpecificIdea = strFeedBackSpecificIdea;
        }

        public String getStrProposalDispenseId() {
            return strProposalDispenseId;
        }

        public void setStrProposalDispenseId(String strProposalDispenseId) {
            this.strProposalDispenseId = strProposalDispenseId;
        }

        public String getStrRecUnitName() {
            return strRecUnitName;
        }

        public void setStrRecUnitName(String strRecUnitName) {
            this.strRecUnitName = strRecUnitName;
        }

        public String getStrAttendType() {
            return strAttendType;
        }

        public void setStrAttendType(String strAttendType) {
            this.strAttendType = strAttendType;
        }
    }
}
