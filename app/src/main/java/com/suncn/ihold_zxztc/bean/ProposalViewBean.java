package com.suncn.ihold_zxztc.bean;

import java.util.List;

/**
 * 提案详细ZxtaViewServlet
 */
public class ProposalViewBean extends BaseGlobal {
    private String strId;
    private String strCaseNo; // 届次信息加案号
    private String strTitle; // 题目
    private String strSourceName; // 提案者姓名
    private String strType; // 提案类别
    private String strReportDate; // 上报时间
    private String strMeeting; // 提案类型
    private String strReason; // 情况分析
    private String strWay; // 具体建议
    private String strName; // 提案姓名
    private String strDuty; // 职务
    private String strMobile; // 手机号
    private String strLinkAdd; // 联系地址
    private String strMemReturnAttitude; // 办理态度满意度
    private String strMemReturnAttitudeTxt; // 办理态度意见
    private String strMemReturnIdea; // 办理结果满意度
    private String strMemReturnTxt; // 办理结果意见
    private String strMemReturnMainTxt; // 具体意见
    private String strHandleIdea; // 领导批示审签意见
    private int intIsDistribute; //  是否显示分发按钮（0-不可以分发；1-可以分发）
    private int intSupportMotionName; //  附议人数
    private int intJointlyMem; //  联名人数
    private String strHotWord; // 提案热词
    private String strState;//提案状态
    private String strHandleState;//办理状态
    private String strKeyName;//是否为重点提案
    private String strInMeet;//是否是会中提案

    private String strJointly;//是否启用联名设置 0否 1是
    private String strJointlyMem;//否或联名人；判断不为否时可以点击操作
    private String strSupportMotion;//是否启用附议设置 0否 1 是
    private String strSupportMotionName;//否或附议人；判断不为否时可以点击操作
    private String strHbFinished;// 会办没有办结时，主办不让办理；（1-会办没有办结）【初次在贵州贵阳项目返回该字段，如果返回1需要弹出提醒对话框】
    private String strWriterName;//主笔人
    private String strReasonTitle; //提案内容的命名
    private String strWayTitle;//解决方法的命名
    private String strShowSurvey; //是否显示调研过程   1显示  0不显示
    private String isOwner;

    public String getIsOwner() {
        return isOwner;
    }

    public String getStrShowSurvey() {
        return strShowSurvey;
    }

    public void setStrShowSurvey(String strShowSurvey) {
        this.strShowSurvey = strShowSurvey;
    }

    public String getStrReasonTitle() {
        return strReasonTitle;
    }

    public void setStrReasonTitle(String strReasonTitle) {
        this.strReasonTitle = strReasonTitle;
    }

    public String getStrWayTitle() {
        return strWayTitle;
    }

    public void setStrWayTitle(String strWayTitle) {
        this.strWayTitle = strWayTitle;
    }

    private String strNo;  //案号

    public String getStrNo() {
        return strNo;
    }

    public void setStrNo(String strNo) {
        this.strNo = strNo;
    }

    public String getStrWriterName() {
        return strWriterName;
    }

    public String getStrHbFinished() {
        return strHbFinished;
    }

    public int getIntJointlyMem() {
        return intJointlyMem;
    }

    public int getIntSupportMotionName() {
        return intSupportMotionName;
    }

    public String getStrJointly() {
        return strJointly;
    }

    public String getStrSupportMotion() {
        return strSupportMotion;
    }

    public String getStrSupportMotionName() {
        return strSupportMotionName;
    }

    private List<ObjTransactBean> objDispenseList; // 记录承办单位集合
    private MemOpinions caseFeedBackObj = null;//委员反馈情况
    private List<MotionApproval> objMotionApprovalList;//领导批示集合
    private List<ObjFeedBackListBean> objFeedBackList;  //反馈的集合
    private String strFeedBackAllState;  //整体评价

    public String getStrFeedBackAllState() {
        return strFeedBackAllState;
    }

    public void setStrFeedBackAllState(String strFeedBackAllState) {
        this.strFeedBackAllState = strFeedBackAllState;
    }

    public List<ObjFeedBackListBean> getObjFeedBackList() {
        return objFeedBackList;
    }

    public void setObjFeedBackList(List<ObjFeedBackListBean> objFeedBackList) {
        this.objFeedBackList = objFeedBackList;
    }

    public List<MotionApproval> getObjMotionApprovalList() {
        return objMotionApprovalList;
    }

    private String strDiaoyanguocheng;//调研过程
    private String strSurveyProcess;// 调研过程 V5.0修改增加

    public String getStrSurveyProcess() {
        return strSurveyProcess;
    }

    public void setStrSurveyProcess(String strSurveyProcess) {
        this.strSurveyProcess = strSurveyProcess;
    }

    public String getStrId() {
        return strId;
    }

    public String getStrDiaoyanguocheng() {
        return strDiaoyanguocheng;
    }

    public MemOpinions getCaseFeedBackObj() {
        return caseFeedBackObj;
    }


    public String getStrInMeet() {
        return strInMeet;
    }

    public String getStrJointlyMem() {
        return strJointlyMem;
    }

    public String getStrKeyName() {
        return strKeyName;
    }

    public String getStrHandleState() {
        return strHandleState;
    }

    public String getStrState() {
        return strState;
    }

    public String getStrHotWord() {
        return strHotWord;
    }

    public int getIntIsDistribute() {
        return intIsDistribute;
    }


    public String getStrCaseNo() {
        return strCaseNo;
    }

    public String getStrTitle() {
        return strTitle;
    }

    public String getStrSourceName() {
        return strSourceName;
    }

    public String getStrType() {
        return strType;
    }

    public String getDtPubDate() {
        return strReportDate;
    }

    public String getStrMeeting() {
        return strMeeting;
    }

    public String getStrReason() {
        return strReason;
    }

    public String getStrWay() {
        return strWay;
    }

    public String getStrName() {
        return strName;
    }

    public String getStrDuty() {
        return strDuty;
    }

    public String getStrMobile() {
        return strMobile;
    }

    public String getStrLinkAdd() {
        return strLinkAdd;
    }

    public String getStrMemReturnAttitude() {
        return strMemReturnAttitude;
    }

    public String getStrMemReturnAttitudeTxt() {
        return strMemReturnAttitudeTxt;
    }

    public String getStrMemReturnIdea() {
        return strMemReturnIdea;
    }

    public String getStrMemReturnTxt() {
        return strMemReturnTxt;
    }

    public String getStrMemReturnMainTxt() {
        return strMemReturnMainTxt;
    }

    public String getStrHandleIdea() {
        return strHandleIdea;
    }

    public List<ObjTransactBean> getObjDispenseList() {
        return objDispenseList;
    }

    public class MemOpinions {
        private String strLastMemFeedBackState = "";//综合意见
        private String strMemFeedBackAttitudeIdea = "";//办理态度意见
        private String strMemFeedBackResultIdea = "";//办理结果意见
        private String strMemFeedBackMainIdea = "";//具体意见

        public String getStrLastMemFeedBackState() {
            return strLastMemFeedBackState;
        }

        public String getStrMemFeedBackAttitudeIdea() {
            return strMemFeedBackAttitudeIdea;
        }

        public String getStrMemFeedBackResultIdea() {
            return strMemFeedBackResultIdea;
        }

        public String getStrMemFeedBackMainIdea() {
            return strMemFeedBackMainIdea;
        }
    }

    public class MotionApproval {//领导批示
        private String strLeader;//批示领导
        private String strComment;//批示内容
        private String strCommentDate;//批示日期
        private String strUserName;//录入人
        private String strLeaderType;//批示类型

        public String getStrLeaderType() {
            return strLeaderType;
        }

        public String getStrLeader() {
            return strLeader;
        }

        public String getStrComment() {
            return strComment;
        }

        public String getStrCommentDate() {
            return strCommentDate;
        }

        public String getStrUserName() {
            return strUserName;
        }
    }
}
