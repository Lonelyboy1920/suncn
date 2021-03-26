package com.suncn.ihold_zxztc.bean;

import java.util.List;

/**
 * 联名人、附议人集合
 */
public class JoinSupportListBean extends BaseGlobal {

    private List<JoinSupportBean> objList;

    public List<JoinSupportBean> getObjList() {
        return objList;
    }

    public void setObjList(List<JoinSupportBean> objList) {
        this.objList = objList;
    }

    public static class JoinSupportBean {
        /**strId:abcdefdsdsfdfdadf
         * strAllyUserName : 王洵良
         * strAttendName : 已附议
         * intAttend : 1
         * strReportDate : 2019-04-09
         * strReason : sssssss
         */

        private String strId;//id
        private String strAllyUserName;//联名人或附议人
        private String strAttendName;//状态
        private int intAttend;//0 不同意 弹出具体意见 当intAttend为2、并且strType为3附议时 附议确认按钮可以点击操作
        private String strReportDate;//联名或附议的提交时间
        private String strReason;//不同意时返回的具体意见 可以做到点击弹出具体意见
        private String strShowReason;

        public String getStrShowReason() {
            return strShowReason;
        }

        public String getStrId() {
            return strId;
        }

        public String getStrAllyUserName() {
            return strAllyUserName;
        }

        public void setStrAllyUserName(String strAllyUserName) {
            this.strAllyUserName = strAllyUserName;
        }

        public String getStrAttendName() {
            return strAttendName;
        }

        public void setStrAttendName(String strAttendName) {
            this.strAttendName = strAttendName;
        }

        public int getIntAttend() {
            return intAttend;
        }

        public void setIntAttend(int intAttend) {
            this.intAttend = intAttend;
        }

        public String getStrReportDate() {
            return strReportDate;
        }

        public void setStrReportDate(String strReportDate) {
            this.strReportDate = strReportDate;
        }

        public String getStrReason() {
            return strReason;
        }

        public void setStrReason(String strReason) {
            this.strReason = strReason;
        }
    }
}
