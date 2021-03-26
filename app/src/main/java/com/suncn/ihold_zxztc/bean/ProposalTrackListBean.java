package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

/**
 * Created by whh on 2018-9-5.
 * 办理追踪
 * MotionDistServlet
 */

public class ProposalTrackListBean extends BaseGlobal {
    private ArrayList<ProposalTrack> objList;

    public ArrayList<ProposalTrack> getObjList() {
        return objList;
    }

    public class ProposalTrack {
        private String strId;//主键
        private String strCaseMotionId;//提案主键
        private String strFromNo;//流水号
        private String strTitle;//提案标题
        private String strSourceName;//上报人
        private int intRecUnitType;//1、承办系统2、承办单位
        private String strRecUnitName;//单位名称
        private String strHandlerLimitDate;//应答复时间
        //strState
        //0(且intRecUnitType为1时，为提醒交办，intRecUnitType为2时，为提醒签收),
        //1（且strDelayState为1，为延期审核,strDelayState 0为催办）
        private String strState;
        private String strDelayState;//1已延期0正常
        private String strReportDate;//上报日期
        private String strHandlerState;//1 待反馈 显示 催委员反馈的按钮
        private String strDelayStateName;//申请延期（直接显示）
        private String strStateName;//初审或复审 或者 待审核
        private String strIsKey;//是否重点提案 1 重点
        private String strIsGood;//是否优秀提案 1 优秀
        //strHasSecondHandler是否第二次办理 1 显示标识 当strHasSecondHandler 为0 strHandlerState 为2 时 intUserRole 为 1 代表委员反馈不满意，显示重新办理按钮
        private String strHasSecondHandler;

        private int intState;//当前状态 intState为2,显示审核按钮
        private String strCaseNo;//案号
        private String strHandlerTypeName;//直接显示 主办 会办

        public String getStrHandlerTypeName() {
            return strHandlerTypeName;
        }

        public String getStrFromNo() {
            return strFromNo;
        }

        public int getIntState() {
            return intState;
        }

        public String getStrIsKey() {
            return strIsKey;
        }

        public String getStrIsGood() {
            return strIsGood;
        }

        public String getStrHasSecondHandler() {
            return strHasSecondHandler;
        }

        public String getStrStateName() {
            return strStateName;
        }


        public String getStrDelayStateName() {
            return strDelayStateName;
        }

        public String getStrId() {
            return strId;
        }

        public String getStrCaseMotionId() {
            return strCaseMotionId;
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

        public int getIntRecUnitType() {
            return intRecUnitType;
        }


        public String getStrRecUnitName() {
            return strRecUnitName;
        }

        public String getStrHandlerLimitDate() {
            return strHandlerLimitDate;
        }

        public String getStrState() {
            return strState;
        }

        public String getStrReportDate() {
            return strReportDate;
        }

        public String getStrHandlerState() {
            return strHandlerState;
        }

        public String getStrDelayState() {
            return strDelayState;
        }
    }

}
