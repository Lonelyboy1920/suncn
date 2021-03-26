package com.suncn.ihold_zxztc.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by whh on 2018-8-30.
 * 提案处理：提案待接收、待审核、(或预立案、待分发)列表
 * MotionRecWithCheckListServlet
 */

public class ProposalDealListBean extends BaseGlobal {
    private ArrayList<ProposalDeal> objList;
    private String strOnlyType;

    public String getStrOnlyType() {
        return strOnlyType;
    }

    public ArrayList<ProposalDeal> getObjList() {
        return objList;
    }

    public class ProposalDeal implements Serializable {
        private String strId;//主键
        private String strCaseMotionId;//提案主键
        private String strCaseNo;//案号
        private String strTitle;//提案标题
        private String strSourceName;//上报人
        private int intRecUnitType;//1、承办系统2、承办单位
        private String strRecUnitName;//单位名称
        private String strHandlerLimitDate;//应答复时间
        private String intState;//0(且intRecUnitType为1时，为提醒交办，intRecUnitType为1时，为提醒签收),-2为申退审核，1（且strDelayState为1，为延期审核）
        private String strReportDate;//时间
        private String strMotionCheckState;//时间
        private String isShowThButton;//是否显示按钮
        private String strMemFeedBackState;//反馈状态

        private String strDelayState;//1已延期0正常
        private String strHandlerTypeName;//承办系统、主办单位、会办单位
        //社情民意
        private String strPubId;//社情民意的主键 机关委：办理追踪中、办理追踪中的办理中显示
        private String strMasTitle;//标题
        private String strIssue;//类别名称 年份 期号(待审核、已审核中不显示,其他必显示)
        private String strSource;//来源人(待审核、已审核中必显示,其他不显示)
        private String strDate;//时间
        private String strState;//机关委：状态(已采编时,0未分发，1已分发,办理追踪中0待签收（提醒签收、提醒交办按钮）,-2已申退(申退审核按钮))--已采编、办理追踪中显示
        private String strUrl;//详情页面的地址
        private String strHandlerType;//1主办，2会办

        public String getStrHandlerType() {
            return strHandlerType;
        }

        public String getStrPubId() {
            return strPubId;
        }

        public String getStrMasTitle() {
            return strMasTitle;
        }

        public String getStrIssue() {
            return strIssue;
        }

        public String getStrSource() {
            return strSource;
        }

        public String getStrDate() {
            return strDate;
        }

        public String getStrState() {
            return strState;
        }

        public String getStrUrl() {
            return strUrl;
        }

        public String getStrMemFeedBackState() {
            return strMemFeedBackState;
        }

        public String getIsShowThButton() {
            return isShowThButton;
        }

        public String getStrMotionCheckState() {
            return strMotionCheckState;
        }

        public String getStrReportDate() {
            return strReportDate;
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

        public String getStrHandlerTypeName() {
            return strHandlerTypeName;
        }

        public String getStrRecUnitName() {
            return strRecUnitName;
        }

        public String getStrHandlerLimitDate() {
            return strHandlerLimitDate;
        }

        public String getIntState() {
            return intState;
        }

        public String getStrDelayState() {
            return strDelayState;
        }
    }
}
