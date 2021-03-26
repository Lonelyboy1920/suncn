package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

public class RideArrangeListBean extends BaseGlobal {
    private ArrayList<RideArrange> objList;

    public ArrayList<RideArrange> getObjList() {
        return objList;
    }

    public class RideArrange {

        /**
         * strTime : 15:00
         * strId : 664c06cace6b41dc8ccbac3ac057fe00
         * strName : 合肥市第几次大会
         * strMeetingPlace : 科创园
         * strDate : 2019-09-09
         * strCarPlace : 科学大道公交站
         */

        private String strTime;
        private String strId;
        private String strName;
        private String strMeetingPlace;
        private String strDate;
        private String strCarPlace;

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

        public String getStrMeetingPlace() {
            return strMeetingPlace;
        }

        public void setStrMeetingPlace(String strMeetingPlace) {
            this.strMeetingPlace = strMeetingPlace;
        }

        public String getStrDate() {
            return strDate;
        }

        public void setStrDate(String strDate) {
            this.strDate = strDate;
        }

        public String getStrCarPlace() {
            return strCarPlace;
        }

        public void setStrCarPlace(String strCarPlace) {
            this.strCarPlace = strCarPlace;
        }
    }
}
