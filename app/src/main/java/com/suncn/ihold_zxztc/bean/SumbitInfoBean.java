package com.suncn.ihold_zxztc.bean;

import java.util.List;

/**
 * Created by Administrator on 2018-5-28.
 */

public class SumbitInfoBean extends BaseGlobal {
    private String strReason;//情况分析
    private String strJointlyMem;//联名委员Id
    private String strJointlyMemName;//联名委员
    private String intNoReply;//是否需要回复
    private String strTitle;//标题
    private String strSuggestUnitId;//承办单位Id
    private String strSuggestUnitName;//承办单位
    private String strWay;//具体建议
    private String intInvestigate;//经过调研
    private String intThread;//网上参考
    private String intAgreePub;//是否公开
    private String intBringUpFirst;//是否第一次
    private String intWriteSelf;//由本人撰写
    private String strTalkHandleType;//协商方式
    private List<HandlerWay> objList;
    private String strDiaoyanguocheng;//调研过程
    private String strSurveyProcess;// 调研过程 V5.0修改增加

    public String getStrSurveyProcess() {
        return strSurveyProcess;
    }

    public void setStrSurveyProcess(String strSurveyProcess) {
        this.strSurveyProcess = strSurveyProcess;
    }

    public String getStrDiaoyanguocheng() {
        return strDiaoyanguocheng;
    }

    public List<HandlerWay> getObjList() {
        return objList;
    }

    public class HandlerWay {
        private String strName;
        private boolean checked;

        public String getStrName() {
            return strName;
        }

        public boolean isChecked() {
            return checked;
        }
    }

    public String getStrReason() {
        return strReason;
    }

    public String getStrJointlyMem() {
        return strJointlyMem;
    }

    public String getStrJointlyMemName() {
        return strJointlyMemName;
    }

    public String getIntNoReply() {
        return intNoReply;
    }

    public String getStrOriginCase() {
        return strTitle;
    }

    public String getStrSuggestUnitId() {
        return strSuggestUnitId;
    }

    public String getStrSuggestUnitName() {
        return strSuggestUnitName;
    }

    public String getStrWay() {
        return strWay;
    }

    public String getIntInvestigate() {
        return intInvestigate;
    }

    public String getIntThread() {
        return intThread;
    }

    public String getIntAgreePub() {
        return intAgreePub;
    }

    public String getIntBringUpFirst() {
        return intBringUpFirst;
    }

    public String getIntWriteSelf() {
        return intWriteSelf;
    }

    public String getStrTalkHandleType() {
        return strTalkHandleType;
    }
}
