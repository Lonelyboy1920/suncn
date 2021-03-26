package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

public class OverSearchBean extends BaseGlobal {
    private ArrayList<OverSearchList> objList;

    public ArrayList<OverSearchList> getObjList() {
        return objList;
    }

    public class OverSearchList {
        private String strId;
        private String strTitle;
        private String strSorceTypeName;
        private String strSourceName;
        private String strReoprtDate;
        private String strUrl;
        private String strAbstractContent;
        private String  strSorceType;

        public String getStrSorceType() {
            return strSorceType;
        }

        public String getStrId() {
            return strId;
        }

        public String getStrTitle() {
            return strTitle;
        }

        public String getStrSorceTypeName() {
            return strSorceTypeName;
        }

        public String getStrSourceName() {
            return strSourceName;
        }

        public String getStrReoprtDate() {
            return strReoprtDate;
        }

        public String getStrUrl() {
            return strUrl;
        }

        public String getStrAbstractContent() {
            return strAbstractContent;
        }


    }
}
