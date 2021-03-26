package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

public class UnitListBean extends BaseTypeBean {
    private ArrayList<UnitList> objList;
    private String strRootUnitName;

    public String getStrRootUnitName() {
        return strRootUnitName;
    }

    public ArrayList<UnitList> getObjList() {
        return objList;
    }

    public class UnitList {
        private String strUnit_id;
        private String strUnit_name;
        private String strUnit_parentId;
        private String strUnit_IconPathUrl;
        private ArrayList<UnitList>objUnits_list;
        private String strUnit_BackGroundPathUrl;

        public String getStrUnit_BackGroundPathUrl() {
            return strUnit_BackGroundPathUrl;
        }

        public ArrayList<UnitList> getObjUnits_list() {
            return objUnits_list;
        }

        public String getStrUnit_id() {
            return strUnit_id;
        }

        public void setStrUnit_id(String strUnit_id) {
            this.strUnit_id = strUnit_id;
        }

        public String getStrUnit_name() {
            return strUnit_name;
        }

        public void setStrUnit_name(String strUnit_name) {
            this.strUnit_name = strUnit_name;
        }

        public String getStrUnit_parentId() {
            return strUnit_parentId;
        }

        public void setStrUnit_parentId(String strUnit_parentId) {
            this.strUnit_parentId = strUnit_parentId;
        }

        public String getStrUnit_IconPathUrl() {
            return strUnit_IconPathUrl;
        }

        public void setStrUnit_IconPathUrl(String strUnit_IconPathUrl) {
            this.strUnit_IconPathUrl = strUnit_IconPathUrl;
        }

        public String getIntCount() {
            return intCount;
        }

        public void setIntCount(String intCount) {
            this.intCount = intCount;
        }

        private String intCount;//显示订阅操作 游客以及其他非新闻资讯无此项操作 0 未订阅 1已订阅


    }
}
