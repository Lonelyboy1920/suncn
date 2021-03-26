package com.suncn.ihold_zxztc.bean;

/**
 * Created by whh on 2018-9-11.
 * 活动签到确认EventQRCodeSignViewServlet
 */

public class SignConfirmBean extends BaseGlobal {
    private String strAttendId;//参加活动id
    private String strPathUrl;//头像Url
    private String strName;//活动名称
    private String strDuty;//职务
    private String strFaction;//党派
    private String strSector;//界别
    private String strMemberName;//委员姓名
    private String strPlace;//活动地点
    private String strDate;//时间
    private String strType;//0：会议；1活动

    public String getStrDate() {
        return strDate;
    }

    public String getStrAttendId() {
        return strAttendId;
    }

    public String getStrPathUrl() {
        return strPathUrl;
    }

    public String getStrMemberName() {
        return strMemberName;
    }

    public String getStrPlace() {
        return strPlace;
    }

    public String getStrType() {
        return strType;
    }

    public String getStrName() {
        return strName;
    }

    public String getStrDuty() {
        return strDuty;
    }

    public String getStrFaction() {
        return strFaction;
    }

    public String getStrSector() {
        return strSector;
    }

}
