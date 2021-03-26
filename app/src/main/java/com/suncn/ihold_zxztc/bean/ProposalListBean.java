package com.suncn.ihold_zxztc.bean;

import com.gavin.giframe.utils.GIStringUtil;

import java.util.ArrayList;

/**
 * 所有提案/已报提案列表ZxtaListServlet
 * 提案分发列表ZxtaFFListServlet
 */
public class ProposalListBean extends BaseGlobal {
    private ArrayList<ProposalBean> objList; // 记录集合

    public ArrayList<ProposalBean> getObjList() {
        return objList;
    }

    public class ProposalBean {
        private String strId; // 任务ID
        private String strCaseMotionId; // 当type为2时联名列表,返回数据，为提案的id，详情取值，
        private String strCaseNo; // 提案号
        private String strTitle; // 提案题目
        private String strUpUnitType; // 提案类型
        private String strSourceName; // 提案者姓名
        private String strPubDate; // 提交时间（格式“yyyy-mm-dd”）（公示列表）
        private String strReportDate; // 提交时间（格式“yyyy-mm-dd”）（提案列表）
        private String strAttendName; // 2，3列表有 显示状态的名称
        private String strSessionCode; // 提案届次号
        private String strFlowState; // 办理状态（已上报、已立案、办理中、待反馈、已反馈（不满意）、已办结）
        private String strResultReason; // 不予立案理由
        private String strResultType; // 不予立案的提案去向
        private String strMainUnit; // 主办单位
        private String strMeetUnit; // 会办单位
        private String strHandlerFinishLevel; // 办理程度
        private String dtHandlerFinishDate; // 办理日期
        private int intAttend; // 是否同意提名（-1联名状态为空；1-同意联名，0-不同意联名 2-待确认） JoinMotionListServlet接口返回
        private String strFeedback;//贵阳用来判断是否可以进行委员反馈
        private String strIsKey;//是否重点提案 1 重点
        private String strIsGood;//是否优秀提案 1 优秀
        private int intCanEdit;//表示是否是草稿箱可以再次编辑1是0否
        private String intState; //strType 1 且 intState 0 有编辑按钮

        public String getIntState() {
            return intState;
        }

        public void setIntState(String intState) {
            this.intState = intState;
        }

        public int getIntCanEdit() {
            return intCanEdit;
        }

        public String getStrIsGood() {
            return strIsGood;
        }

        public String getStrIsKey() {
            return strIsKey;
        }

        public String getStrFeedback() {
            return strFeedback;
        }

        public String getStrAttendName() {
            return strAttendName;
        }

        // intAttend 2，3列表有，联名列表时 intAttend 为2显示 联名按钮操作 附议列表时 intAttend为-1时显示 附议操作按钮
        public int getIntAttend() {
            return intAttend;
        }

        public String getStrPubDate() {
            return strPubDate;
        }

        public String getStrId() {
            return strId;
        }

        public String getStrCaseNo() {
            return strCaseNo;
        }

        public String getStrCaseMotionId() {
            return strCaseMotionId;
        }

        public String getStrTitle() {
            return strTitle;
        }

        public String getStrUpUnitType() {
            return strUpUnitType;
        }

        public String getStrSourceName() {
            if (GIStringUtil.isBlank(strSourceName)) {
                return "";
            }
            return strSourceName;
        }

        public String getPubDate() {
            return strPubDate;
        }

        public String getStrReportDate() {
            return strReportDate;
        }

        public String getStrSessionCode() {
            return strSessionCode;
        }

        public String getStrFlowState() {
            return strFlowState;
        }

        public String getStrResultReason() {
            return strResultReason;
        }

        public String getStrResultType() {
            return strResultType;
        }

        public String getStrMainUnit() {
            return strMainUnit;
        }

        public String getStrMeetUnit() {
            return strMeetUnit;
        }

        public String getStrHandlerFinishLevel() {
            return strHandlerFinishLevel;
        }

        public String getDtHandlerFinishDate() {
            return dtHandlerFinishDate;
        }
    }
}
