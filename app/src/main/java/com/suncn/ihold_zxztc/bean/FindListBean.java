package com.suncn.ihold_zxztc.bean;

import java.util.ArrayList;

/**
 * Created by whh on 2018-6-5.
 * 发现
 */

public class FindListBean extends BaseGlobal {

    private ArrayList<Find> objList;

    public ArrayList<Find> getObjList() {
        return objList;
    }

    public class Find{
        private String strId;//id
        private String strUrl;//查看Url
        private String strTitle;//标题
       // private String strSourceName;//来源人
        private String strTypeName;//类别名称
        private String strReportDate;//日期（格式为：xxxx-xx-xx）

        public String getStrId() {
            return strId;
        }

        public String getStrUrl() {
            return strUrl;
        }

        public String getStrTitle() {
            return strTitle;
        }

       /* public String getStrSourceName() {
            return strSourceName;
        }
*/
        public String getStrTypeName() {
            return strTypeName;
        }

        public String getStrReportDate() {
            return strReportDate;
        }
    }
}
