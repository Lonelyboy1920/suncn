package com.suncn.ihold_zxztc.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 届次集合
 */
public class SessionListBean extends BaseGlobal {

    private ArrayList<SessionBean> objList;

    public ArrayList<SessionBean> getObjList() {
        return objList;
    }

    private ArrayList<YearInfo> objYearList;

    public ArrayList<YearInfo> getObjYearList() {
        return objYearList;
    }

    public class YearInfo implements Serializable {
        private String strYear;//年份
        private String checked;//true即为当前年份，用不到，默认第一条为当前年份

        public String getStrYear() {
            return strYear;
        }

        public String getChecked() {
            return checked;
        }
    }

    public class SessionBean implements Serializable {
        /**
         * strSessionCode : 1402
         * strSessionName : 本届
         * strSessionId : 1541991596095779
         */

        private String strSessionCode;
        private String strSessionName;
        private String strSessionId;

        public String getStrSessionCode() {
            return strSessionCode;
        }

        public void setStrSessionCode(String strSessionCode) {
            this.strSessionCode = strSessionCode;
        }

        public String getStrSessionName() {
            return strSessionName;
        }

        public void setStrSessionName(String strSessionName) {
            this.strSessionName = strSessionName;
        }

        public String getStrSessionId() {
            return strSessionId;
        }

        public void setStrSessionId(String strSessionId) {
            this.strSessionId = strSessionId;
        }
    }
}
