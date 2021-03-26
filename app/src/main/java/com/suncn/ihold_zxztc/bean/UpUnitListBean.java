package com.suncn.ihold_zxztc.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 承办单位列表
 */
public class UpUnitListBean extends BaseGlobal {
    private List<UpUnitBean> objList;

    public List<UpUnitBean> getObjList() {
        return objList;
    }

    public static class UpUnitBean implements Serializable {
        private String strName; // 机构名称
        private String strId;//搜索返回id
        private boolean isChecked;//是否选中
        private ArrayList<Unit> objChildList;


        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }

        public String getStrId() {
            return strId;
        }

        public String getStrName() {
            return strName;
        }

        public ArrayList<Unit> getObjChildList() {
            return objChildList;
        }
    }

    public class Unit {
        private String strId; // 机构id
        private String strName; // 机构名称
        private boolean isChecked;

        public String getStrId() {
            return strId;
        }

        public String getStrName() {
            return strName;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }
}
