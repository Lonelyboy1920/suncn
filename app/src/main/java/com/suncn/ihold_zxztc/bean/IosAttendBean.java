package com.suncn.ihold_zxztc.bean;

/**
 * 会议/活动手机端签到IosAttendServlet
 */
public class IosAttendBean extends BaseGlobal {
    private int strIsLateSign; // 签到迟到标志（0：未迟到，1：迟到）
    private String strRealSignDate; // 签到时间
    private String strAttendId; // 返回出席记录的id,主要用于迟到时需要回传的参数
    private String intALlIntegral;//总积分
    private String intUseIntegral;//已经用了的积分

    public String getIntALlIntegral() {
        return intALlIntegral;
    }

    public String getIntUseIntegral() {
        return intUseIntegral;
    }
    public int getStrIsLateSign() {
        return strIsLateSign;
    }

    public String getStrRealSignDate() {
        return strRealSignDate;
    }

    public String getStrAttendId() {
        return strAttendId;
    }
}
