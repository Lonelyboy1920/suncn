package com.suncn.ihold_zxztc.bean;


public class BaseMeetDetailBean extends BaseGlobal {
    private String strTitle;
    private String strContent;
    private String strStartDate;
    private String strEndDate;
    private String strName;
   private String strCreatId;
   private String strJointUserId;
   private String strCreatUserId;

    public String getStrCreatUserId() {
        return strCreatUserId;
    }

    public String getStrJointUserId() {
        return strJointUserId;
    }

    public String getStrCreatId() {
        return strCreatId;
    }

    public String getStrName() {
        return strName;
    }

    public String getStrTitle() {
        return strTitle;
    }

    public String getStrContent() {
        return strContent;
    }

    public String getStrStartDate() {
        return strStartDate;
    }

    public String getStrEndDate() {
        return strEndDate;
    }

    public String getStrJointMember() {
        return strJointMember;
    }

    private String strJointMember;

}
