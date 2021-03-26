package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

/**
 * 会议活动请假审核信息
 */

public class AuditLeaveBean extends BaseGlobal {
    private String strApplyName;//申请人姓名
    private String strAbsentType;//请假类型
    private String strReason;//请假理由
    private String strName;//会议活动名称
    private String strApplyId;//会议活动请假记录id
    private ArrayList<ObjFileBean> affix;

    public String getStrName() {
        return strName;
    }

    public String getStrApplyId() {
        return strApplyId;
    }

    public String getStrApplyName() {
        return strApplyName;
    }

    public String getStrAbsentType() {
        return strAbsentType;
    }

    public String getStrReason() {
        return strReason;
    }

    public ArrayList<ObjFileBean> getAffix() {
        return affix;
    }
}
