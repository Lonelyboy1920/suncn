package com.suncn.ihold_zxztc.bean;

import java.util.List;

/**
 * @author :Sea
 * Date :2020-6-12 15:08
 * PackageName:com.suncn.ihold_zxztc.bean
 * Desc:提案列表的类型
 */
public class ProposalAllTypeServletBean extends BaseGlobal {
    private List<ObjListBean> objList;

    public List<ObjListBean> getObjList() {
        return objList;
    }

    public void setObjList(List<ObjListBean> objList) {
        this.objList = objList;
    }

    public class ObjListBean {
        private String strType;  //1所有提案 2办理中提案 3已办结提案 4立案提案 5不予立案提案 6重点提案 7优秀提案
        private String strName; //显示的名称
        private String intCount;   //数量

        public String getStrType() {
            return strType;
        }

        public void setStrType(String strType) {
            this.strType = strType;
        }

        public String getStrName() {
            return strName;
        }

        public void setStrName(String strName) {
            this.strName = strName;
        }

        public String getIntCount() {
            return intCount;
        }

        public void setIntCount(String intCount) {
            this.intCount = intCount;
        }
    }
}
