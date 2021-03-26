package com.suncn.ihold_zxztc.bean;

import java.io.Serializable;

/**
 * @author :Sea
 * Date :2020-6-10 18:37
 * PackageName:com.suncn.ihold_zxztc.bean
 * Desc:提案详情 反馈的集合
 */
public class ObjFeedBackListBean implements Serializable {
    private String strRecUnitName; //单位名称
    private String strFeedBackResultState; //办理结果
    private String strFeedBackAttitudeState; //办理态度
    private String strFeedBackTotalState; //总体评价
    private String strFeedBackSpecificIdea; //具体意见
    private String strAttendType;//承办单位的类型 主办 、会办 、分办

    public String getStrAttendType() {
        return strAttendType;
    }

    public void setStrAttendType(String strAttendType) {
        this.strAttendType = strAttendType;
    }

    public String getStrRecUnitName() {
        return strRecUnitName;
    }

    public void setStrRecUnitName(String strRecUnitName) {
        this.strRecUnitName = strRecUnitName;
    }

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
}
