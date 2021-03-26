package com.suncn.ihold_zxztc.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author :Sea
 * Date :2020-7-1 16:36
 * PackageName:com.suncn.ihold_zxztc.bean
 * Desc:
 */
public class ListAllYearServletBean extends BaseGlobal {
    private List<ObjListBean> objList;

    public List<ObjListBean> getObjList() {
        return objList;
    }

    public void setObjList(List<ObjListBean> objList) {
        this.objList = objList;
    }

    public static class ObjListBean {
        /**
         * strYear : 2020
         * checked : true
         */

        private String strYear;
        private String checked;

        public String getStrYear() {
            return strYear;
        }

        public void setStrYear(String strYear) {
            this.strYear = strYear;
        }

        public String getChecked() {
            return checked;
        }

        public void setChecked(String checked) {
            this.checked = checked;
        }
    }
}
