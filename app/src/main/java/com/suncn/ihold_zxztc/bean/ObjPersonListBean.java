package com.suncn.ihold_zxztc.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/13.
 */

public class ObjPersonListBean implements Serializable {
    private String strId; // 活动记录id （活动通知会有）
    private String strName; // 委员姓名
    private String strMtName; // 主会名称（没有次会时显示）
    private String strFaction; // 党派
    private String strSector; // 界别
    private int intAttend; // 出席状态   0请假1出席2缺席
    private String strAbsentType; // 请假类型（出席状态为请假时出现）
    private String strReason; // 请假原因（出席状态为请假时出现）
    private String strMobile; // 手机

    public String getStrName() {
        return strName;
    }

    public String getStrMtName() {
        return strMtName;
    }

    public String getStrFaction() {
        return strFaction;
    }

    public String getStrSector() {
        return strSector;
    }

    public int getIntAttend() {
        return intAttend;
    }

    public String getStrAbsentType() {
        return strAbsentType;
    }

    public String getStrReason() {
        return strReason;
    }

    public String getStrMobile() {
        return strMobile;
    }
}
