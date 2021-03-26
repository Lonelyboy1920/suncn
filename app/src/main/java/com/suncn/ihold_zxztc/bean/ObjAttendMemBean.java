package com.suncn.ihold_zxztc.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 会议/活动参与人员（出席情况）
 */
public class ObjAttendMemBean {
    /**
     * intModify : 1
     * strName : 曹荣山
     * strAttendId : 8d728e4c61d74f1a97cb904de8ea6f92
     * intAttend : 1
     * strPathUrl : /res/upload/2018/12/19/2ff7360c218f4dad99a9a266753831d0.jpg
     * affix : []
     */
    @SerializedName("intCount")
    private int intCount;
    private int intModify;
    private String strName;
    private String strAbsentType;//请假类型
    private String strReason;//请假理由
    private String strAttendId;
    private int intAttend;
    private String strPathUrl;
    private List<ObjFileBean> affix;
    private List<ObjAttendMemBean> mem_list;
    private String strLeaveType;

    public String getStrLeaveType() {
        return strLeaveType;
    }

    public int getIntCount() {
        return intCount;
    }

    public List<ObjAttendMemBean> getMem_list() {
        return mem_list;
    }

    public String getStrAbsentType() {
        return strAbsentType;
    }

    public String getStrReason() {
        return strReason;
    }

    public int getIntModify() {
        return intModify;
    }

    public void setIntModify(int intModify) {
        this.intModify = intModify;
    }

    public String getStrName() {
        return strName;
    }

    public void setStrName(String strName) {
        this.strName = strName;
    }

    public String getStrAttendId() {
        return strAttendId;
    }

    public void setStrAttendId(String strAttendId) {
        this.strAttendId = strAttendId;
    }

    public int getIntAttend() {
        return intAttend;
    }

    public void setIntAttend(int intAttend) {
        this.intAttend = intAttend;
    }

    public String getStrPathUrl() {
        return strPathUrl;
    }

    public void setStrPathUrl(String strPathUrl) {
        this.strPathUrl = strPathUrl;
    }

    public List<ObjFileBean> getAffix() {
        return affix;
    }

    public void setAffix(List<ObjFileBean> affix) {
        this.affix = affix;
    }
}
