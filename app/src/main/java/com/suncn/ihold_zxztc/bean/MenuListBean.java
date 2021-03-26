package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

public class MenuListBean extends BaseGlobal {
    private ArrayList<MenuBean> objList;

    public ArrayList<MenuBean> getObjList() {
        return objList;
    }

    public class MenuBean {

        /**
         * strStarTime : 10:00
         * strEndTime : 10:30
         * strId : ba85513f1de74cd597800a32d6aae50d
         * dtEatTime : 2019-09-10
         * strMenu : 包子油条
         * strStatus : 1
         */

        private String strStarTime;
        private String strEndTime;
        private String strId;
        private String dtEatTime;
        private String strMenu;
        private String strStatus;
        private String strQZMenu;

        public String getStrQZMenu() {
            return strQZMenu;
        }

        public String getStrStarTime() {
            return strStarTime;
        }

        public void setStrStarTime(String strStarTime) {
            this.strStarTime = strStarTime;
        }

        public String getStrEndTime() {
            return strEndTime;
        }

        public void setStrEndTime(String strEndTime) {
            this.strEndTime = strEndTime;
        }

        public String getStrId() {
            return strId;
        }

        public void setStrId(String strId) {
            this.strId = strId;
        }

        public String getDtEatTime() {
            return dtEatTime;
        }

        public void setDtEatTime(String dtEatTime) {
            this.dtEatTime = dtEatTime;
        }

        public String getStrMenu() {
            return strMenu;
        }

        public void setStrMenu(String strMenu) {
            this.strMenu = strMenu;
        }

        public String getStrStatus() {
            return strStatus;
        }

        public void setStrStatus(String strStatus) {
            this.strStatus = strStatus;
        }
    }
}
