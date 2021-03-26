package com.suncn.ihold_zxztc.bean;



import java.util.ArrayList;

public class StudyBean extends BaseGlobal {
    private ArrayList<study> objList;

    public ArrayList<study> getObjList() {
        return objList;
    }

    public class study {
        private String strId;
        private String strTitle;
        private String strImagePath;
        private String strFileType;
        private String intReadCount;
        private String strPubDate;
        private ArrayList<ObjFileBean> affix;
        private String strUrl;

        public String getStrUrl() {
            return strUrl;
        }

        public String getStrId() {
            return strId;
        }

        public String getStrTitle() {
            return strTitle;
        }

        public String getStrImagePath() {
            return strImagePath;
        }

        public String getStrFileType() {
            return strFileType;
        }

        public String getIntReadCount() {
            return intReadCount;
        }

        public String getStrPubDate() {
            return strPubDate;
        }

        public ArrayList<ObjFileBean> getAffix() {
            return affix;
        }


    }
}
