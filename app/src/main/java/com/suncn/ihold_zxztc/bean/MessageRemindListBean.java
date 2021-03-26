package com.suncn.ihold_zxztc.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 消息提醒
 */
public class MessageRemindListBean extends BaseGlobal {
    private int intNotReadCount;//未读的数量
    private ArrayList<MessageRemindBean> objList;

    public ArrayList<MessageRemindBean> getObjList() {
        return objList;
    }

    public int getIntNotReadCount() {
        return intNotReadCount;
    }

    public static class MessageRemindBean implements Serializable {
        private String strId;//消息主键id
        private String strAffirId;//关联业务id
        private String strMsgTypeName; //类型名称
        private String strMsgContent; // 内容
        private String strButtonName; // 按钮名称

        // strButtonJSFunction 目前只对委员操作进行判断 两种 提案 委员反馈 feedBackHandler 委员的联名 showCaseMotionAlly 提案附议确认 caseAllySupport
        // 直接跳到弹框附议列表 社情民意联名 showInfoALly 传入业务字段 strAffirId
        private String strButtonJSFunction;
        private String strDistanceTime; // 时间
        private String strAffirType; // 01 提案 02 会议 03 活动 04 社情民意 05 刊物 06 大会发言
        private String strMobileState; // 字段变更为strMobileState 0未读
        private String strUrl; // strUrl
        private String strName; // 会议活动名称
        private String strPlace; // 会议活动地点
        private String strStartDate; // 会议活动开始时间
        private String strEndDate; // 会议活动结束时间
        private String strAttendId;

        public String getStrAttendId() {
            return strAttendId;
        }

        public void setStrMobileState(String strMobileState) {
            this.strMobileState = strMobileState;
        }

        public String getStrName() {
            return strName;
        }

        public String getStrPlace() {
            return strPlace;
        }

        public String getStrStartDate() {
            return strStartDate;
        }

        public String getStrEndDate() {
            return strEndDate;
        }

        public String getStrUrl() {
            return strUrl;
        }

        public String getStrAffirType() {
            return strAffirType;
        }

        public String getStrId() {
            return strId;
        }

        public String getStrAffirId() {
            return strAffirId;
        }

        public String getStrMsgTypeName() {
            return strMsgTypeName;
        }

        public String getStrMsgContent() {
            return strMsgContent;
        }

        public String getStrButtonName() {
            return strButtonName;
        }

        public String getStrButtonJSFunction() {
            return strButtonJSFunction;
        }

        public String getStrDistanceTime() {
            return strDistanceTime;
        }

        public String getStrMobileState() {
            return strMobileState;
        }
    }
}
