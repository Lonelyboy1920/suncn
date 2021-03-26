package com.suncn.ihold_zxztc.bean;

/**
 * Created by whh on 2018-6-7.
 * 二维码签到信息
 */

public class QRInfoBean {
    private String strId;//会议活动记录id
    private String strUserId;//用户id
    private int sign;

    public int getSign() {
        return sign;
    }

    public void setSign(int sign) {
        this.sign = sign;
    }

    public String getStrId() {
        return strId;
    }

    public void setStrId(String strId) {
        this.strId = strId;
    }

    public String getStrUserId() {
        return strUserId;
    }

    public void setStrUserId(String strUserId) {
        this.strUserId = strUserId;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
