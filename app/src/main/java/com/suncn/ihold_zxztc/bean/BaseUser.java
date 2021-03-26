package com.suncn.ihold_zxztc.bean;

import java.io.Serializable;

/**
 * 用户信息
 */
public class BaseUser implements Serializable {
    private String strId; // 机构ID
    private String strUserId; // 委员用户名
    private String strLinkId; // 用户帐号@域名
    private String strName; // 委员姓名
    private String strDuty; // 委员职务
    private String strSector; // 委员界别
    private String strFaction; // 委员党派
    private String strMobile; // 委员手机号
    private String strPathUrl; // 头像路径
    private int intUserRole;// 用户类型（ 0-委员 1-工作人员 2-领导）
    private String strOpenState;//0未开通，1：开通
    private boolean isChecked;
    private String strOpenMobile;
    private String strUrl;//邀请url
    private String strQDUserId;

    public String getStrQDUserId() {
        return strQDUserId;
    }

    public String getStrUrl() {
        return strUrl;
    }

    public String getStrId() {
        return strId;
    }

    public String getStrUserId() {
        return strUserId;
    }

    public String getStrLinkId() {
        return strLinkId;
    }

    public String getStrName() {
        return strName;
    }

    public String getStrDuty() {
        return strDuty;
    }

    public String getStrSector() {
        return strSector;
    }

    public String getStrFaction() {
        return strFaction;
    }

    public String getStrMobile() {
        return strMobile;
    }

    public String getStrPathUrl() {
        return strPathUrl;
    }

    public int getIntUserRole() {
        return intUserRole;
    }

    public String getStrOpenState() {
        return strOpenState;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public String getStrOpenMobile() {
        return strOpenMobile;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
