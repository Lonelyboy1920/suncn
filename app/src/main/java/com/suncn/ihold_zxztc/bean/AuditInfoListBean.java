package com.suncn.ihold_zxztc.bean;

import java.util.List;

/**
 * Created by whh on 2018-9-6.
 * 审核信息MotionCheckGetServlet
 */

public class AuditInfoListBean extends BaseGlobal {
    private String strNowCheckStateName;//当前状态
    private boolean checkIsFinishCheck;//true立案并分发,false:立案
    private int intDistType;//分发类型：1（承办系统）、2（承办单位）
    private String strMeetUnit;//会办单位:u/id/name
    private String strCbxtUnit;//承办系统:u/id/name
    private String strCheckUserName;//审核人员
    private int intAgreePub;//是否公开1（同意公开）,0（不同意公开）
    private String strKeyName;//是否重点提案1（是）,0（否）
    private String strInMeet;//是否会中提案1（是）,0（否）
    private String strMainUnit;//主办单位:u/id/name
    private String strCheckResultType;//不予立案的提案去向编码
    private String strCheckResultTypeName;//不予案的提案去向名称
    private String strCheckResultState;//1(立案或立案并分发）2不予立案)
    private String strHandlerType;//办理方式：1(主办：主办单位单选)、2（会办：主办单位单选、会办单位多选）、3（分办：主办单位多选）；分发类型为承办系统：承办系统单选
    private String strCaseTypeName;//提案分类名称
    private String strCaseTypeId;//提案分类id
    private String strCaseMotionSessionId;//界次id

    public String getStrInMeet() {
        return strInMeet;
    }

    public String getStrCaseMotionSessionId() {
        return strCaseMotionSessionId;
    }

    public String getStrCaseTypeName() {
        return strCaseTypeName;
    }

    public String getStrCaseTypeId() {
        return strCaseTypeId;
    }

    private List<AuditInfo> caseCheckList;//
    private List<ProSign> preHandlerList;//预交办

    public List<ProSign> getPreHandlerList() {
        return preHandlerList;
    }

    public String getStrCheckResultState() {
        return strCheckResultState;
    }

    public String getStrNowCheckStateName() {
        return strNowCheckStateName;
    }

    public boolean isCheckIsFinishCheck() {
        return checkIsFinishCheck;
    }

    public int getIntDistType() {
        return intDistType;
    }

    public String getStrMeetUnit() {
        return strMeetUnit;
    }

    public String getStrCbxtUnit() {
        return strCbxtUnit;
    }

    public String getStrCheckUserName() {
        return strCheckUserName;
    }

    public int getIntAgreePub() {
        return intAgreePub;
    }

    public String getStrKeyName() {
        return strKeyName;
    }

    public String getStrMainUnit() {
        return strMainUnit;
    }

    public String getStrCheckResultType() {
        return strCheckResultType;
    }

    public String getStrHandlerType() {
        return strHandlerType;
    }

    public String getStrCheckResultTypeName() {
        return strCheckResultTypeName;
    }

    public List<AuditInfo> getCaseCheckList() {
        return caseCheckList;
    }

    public class AuditInfo {
        private String strHeadTitle;//
        private String strCheckUserName;//审核人
        private String strCheckDate;//审核时间
        private String strFlowStateName;//审核结果
        private String strCheckResultType;//不予立案的提案去向
        private int intDistType;//2承办单位,1承办系统
        private String strUnitName;//承办单位名称
        private String strCheckIdea;//审核意见
        private String strLiAnNoReason;//不予立案理由

        public String getStrLiAnNoReason() {
            return strLiAnNoReason;
        }

        public String getStrHeadTitle() {
            return strHeadTitle;
        }

        public String getStrCheckUserName() {
            return strCheckUserName;
        }

        public String getStrCheckDate() {
            return strCheckDate;
        }

        public String getStrFlowStateName() {
            return strFlowStateName;
        }

        public String getStrCheckResultType() {
            return strCheckResultType;
        }

        public int getIntDistType() {
            return intDistType;
        }

        public String getStrUnitName() {
            return strUnitName;
        }

        public String getStrCheckIdea() {
            return strCheckIdea;
        }
    }

    public class ProSign {//预签收
        private String strHandlerTypeName;//承办单位的办理类型：主办或者会办
        private String strRecUnitName;//单位名称
        private String strMemFeedBackState;//状态名称
        private String strSuggestUnitName;//建议承办单位
        private String strBackIdea;//反馈意见
        private String strCaseMotionSessionId;//届次的id

        public String getStrHandlerTypeName() {
            return strHandlerTypeName;
        }

        public String getStrRecUnitName() {
            return strRecUnitName;
        }

        public String getStrMemFeedBackState() {
            return strMemFeedBackState;
        }

        public String getStrSuggestUnitName() {
            return strSuggestUnitName;
        }

        public String getStrBackIdea() {
            return strBackIdea;
        }

        public String getStrCaseMotionSessionId() {
            return strCaseMotionSessionId;
        }
    }
}
