package com.suncn.ihold_zxztc.bean;

/**
 * Created by whh on 2018-9-7.
 * 延期审核信息 MotionDelayCheckGetServlet
 */

public class DelayAuditBean extends BaseGlobal {
    private String strCaseDelayId;//提案的延期审核的主键id
    private String strPubDelayId;//社情民意的延期审核的主键id
    private String strUnitName;//承办单位
    private String strHandlerLimitDate;//应答复日期
    private String strDelayLimitDate;//延期日期
    private String strDelayIdea;//延期理由

    public String getStrCaseDelayId() {
        return strCaseDelayId;
    }

    public String getStrPubDelayId() {
        return strPubDelayId;
    }

    public String getStrUnitName() {
        return strUnitName;
    }

    public String getStrHandlerLimitDate() {
        return strHandlerLimitDate;
    }

    public String getStrDelayLimitDate() {
        return strDelayLimitDate;
    }

    public String getStrDelayIdea() {
        return strDelayIdea;
    }
}
