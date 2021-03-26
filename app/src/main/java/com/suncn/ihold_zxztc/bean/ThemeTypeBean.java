package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

public class ThemeTypeBean extends BaseGlobal {
    private ArrayList<themeBean> objList;

    public ArrayList<themeBean> getObjList() {
        return objList;
    }

    public class themeBean {
       private String strTopicType;
       private String strTopicTypeName;
       private ArrayList<themeBean>objChildList;

        public ArrayList<themeBean> getObjChildList() {
            return objChildList;
        }

        public String getStrTopicType() {
            return strTopicType;
        }

        public String getStrTopicTypeName() {
            return strTopicTypeName;
        }
    }
}
