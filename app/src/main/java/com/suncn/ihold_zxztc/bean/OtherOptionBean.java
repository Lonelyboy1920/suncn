package com.suncn.ihold_zxztc.bean;

import java.util.List;

/**
 * Created by whh on 2018-10-10.
 */

public class OtherOptionBean extends BaseGlobal {
    private List<OtherOption> objList;
    private List<OtherOption> objOtherList;
    private List<OtherOption> objContentList;

    public List<OtherOption> getObjOtherList() {
        return objOtherList;
    }

    public void setObjOtherList(List<OtherOption> objOtherList) {
        this.objOtherList = objOtherList;
    }

    public List<OtherOption> getObjContentList() {
        return objContentList;
    }

    public void setObjContentList(List<OtherOption> objContentList) {
        this.objContentList = objContentList;
    }

    public List<OtherOption> getObjList() {
        return objList;
    }

    public class OtherOption {
        private String strName;//显示的值
        private String strKey;//键
        private String strValue;//0-不选中，1-选中
        private int intCount;

        public int getIntCount() {
            return intCount;
        }

        public void setIntCount(int intCount) {
            this.intCount = intCount;
        }

        public String getStrName() {
            return strName;
        }

        public String getStrKey() {
            return strKey;
        }

        public String getStrValue() {
            return strValue;
        }
    }
}
