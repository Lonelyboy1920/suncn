package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

public class BaseMeetBean extends BaseGlobal {
    private ArrayList<BaseMeet> objList;

    public ArrayList<BaseMeet> getObjList() {
        return objList;
    }

    public class BaseMeet {


        private String strTitle;
        private String strContent;
        private String strStartDate;
        private String strEndDate;
        private String strName;
        private String strId;

        public String getStrId() {
            return strId;
        }

        public String getStrName() {
            return strName;
        }

        public String getStrTitle() {
            return strTitle;
        }

        public String getStrContent() {
            return strContent;
        }

        public String getStrStartDate() {
            return strStartDate;
        }

        public String getStrEndDate() {
            return strEndDate;
        }

        public String getStrJointMember() {
            return strJointMember;
        }

        private String strJointMember;
    }
}
