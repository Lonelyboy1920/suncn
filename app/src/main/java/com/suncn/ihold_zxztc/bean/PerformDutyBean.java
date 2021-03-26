package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

public class PerformDutyBean extends BaseGlobal {

    private String strFaction;
    private String strAllScore;
    private String strName;
    private String strLzGrade;// 0-显示履职等级，1-显示履职等分， 2-不显示
    private String strPathUrl;
    private String strGradeName;
    private String strSector;
    private String strUserId;
    private String strPhotoUrl;

    public String getStrLzGrade() {
        return strLzGrade;
    }

    private ArrayList<DutyList> objList;

    public String getStrFaction() {
        return strFaction;
    }

    public String getStrAllScore() {
        return strAllScore;
    }

    public String getStrName() {
        return strName;
    }

    public String getStrPathUrl() {
        return strPathUrl;
    }


    public String getStrGradeName() {
        return strGradeName;
    }

    public String getStrSector() {
        return strSector;
    }

    public String getStrUserId() {
        return strUserId;
    }

    public String getStrPhotoUrl() {
        return strPhotoUrl;
    }


    public ArrayList<DutyList> getObjList() {
        return objList;
    }


    public class DutyList {
        private String strId;
        private String strTitle;
        private String strDate;
        private String strStateName;
        private ArrayList<DutyList> objOneList;
        private ArrayList<DutyList> childList;
        private String strType;
        private String strUrl;
        private String strRecordType;
        private String strSourceType;

        public String getStrSourceType() {
            return strSourceType;
        }

        public String getStrRecordType() {
            return strRecordType;
        }

        public String getStrUrl() {
            return strUrl;
        }

        public String getStrType() {
            return strType;
        }

        public String getStrId() {
            return strId;
        }

        public String getStrTitle() {
            return strTitle;
        }

        public String getStrDate() {
            return strDate;
        }

        public String getStrStateName() {
            return strStateName;
        }

        public ArrayList<DutyList> getObjOneList() {
            return objOneList;
        }

        public ArrayList<DutyList> getChildList() {
            return childList;
        }

    }
}
