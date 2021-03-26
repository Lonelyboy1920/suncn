package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

public class HYRCListBean extends BaseGlobal {
    private ArrayList<HYRCList> objList;

    public ArrayList<HYRCList> getObjList() {
        return objList;
    }
    public class HYRCList{

        /**
         * strTime : 17:00
         * strId : 97c0d087b23e4644a5dd1805e8d9a7b8
         * strName : 第一次会议
         * strDate : 2019-09-09
         * strPlace : 商信
         * strContent : 开会啦
         */

        private String strTime;
        private String strId;
        private String strName;
        private String strDate;
        private String strPlace;
        private String strContent;
        private String strUrl;

        public String getStrUrl() {
            return strUrl;
        }

        public String getStrTime() {
            return strTime;
        }

        public void setStrTime(String strTime) {
            this.strTime = strTime;
        }

        public String getStrId() {
            return strId;
        }

        public void setStrId(String strId) {
            this.strId = strId;
        }

        public String getStrName() {
            return strName;
        }

        public void setStrName(String strName) {
            this.strName = strName;
        }

        public String getStrDate() {
            return strDate;
        }

        public void setStrDate(String strDate) {
            this.strDate = strDate;
        }

        public String getStrPlace() {
            return strPlace;
        }

        public void setStrPlace(String strPlace) {
            this.strPlace = strPlace;
        }

        public String getStrContent() {
            return strContent;
        }

        public void setStrContent(String strContent) {
            this.strContent = strContent;
        }
    }
}
